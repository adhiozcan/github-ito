package id.net.iconpln.apps.ito.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.ItoApplication;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.model.FlagTusbung;
import id.net.iconpln.apps.ito.model.ProgressUpdateEvent;
import id.net.iconpln.apps.ito.model.RefreshEvent;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.PingEvent;
import id.net.iconpln.apps.ito.ui.fragment.ItoDialog;
import id.net.iconpln.apps.ito.utility.CameraUtils;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.ConnectivityUtils;
import id.net.iconpln.apps.ito.utility.DateUtils;
import id.net.iconpln.apps.ito.utility.ImageUtils;
import id.net.iconpln.apps.ito.utility.SignalListener;
import id.net.iconpln.apps.ito.utility.SmileyLoading;
import id.net.iconpln.apps.ito.utility.StringUtils;
import io.realm.Realm;

/**
 * Created by Ozcan on 30/03/2017.
 */

public class PemutusanActivity extends AppCompatActivity
        implements SignalListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = PemutusanActivity.class.getSimpleName();

    private static final int DO_OFFLINE            = 0;
    private static final int DO_ONLINE             = 1;
    public static final  int CAMERA_REQUEST_CODE_1 = 101;
    public static final  int CAMERA_REQUEST_CODE_2 = 102;
    public static final  int CAMERA_REQUEST_CODE_3 = 103;
    public static final  int CAMERA_REQUEST_CODE_4 = 104;

    private static int REQUEST_TAKE_PHOTO = 0;

    private Spinner spnStatus;
    private Spinner spnKeterangan;

    private TextView txtNoTul;
    private TextView txtIdPelanggan;
    private TextView txtNama;
    private TextView txtAlamat;
    private TextView txtTanggal;

    private EditText edTanggalPutus;
    private EditText edLwbp;
    private EditText edWbp;
    private EditText edKvarh;

    private ImageView imgFoto1;
    private ImageView imgFoto2;
    private ImageView imgFoto3;
    private ImageView imgFoto4;

    private GoogleApiClient mGoogleClient;
    private LocationRequest mLocationRequest;

    private String mCurrentPhotoPath;
    private Uri[] mPhotoPath = new Uri[4];

    private List<FlagTusbung> mFlagTusbung;
    private WorkOrder         mWo;

    private int mStatusSignal = ConnectivityUtils.SEARCHING;

    private Tusbung tusbung = new Tusbung();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_progress);
        CommonUtils.installToolbar(this);
        initView();

        // -- listening signal strength
        if (ConnectivityUtils.isHaveInternetConnection(this)) {
            ConnectivityUtils.register(this, this);
        }

        mGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        List<String> keteranganList = new ArrayList<>();
        mFlagTusbung = new ArrayList<>();
        mFlagTusbung.addAll(getDataMasterTusbungFromLocal());
        for (FlagTusbung flagTusbung : mFlagTusbung) {
            String keterangan = StringUtils.normalize(flagTusbung.getKeterangan());
            keteranganList.add(keterangan);
        }
        ArrayAdapter userAdapter = new ArrayAdapter(this, R.layout.adapter_item_spinner_keterangan, keteranganList);
        spnKeterangan.setAdapter(userAdapter);
    }

    private void initView() {
        spnStatus = (Spinner) findViewById(R.id.status_pekerjaan);
        spnKeterangan = (Spinner) findViewById(R.id.keterangan);
        txtNoTul = (TextView) findViewById(R.id.nomor_tul);
        txtIdPelanggan = (TextView) findViewById(R.id.pelanggan_id);
        txtNama = (TextView) findViewById(R.id.pelanggan_nama);
        txtAlamat = (TextView) findViewById(R.id.pelanggan_alamat);
        txtTanggal = (TextView) findViewById(R.id.tanggal_tul);
        edTanggalPutus = (EditText) findViewById(R.id.tanggal_putus);
        edLwbp = (EditText) findViewById(R.id.lwbp);
        edWbp = (EditText) findViewById(R.id.wbp);
        edKvarh = (EditText) findViewById(R.id.kvarh);
        imgFoto1 = (ImageView) findViewById(R.id.foto_1);
        imgFoto2 = (ImageView) findViewById(R.id.foto_2);
        imgFoto3 = (ImageView) findViewById(R.id.foto_3);
        imgFoto4 = (ImageView) findViewById(R.id.foto_4);
    }

    /**
     * Update customer information.
     *
     * @param wo
     */
    private void updateInfoPelangganDisplay(WorkOrder wo) {
        this.mWo = wo;
        txtNoTul.setText(wo.getNoTul601());
        txtIdPelanggan.setText(wo.getPelangganId());
        txtNama.setText(wo.getNama());
        txtAlamat.setText(wo.getAlamat());
        txtTanggal.setText(wo.getTanggalWo());
    }

    private List<FlagTusbung> getDataMasterTusbungFromLocal() {
        Realm realm = Realm.getDefaultInstance();
        return realm.copyFromRealm(realm.where(FlagTusbung.class).findAll());
    }

    public void takePicture(View viewId) {
        if (CameraUtils.canOpenCamera(this))
            switch (viewId.getId()) {
                case R.id.foto_1:
                    REQUEST_TAKE_PHOTO = CAMERA_REQUEST_CODE_1;
                    dispatchTakePictureIntent();
                    break;
                case R.id.foto_2:
                    REQUEST_TAKE_PHOTO = CAMERA_REQUEST_CODE_2;
                    dispatchTakePictureIntent();
                    break;
                case R.id.foto_3:
                    REQUEST_TAKE_PHOTO = CAMERA_REQUEST_CODE_3;
                    dispatchTakePictureIntent();
                    break;
                case R.id.foto_4:
                    REQUEST_TAKE_PHOTO = CAMERA_REQUEST_CODE_4;
                    dispatchTakePictureIntent();
                    break;
            }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "dispatchTakePictureIntent: ", ex);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp     = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String imageFileName = "ITO_" + timeStamp + "_";
        File   storageDir    = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        switch (REQUEST_TAKE_PHOTO) {
            case CAMERA_REQUEST_CODE_1:
                mPhotoPath[0] = Uri.fromFile(image);
                break;
            case CAMERA_REQUEST_CODE_2:
                mPhotoPath[1] = Uri.fromFile(image);
                break;
            case CAMERA_REQUEST_CODE_3:
                mPhotoPath[2] = Uri.fromFile(image);
                break;
            case CAMERA_REQUEST_CODE_4:
                mPhotoPath[3] = Uri.fromFile(image);
                break;
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap imageBitmap = CameraUtils.uriToBitmap(this, Uri.parse("file://" + mCurrentPhotoPath));
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, 150, 150);
            switch (requestCode) {
                case CAMERA_REQUEST_CODE_1:
                    hapusFoto(R.id.foto_1);
                    imgFoto1.setImageBitmap(imageBitmap);
                    imgFoto1.setTag("Flag to be post!");
                    break;
                case CAMERA_REQUEST_CODE_2:
                    hapusFoto(R.id.foto_2);
                    imgFoto2.setImageBitmap(imageBitmap);
                    imgFoto2.setTag("Flag to be post!");
                    break;
                case CAMERA_REQUEST_CODE_3:
                    hapusFoto(R.id.foto_3);
                    imgFoto3.setImageBitmap(imageBitmap);
                    imgFoto3.setTag("Flag to be post!");
                    break;
                case CAMERA_REQUEST_CODE_4:
                    hapusFoto(R.id.foto_4);
                    imgFoto4.setImageBitmap(imageBitmap);
                    imgFoto4.setTag("Flag to be post!");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBusProvider.getInstance().register(this);
        mGoogleClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleClient.disconnect();
        super.onStop();
        EventBusProvider.getInstance().unregister(this);
        ConnectivityUtils.unregister(this);
    }

    private void doPemutusan(int typeWork) {
        SmileyLoading.show(this, "Sedang memproses tusbung");

        String noTul        = txtNoTul.getText().toString();
        String noTul601     = mWo.getNoTul601();
        String noWo         = mWo.getNoWo();
        String idpel        = mWo.getPelangganId();
        String nama         = mWo.getNama();
        String alamat       = mWo.getAlamat();
        String unitUpId     = mWo.getUnitUp();
        String kodePetugas  = mWo.getKodePetugas();
        String selectedVal  = getResources().getStringArray(R.array.jenis_pekerjaan_val)[spnStatus.getSelectedItemPosition()];
        String lwbp         = edLwbp.getText().toString();
        String wbp          = edWbp.getText().toString();
        String kvarh        = edKvarh.getText().toString();
        String isGagalPutus = "0";
        String status       = mFlagTusbung.get(spnKeterangan.getSelectedItemPosition()).getKode();
        String namaPetugas  = mWo.getNama();
        String latitude     = "";
        String longitude    = "";
        String tanggalPutus = "";
        if (ItoApplication.pingDate.isEmpty()) {
            tanggalPutus = edTanggalPutus.getText().toString();
        } else {
            tanggalPutus = ItoApplication.pingDate;
        }

        Uri[] fotoTobePosted = new Uri[4];
        int   jumlahFoto     = 0;
        for (Uri uri : mPhotoPath) {
            if (uri != null) {
                fotoTobePosted[jumlahFoto] = uri;
                jumlahFoto++;
            }
        }

        tusbung.setKodePetugas(kodePetugas);
        tusbung.setNamaPetugas(namaPetugas);
        tusbung.setPelangganId(idpel);
        tusbung.setAlamat(alamat);
        tusbung.setNamaPelanggan(nama);
        tusbung.setUnitUpId(unitUpId);
        tusbung.setNoTul(noTul601);
        tusbung.setNoWo(noWo);
        tusbung.setTanggalPemutusan(tanggalPutus);
        tusbung.setStandLWBP(lwbp);
        tusbung.setStandWBP(wbp);
        tusbung.setStandKVARH(kvarh);
        tusbung.setGagalPutus(isGagalPutus);
        tusbung.setStatus(status);
        tusbung.setJumlahFoto(String.valueOf(jumlahFoto));

        if (fotoTobePosted[0] != null) tusbung.setPhotoPath1(fotoTobePosted[0].toString());
        if (fotoTobePosted[1] != null) tusbung.setPhotoPath2(fotoTobePosted[1].toString());
        if (fotoTobePosted[2] != null) tusbung.setPhotoPath3(fotoTobePosted[2].toString());
        if (fotoTobePosted[3] != null) tusbung.setPhotoPath4(fotoTobePosted[3].toString());

        if (typeWork == DO_ONLINE) {
            Log.d(TAG, "[DO ONLINE : Simpan di penyimpanan server]");
            int part = 1;
            for (int i = 0; i < jumlahFoto; i++) {
                tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(this, fotoTobePosted[i]));
                tusbung.setPart(String.valueOf(part));

                AppConfig.TUSBUNG = tusbung;
                SocketTransaction.prepareStatement().sendMessage(ParamDef.DO_TUSBUNG);
                part++;
            }
        }

        // mark wo to be pending and will be uploaded later
        if (typeWork == DO_OFFLINE) {
            Log.d(TAG, "[DO OFFLINE : Simpan di penyimpanan local]");

            Realm realm = Realm.getDefaultInstance();
            if (!realm.isInTransaction())
                realm.beginTransaction();
            realm.where(WorkOrder.class).equalTo("noWo", mWo.getNoWo()).findFirst().setUploaded(false);
            realm.insert(tusbung);
            if (realm.isInTransaction())
                realm.commitTransaction();

            SmileyLoading.close();

            Snackbar snackbar = Snackbar.make(findViewById(R.id.container_layout),
                    "[Offline Mode] Tusbung tersimpan di local.",
                    Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.material_pink));
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    EventBusProvider.getInstance().post(new RefreshEvent());
                    finish();
                }
            });
            snackbar.show();
        }

        // mark mWo has done.
        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction())
            realm.beginTransaction();
        realm.where(WorkOrder.class).equalTo("noWo", mWo.getNoWo()).findFirst().setSelesai(true);
        realm.insert(tusbung);
        if (realm.isInTransaction())
            realm.commitTransaction();

        Log.d(TAG, "doPemutusan: [Param]--------------------------------------------------------");
        Log.d(TAG, "doPemutusan: " + tusbung.toString());
    }

    public void onProsesButtonClicked(View btnId) {
        ItoDialog.simpleAlert(this, "Apakah Anda yakin akan memproses tusbung Work Order ini?", new ItoDialog.Action() {
            @Override
            public void onYesButtonClicked() {
                if (cekFieldContent()) {
                    if (mStatusSignal == ConnectivityUtils.SIGNAL_WEAK ||
                            mStatusSignal == ConnectivityUtils.SIGNAL_STRONG) {
                        doPemutusan(DO_ONLINE);
                    } else {
                        doPemutusan(DO_OFFLINE);
                    }
                }
            }

            @Override
            public void onNoButtonClicked() {
            }
        });

    }

    private boolean cekFieldContent() {
        if (edLwbp.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "No. LWBP Wajib diisi", Toast.LENGTH_SHORT).show();
            return false;
        }

        int photoNumber = 0;
        for (Uri photoPath : mPhotoPath) {
            if (photoPath != null) photoNumber++;
        }
        if (photoNumber == 0) {
            Toast.makeText(this, "Tidak ada bukti foto yang terlampir", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onBtnHapusClicked(View btnId) {
        hapusFoto(btnId.getId());
    }

    private void hapusFoto(int id) {
        switch (id) {
            case R.id.hapus_foto_1:
                if (mPhotoPath[0] == null) return;
                new File(mPhotoPath[0].getPath()).delete();
                imgFoto1.setImageResource(R.drawable.camera_b);
                mPhotoPath[0] = null;
                break;
            case R.id.hapus_foto_2:
                if (mPhotoPath[1] == null) return;
                new File(mPhotoPath[1].getPath()).delete();
                imgFoto1.setImageResource(R.drawable.camera_b);
                mPhotoPath[1] = null;
                break;
            case R.id.hapus_foto_3:
                if (mPhotoPath[2] == null) return;
                new File(mPhotoPath[2].getPath()).delete();
                imgFoto1.setImageResource(R.drawable.camera_b);
                mPhotoPath[2] = null;
                break;
            case R.id.hapus_foto_4:
                if (mPhotoPath[3] == null) return;
                new File(mPhotoPath[3].getPath()).delete();
                imgFoto1.setImageResource(R.drawable.camera_b);
                mPhotoPath[3] = null;
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEventReceived(WorkOrder workOrder) {
        updateInfoPelangganDisplay(workOrder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventReceived(PingEvent pingEvent) {
        System.out.println("[PING] -----------------------");
        System.out.println(pingEvent.getDate());
        Date   date       = DateUtils.parseToDate(pingEvent.getDate());
        String dateString = DateUtils.parseToString(date);
        ItoApplication.pingDate = dateString;
        updateTanggalDisplay(dateString);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTusbungRespond(ProgressUpdateEvent progressEvent) {
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, progressEvent.toString());

        SmileyLoading.close();
        if (progressEvent.kode == "1") {
            // mark mWo has done.
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WorkOrder.class).equalTo("noWo", mWo.getNoWo()).findFirst().setUploaded(true);
            realm.commitTransaction();

            Snackbar snackbar = Snackbar.make(findViewById(R.id.container_layout),
                    "Tusbung berhasil dikerjakan dan terunggah ke server",
                    Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.material_light_green));
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    EventBusProvider.getInstance().post(new RefreshEvent());
                    finish();
                }
            });
            snackbar.show();
        } else {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WorkOrder.class).equalTo("noWo", mWo.getNoWo()).findFirst().setUploaded(false);
            realm.commitTransaction();

            Snackbar snackbar = Snackbar.make(findViewById(R.id.container_layout),
                    StringUtils.normalize(progressEvent.message),
                    Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(PemutusanActivity.this, R.color.material_pink));
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    EventBusProvider.getInstance().post(new RefreshEvent());
                    finish();
                }
            });
            snackbar.show();
        }
    }

    private void updateTanggalDisplay(String date) {
        edTanggalPutus.setText(date);
    }

    private void updateSignalColorBar(int color) {
        findViewById(R.id.signal_condition).setBackgroundResource(color);
    }

    @Override
    public void onReceived(int strength) {
        System.out.println(strength);
        mStatusSignal = strength;
        switch (strength) {
            case ConnectivityUtils.DISCONNECT:
                updateSignalColorBar(R.color.material_pink);
                break;
            case ConnectivityUtils.SIGNAL_WEAK:
                updateSignalColorBar(R.color.material_orange);
                break;
            case ConnectivityUtils.SIGNAL_STRONG:
                updateSignalColorBar(R.color.material_light_green);
                break;
            default:
                updateSignalColorBar(R.color.colorGrey);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        tusbung.setLatitude(String.valueOf(location.getLatitude()));
        tusbung.setLongitude(String.valueOf(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "onStatusChanged: GoogleApiClient connection has been changed");
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: GoogleApiClient connection has failed");
    }
}
