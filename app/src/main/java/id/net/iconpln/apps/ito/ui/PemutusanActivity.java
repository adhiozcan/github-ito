package id.net.iconpln.apps.ito.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.ItoApplication;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.ConnectionListener;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.model.FlagTusbung;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.NetworkAvailabilityEvent;
import id.net.iconpln.apps.ito.model.eventbus.ProgressUpdateEvent;
import id.net.iconpln.apps.ito.model.eventbus.RefreshEvent;
import id.net.iconpln.apps.ito.model.eventbus.TempUploadEvent;
import id.net.iconpln.apps.ito.model.eventbus.WoUploadServiceEvent;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.PingEvent;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.ui.fragment.ItoDialog;
import id.net.iconpln.apps.ito.utility.CameraUtils;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.ConnectivityUtils;
import id.net.iconpln.apps.ito.utility.DateUtils;
import id.net.iconpln.apps.ito.utility.ImageUtils;
import id.net.iconpln.apps.ito.utility.NotifUtils;
import id.net.iconpln.apps.ito.utility.SmileyLoading;
import id.net.iconpln.apps.ito.utility.StringUtils;
import io.realm.Realm;

/**
 * Created by Ozcan on 30/03/2017.
 */

public class PemutusanActivity extends AppCompatActivity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = PemutusanActivity.class.getSimpleName();

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int DO_OFFLINE                            = 0;
    private static final int DO_ONLINE                             = 1;
    public static final  int CAMERA_REQUEST_CODE_1                 = 101;
    public static final  int CAMERA_REQUEST_CODE_2                 = 102;
    public static final  int CAMERA_REQUEST_CODE_3                 = 103;
    public static final  int CAMERA_REQUEST_CODE_4                 = 104;

    private final float MINIMUM_ACCURACY_ACCEPTED = 20.0f;
    private final float NO_ACCURACY_ACCEPTED      = 0.0f;

    private boolean reachMinimumLocationAccuracy = false;

    private static int REQUEST_TAKE_PHOTO = 0;

    private Spinner spnPutus;
    private Spinner spnKeterangan;

    private TextView txtNoTul;
    private TextView txtIdPelanggan;
    private TextView txtNama;
    private TextView txtAlamat;
    private TextView txtTanggal;
    private EditText edLokasi;
    private EditText edTanggalPutus;
    private EditText edLwbp;
    private EditText edWbp;
    private EditText edKvarh;
    private EditText edEmail;
    private EditText edHp;

    private ImageView imgFoto1;
    private ImageView imgFoto2;
    private ImageView imgFoto3;
    private ImageView imgFoto4;

    private GoogleApiClient mGoogleClient;
    private LocationRequest mLocationRequest;
    private ArrayAdapter    keteranganAdapter;

    private List<FlagTusbung> mFlagTusbung;
    private List<String>      keteranganList;
    private List<String>      putusList;
    private List<String>      gagalPutusList;

    private WorkOrder mWo;
    private boolean   isTusbungUlang;
    private String    mCurrentPhotoPath;
    private Uri[] mPhotoPath     = new Uri[4];
    private Uri[] fotoTobePosted = new Uri[4];

    private FusedLocationProviderApi mFusedLocationClient;
    private Tusbung tusbung = new Tusbung();

    private boolean wajibIsiLwbp = true;

    private void getDataFromIntent() {
        if (getIntent().getExtras() != null) {
            isTusbungUlang = getIntent().getExtras().getBoolean("tusbung_ulang");
            Log.d(TAG, "getDataFromIntent: " + isTusbungUlang);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_progress);
        CommonUtils.installToolbar(this);
        getDataFromIntent();

        initView();
        populateSpinnerListItem();

        mFusedLocationClient = LocationServices.FusedLocationApi;
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (ConnectivityUtils.isHaveInternetConnection(this)) {
            updateSignalColorBar(R.color.material_light_green);
        } else {
            updateSignalColorBar(R.color.material_pink);
        }

        ArrayAdapter statusAdapter = ArrayAdapter.createFromResource(this, R.array.status_putus,
                R.layout.adapter_item_spinner_status);
        statusAdapter.setDropDownViewResource(R.layout.adapter_item_spinner_keterangan);
        spnPutus.setAdapter(statusAdapter);
        spnPutus.setSelection(0);
        spnPutus.setOnItemSelectedListener(this);

        keteranganAdapter = new ArrayAdapter(this,
                R.layout.adapter_item_spinner_keterangan, keteranganList);
        keteranganAdapter.setDropDownViewResource(R.layout.adapter_item_spinner_keterangan);
        spnKeterangan.setAdapter(keteranganAdapter);
        spnKeterangan.setOnItemSelectedListener(this);

        updateTanggalDisplay(DateUtils.getDateFromLogin());
    }

    private void initView() {
        spnPutus = (Spinner) findViewById(R.id.status_putus);
        spnKeterangan = (Spinner) findViewById(R.id.keterangan);
        txtNoTul = (TextView) findViewById(R.id.nomor_tul);
        txtIdPelanggan = (TextView) findViewById(R.id.pelanggan_id);
        txtNama = (TextView) findViewById(R.id.pelanggan_nama);
        txtAlamat = (TextView) findViewById(R.id.pelanggan_alamat);
        txtTanggal = (TextView) findViewById(R.id.tanggal_tul);
        edLokasi = (EditText) findViewById(R.id.lokasi);
        edTanggalPutus = (EditText) findViewById(R.id.tanggal_putus);
        edLwbp = (EditText) findViewById(R.id.lwbp);
        edWbp = (EditText) findViewById(R.id.wbp);
        edKvarh = (EditText) findViewById(R.id.kvarh);
        edEmail = (EditText) findViewById(R.id.email_pelanggan);
        edHp = (EditText) findViewById(R.id.no_telp_pelanggan);
        imgFoto1 = (ImageView) findViewById(R.id.foto_1);
        imgFoto2 = (ImageView) findViewById(R.id.foto_2);
        imgFoto3 = (ImageView) findViewById(R.id.foto_3);
        imgFoto4 = (ImageView) findViewById(R.id.foto_4);
    }

    private void populateSpinnerListItem() {
        mFlagTusbung = new ArrayList<>();
        mFlagTusbung.add(new FlagTusbung("-", "Pilih keterangan pemutusan", "Pilih keterangan pemutusan"));
        mFlagTusbung.addAll(getDataMasterTusbungFromLocal());

        keteranganList = new ArrayList<>();
        putusList = new ArrayList<>();
        gagalPutusList = new ArrayList<>();

        for (FlagTusbung flagTusbung : mFlagTusbung) {
            String keterangan = StringUtils.normalize(flagTusbung.getKeterangan());
            String deskripsi  = StringUtils.normalize(flagTusbung.getDeskripsi());

            if (deskripsi.equals("Gagal Putus")) {
                gagalPutusList.add(keterangan);
                Log.d(TAG, "onCreate: [Gagal Putus]\t" + flagTusbung.toString());
            } else if (deskripsi.equals("Putus")) {
                putusList.add(keterangan);
                Log.d(TAG, "onCreate: [Putus]\t" + flagTusbung.toString());
            }
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Cannot access location, check your permission", Toast.LENGTH_LONG).show();
        }

        mFusedLocationClient.requestLocationUpdates(mGoogleClient, mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mGoogleClient, this);
        System.out.println("Stop receiving location updates");
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
        return LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance()
                        .where(FlagTusbung.class)
                        .findAll()
        );
    }

    public void takePicture(final View viewId) {
        if (CameraUtils.canOpenCamera(this)) {

            // chek location accuracy first for watermark location tagging validation
            if (!reachMinimumLocationAccuracy) {
                ItoDialog.simpleAlert(this, "Koordinat lokasi belum akurat.\n\n" +
                        "Tetap melanjutkan pengambilan foto?", new ItoDialog.Action() {
                    @Override
                    public void onYesButtonClicked() {
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

                    @Override
                    public void onNoButtonClicked() {
                    }
                });
            } else {
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
        }

    }

    private void dispatchTakePictureIntent() {
        /*if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

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
                takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        //File storageDir = Environment.getExternalStorageDirectory();
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

    private Bitmap addWatermark(Bitmap imageBitmap) {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria        criteria        = new Criteria();
        String          provider        = locationManager.getBestProvider(criteria, true);
        Location        location        = locationManager.getLastKnownLocation(provider);

        String kodePetugas = AppConfig.getUserInformation().getKodePetugas();
        String tanggal     = DateUtils.parseToString(new Date());
        String latitude    = "";
        String longitude   = "";
        if (tusbung.getLatitude() != null && tusbung.getLongitude() != null) {
            latitude = tusbung.getLatitude();
            longitude = tusbung.getLongitude();
        } else if (location != null) {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }
        imageBitmap = ImageUtils.addWatermark(imageBitmap, kodePetugas, tanggal, latitude, longitude);
        ImageUtils.saveImage(mCurrentPhotoPath, imageBitmap);
        return imageBitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Special treatment for Samsung Galaxy Tablet Device
        if (data == null) {
            Bitmap imageBitmap = CameraUtils.uriToBitmap(this, Uri.parse("file://" + mCurrentPhotoPath));
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, 150, 150);
            switch (requestCode) {
                case CAMERA_REQUEST_CODE_1:
                    hapusFoto(R.id.foto_1);
                    imgFoto1.setImageBitmap(addWatermark(imageBitmap));
                    imgFoto1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imgFoto1.setTag("Flag to be post!");
                    break;
                case CAMERA_REQUEST_CODE_2:
                    hapusFoto(R.id.foto_2);
                    imgFoto2.setImageBitmap(addWatermark(imageBitmap));
                    imgFoto2.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imgFoto2.setTag("Flag to be post!");
                    break;
                case CAMERA_REQUEST_CODE_3:
                    hapusFoto(R.id.foto_3);
                    imgFoto3.setImageBitmap(addWatermark(imageBitmap));
                    imgFoto3.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imgFoto3.setTag("Flag to be post!");
                    break;
                case CAMERA_REQUEST_CODE_4:
                    hapusFoto(R.id.foto_4);
                    imgFoto4.setImageBitmap(addWatermark(imageBitmap));
                    imgFoto4.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imgFoto4.setTag("Flag to be post!");
                    break;
                default:
                    break;
            }
        } else if (resultCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Bitmap imageBitmap = CameraUtils.uriToBitmap(this, Uri.parse("file://" + mCurrentPhotoPath));
                imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, 150, 150);
                addWatermark(imageBitmap);

                switch (requestCode) {
                    case CAMERA_REQUEST_CODE_1:
                        imgFoto1.setImageBitmap(addWatermark(imageBitmap));
                        imgFoto1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imgFoto1.setTag("Flag to be post!");
                        break;
                    case CAMERA_REQUEST_CODE_2:
                        imgFoto2.setImageBitmap(addWatermark(imageBitmap));
                        imgFoto2.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imgFoto2.setTag("Flag to be post!");
                        break;
                    case CAMERA_REQUEST_CODE_3:
                        imgFoto3.setImageBitmap(addWatermark(imageBitmap));
                        imgFoto3.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imgFoto3.setTag("Flag to be post!");
                        break;
                    case CAMERA_REQUEST_CODE_4:
                        imgFoto4.setImageBitmap(addWatermark(imageBitmap));
                        imgFoto4.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imgFoto4.setTag("Flag to be post!");
                        break;
                    default:
                        break;
                }
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("Result Cancel : " + RESULT_CANCELED);
                switch (requestCode) {
                    case CAMERA_REQUEST_CODE_1:
                        hapusFoto(R.id.hapus_foto_1);
                        break;
                    case CAMERA_REQUEST_CODE_2:
                        hapusFoto(R.id.hapus_foto_2);
                        break;
                    case CAMERA_REQUEST_CODE_3:
                        hapusFoto(R.id.hapus_foto_3);
                        break;
                    case CAMERA_REQUEST_CODE_4:
                        hapusFoto(R.id.hapus_foto_4);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void toggleFormKeterangan(int visibility) {
        View formKeterangan = findViewById(R.id.form_keterangan);
        formKeterangan.setVisibility(visibility);
    }

    private void toggleFormMeter(int visibility) {
        View formLwbp  = findViewById(R.id.form_meter_lwbp);
        View formWbp   = findViewById(R.id.form_meter_wbp);
        View formKvarh = findViewById(R.id.form_meter_kvarh);

        formLwbp.setVisibility(visibility);
        formWbp.setVisibility(visibility);
        formKvarh.setVisibility(visibility);

        if (formLwbp.getVisibility() == View.VISIBLE) {
            edLwbp.requestFocus();
        }
    }

    public void onProsesButtonClicked(View btnId) {
        ItoDialog.simpleAlert(this, "Apakah Anda yakin akan memproses tusbung Work Order ini?", new ItoDialog.Action() {
            @Override
            public void onYesButtonClicked() {
                if (mWo.isExpired()) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.container_layout),
                            "Tidak dapat melanjutkan proses, karena sudah mencapai tanggal expired.",
                            Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(PemutusanActivity.this, R.color.material_pink));
                    snackbar.show();
                } else if (cekFieldContent()) {
                    if (ConnectivityUtils.isHaveInternetConnection(PemutusanActivity.this)) {
                        System.out.println("Have Internet Access : " + ConnectivityUtils.isHaveInternetConnection(PemutusanActivity.this));
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
        System.out.println("Tanggal Putus : " + edTanggalPutus.getText().toString().trim());
        if (edTanggalPutus.getText().toString().length() == 0) {
            Snackbar snackbar = NotifUtils.makePinkSnackbar(this, "Tanggal harus tersedia dari server");
            snackbar.show();
            return false;
        } else if (edLokasi.getText().toString().length() == 0) {
            Snackbar snackbar = NotifUtils.makePinkSnackbar(this, "Sistem belum menemukan lokasi Anda");
            snackbar.show();
            return false;
        } else if (spnPutus.getSelectedItemPosition() == 1) {
            if (spnKeterangan.getSelectedItemPosition() == 0) {
                Snackbar snackbar = NotifUtils.makePinkSnackbar(this, "Anda belum menentukan keterangan pemutusan");
                snackbar.show();
                return false;
            }
        } else if (wajibIsiLwbp) {
            if (edLwbp.getText().toString().trim().length() == 0) {
                Snackbar snackbar = NotifUtils.makePinkSnackbar(this, "LWBP Wajib diisi");
                snackbar.show();
                return false;
            }
        }

        int photoNumber = 0;
        for (Uri photoPath : mPhotoPath) {
            if (photoPath != null) photoNumber++;
        }
        if (photoNumber == 0) {
            Snackbar snackbar = NotifUtils.makePinkSnackbar(this, "Tidak ada bukti foto yang terlampir");
            snackbar.show();
            return false;
        }

        return true;
    }

    public void onBtnHapusClicked(View btnId) {
        hapusFoto(btnId.getId());
    }

    private void hapusFoto(int id) {
        System.out.println("Menghapus foto " + REQUEST_TAKE_PHOTO + ", Id : " + id);
        switch (id) {
            case R.id.hapus_foto_1:
                if (mPhotoPath[0] == null) return;
                new File(mPhotoPath[0].getPath()).delete();
                imgFoto1.setImageResource(R.drawable.camera_dark);
                imgFoto1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mPhotoPath[0] = null;
                break;
            case R.id.hapus_foto_2:
                if (mPhotoPath[1] == null) return;
                new File(mPhotoPath[1].getPath()).delete();
                imgFoto2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imgFoto2.setImageResource(R.drawable.camera_dark);
                mPhotoPath[1] = null;
                break;
            case R.id.hapus_foto_3:
                if (mPhotoPath[2] == null) return;
                new File(mPhotoPath[2].getPath()).delete();
                imgFoto3.setImageResource(R.drawable.camera_dark);
                imgFoto3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mPhotoPath[2] = null;
                break;
            case R.id.hapus_foto_4:
                if (mPhotoPath[3] == null) return;
                new File(mPhotoPath[3].getPath()).delete();
                imgFoto4.setImageResource(R.drawable.camera_dark);
                imgFoto4.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mPhotoPath[3] = null;
                break;
        }
    }

    private void updateTanggalDisplay(String date) {
        edTanggalPutus.setText(date);
    }

    private void updateSignalColorBar(int color) {
        findViewById(R.id.signal_condition).setBackgroundResource(color);
    }

    private String getStatusKodeFlag() {
        String kode = "";
        if (spnPutus.getSelectedItemPosition() == 0) {
            for (FlagTusbung flag : mFlagTusbung) {
                if (StringUtils.normalize(flag.getKeterangan()).toLowerCase().equals("putus")) {
                    return flag.getKode();
                }
            }
        } else {
            String keterangan = StringUtils.normalize((String) spnKeterangan.getSelectedItem()).toLowerCase();
            for (FlagTusbung flag : mFlagTusbung) {
                if (StringUtils.normalize(flag.getKeterangan()).toLowerCase().equals(keterangan)) {
                    return flag.getKode();
                }
            }
        }
        return kode;
    }

    private void doPemutusan(int mode) {
        SmileyLoading.show(this, "Sedang memproses tusbung", 15000, new SmileyLoading.LoadingTimer() {
            @Override
            public void onTimeout() {
                NotifUtils.makePinkSnackbar(PemutusanActivity.this, "Pengerjaan tusbung akan disimpan di lokal").show();
                finish();
            }
        });

        String noTul601     = mWo.getNoTul601();
        String noWo         = mWo.getNoWo();
        String idpel        = mWo.getPelangganId();
        String nama         = mWo.getNama();
        String alamat       = mWo.getAlamat();
        String unitUpId     = mWo.getUnitUp();
        String kodePetugas  = AppConfig.KODE_PETUGAS;
        String lwbp         = edLwbp.getText().toString();
        String wbp          = edWbp.getText().toString();
        String kvarh        = edKvarh.getText().toString();
        String status       = getStatusKodeFlag();
        String isGagalPutus = String.valueOf(spnPutus.getSelectedItemPosition());
        String namaPetugas  = mWo.getNama();
        String email        = "";
        try {
            email = URLEncoder.encode(edEmail.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String hp             = edHp.getText().toString();
        String tanggalExpired = mWo.getExpired();
        String tanggalPutus   = DateUtils.getDateFromLogin();

        if (tanggalPutus.isEmpty())
            edTanggalPutus.getText().toString();

        int jumlahFoto = 0;
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
        tusbung.setTanggalPemutusan(tanggalPutus);
        tusbung.setStatus(status);
        tusbung.setJumlahFoto(String.valueOf(jumlahFoto));
        tusbung.setExpired(tanggalExpired);
        tusbung.setUlang(isTusbungUlang);
        tusbung.setEmail(email);
        tusbung.setHp(hp);

        if (fotoTobePosted[0] != null) tusbung.setPhotoPath1(fotoTobePosted[0].toString());
        if (fotoTobePosted[1] != null) tusbung.setPhotoPath2(fotoTobePosted[1].toString());
        if (fotoTobePosted[2] != null) tusbung.setPhotoPath3(fotoTobePosted[2].toString());
        if (fotoTobePosted[3] != null) tusbung.setPhotoPath4(fotoTobePosted[3].toString());

        AppConfig.TUSBUNG = tusbung;

        if (mode == DO_ONLINE) {
            doTusbungOnline();
        } else if (mode == DO_OFFLINE) {
            doTusbungOffline();
        }

        // mark mWo has done.
        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(WorkOrder.class)
                        .equalTo("noWo", mWo.getNoWo())
                        .findFirst()
                        .setSelesai(true);
            }
        });

        // set tanggal pemutusan
        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(WorkOrder.class)
                        .equalTo("noWo", mWo.getNoWo())
                        .findFirst()
                        .setTanggalPutus(DateUtils.getDateFromLogin());
            }
        });

        Log.d(TAG, "doPemutusan: [Param]--------------------------------------------------------");
        Log.d(TAG, "doPemutusan: " + tusbung.toString());
    }

    private void doTusbungOnline() {
        Log.d(TAG, "[DO ONLINE : Simpan di penyimpanan server]");

        tusbung.setStatusSinkron(Constants.SINKRONISASI_PROSES);
        tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(this, fotoTobePosted[0]));
        tusbung.setPart("1");

        if (ConnectivityUtils.isHaveInternetConnection(this)) {
            Log.d(TAG, "doTusbungOnline: doing tusbung ulang is " + isTusbungUlang);
            Log.d(TAG, "doPemutusan: [Part Foto] " + tusbung.getPart() + " dari " + tusbung.getJumlahFoto());
            if (isTusbungUlang) {
                SocketTransaction.getInstance().sendMessage(ParamDef.DO_TUSBUNG_ULANG);
            } else {
                SocketTransaction.getInstance().sendMessage(ParamDef.DO_TUSBUNG);
            }
        }
    }

    private void doTusbungOffline() {
        // mark wo to be pending and will be uploaded later
        Log.d(TAG, "[DO OFFLINE : Simpan di penyimpanan local]");
        tusbung.setStatusSinkron(Constants.SINKRONISASI_PENDING);
        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("noWo", mWo.getNoWo())
                        .findFirst()
                        .setUploaded(false);
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("noWo", mWo.getNoWo())
                        .findFirst()
                        .setStatusSinkronisasi(Constants.SINKRONISASI_PENDING);
                LocalDb.getInstance().insert(tusbung);
            }
        });

        // Starting service to upload this wo.
        //startBackgroundUploader();

        SmileyLoading.shouldCloseDialog();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.container_layout),
                "Pengerjaan tusbung akan disimpan di lokal",
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

    private void startBackgroundUploader() {
        AlarmManager am  = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar     cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_WEEK, cal.get(Calendar.DAY_OF_WEEK));
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR));
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.get(Calendar.SECOND));

        Intent intent = new Intent(this, ConnectionListener.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, 0);

        Long alarmTime = cal.getTimeInMillis();
        am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 6000, pendingIntent);

        System.out.println("Starting alarm background uploader service");

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBusProvider.getInstance().register(this);
        mGoogleClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusProvider.getInstance().unregister(this);
        if (mGoogleClient.isConnected()) {
            stopLocationUpdates();
            mGoogleClient.disconnect();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTusbungRespond(ProgressUpdateEvent progressEvent) {
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, progressEvent.toString());

        SmileyLoading.shouldCloseDialog();

        // mark mWo has done.
        if (progressEvent.isSuccess()) {
            LocalDb.makeSafeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    LocalDb.getInstance().where(WorkOrder.class)
                            .equalTo("noWo", mWo.getNoWo())
                            .findFirst()
                            .setUploaded(true);
                    LocalDb.getInstance().where(WorkOrder.class)
                            .equalTo("noWo", mWo.getNoWo())
                            .findFirst()
                            .setStatusSinkronisasi(Constants.SINKRONISASI_SUKSES);
                }
            });

            String   message  = StringUtils.normalize(progressEvent.message);
            Snackbar snackbar = NotifUtils.makeGreenSnackbar(this, message);
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    EventBusProvider.getInstance().post(new RefreshEvent());
                    finish();
                }
            });
            snackbar.show();
        } else {
            LocalDb.makeSafeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    LocalDb.getInstance().where(WorkOrder.class)
                            .equalTo("noWo", mWo.getNoWo())
                            .findFirst()
                            .setUploaded(false);
                    LocalDb.getInstance().where(WorkOrder.class)
                            .equalTo("noWo", mWo.getNoWo())
                            .findFirst()
                            .setStatusSinkronisasi(Constants.SINKRONISASI_GAGAL);
                    tusbung.setStatusSinkron(Constants.SINKRONISASI_PENDING);
                    LocalDb.getInstance().insert(tusbung);
                }
            });

            String   message  = StringUtils.normalize(progressEvent.message);
            Snackbar snackbar = NotifUtils.makePinkSnackbar(this, message);
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    EventBusProvider.getInstance().post(new RefreshEvent());
                    //finish();
                }
            });
            snackbar.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTempUploadEvent(TempUploadEvent tempEvent) {
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, "Return from tempuploadwo " + tempEvent.toString());

        if (tempEvent.next != null) {
            int tempEventCounter = Integer.parseInt(tempEvent.next);
            if (tempEventCounter <= Integer.parseInt(tusbung.getJumlahFoto())) {
                Log.d(TAG, "doPemutusan: [Part Foto] " + tusbung.getPart() + " dari " + tusbung.getJumlahFoto());

                tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(this, fotoTobePosted[tempEventCounter - 1]));
                tusbung.setPart(tempEvent.next);
                AppConfig.TUSBUNG = tusbung;

                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long l) {
                        System.out.println("Ticking ...");
                    }

                    @Override
                    public void onFinish() {
                        System.out.println("Finish ...");
                        if (ConnectivityUtils.isHaveInternetConnection(PemutusanActivity.this)) {
                            if (isTusbungUlang) {
                                SocketTransaction.getInstance().sendMessage(ParamDef.DO_TUSBUNG_ULANG);
                            } else {
                                SocketTransaction.getInstance().sendMessage(ParamDef.DO_TUSBUNG);
                            }
                        } else {
                            NotifUtils.makePinkSnackbar(PemutusanActivity.this, "Tidak ada jaringan Internet");
                        }
                    }
                }.start();

            } else {
                SmileyLoading.shouldCloseDialog();
                Log.d(TAG, "onTempUploadEvent: Done " + tusbung.getPart() + " dari " + tusbung.getJumlahFoto());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEventReceived(WorkOrder workOrder) {
        updateInfoPelangganDisplay(workOrder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChangeReceived(NetworkAvailabilityEvent networkEvent) {
        System.out.println("Network Availability : " + networkEvent);
        if (networkEvent.isAvailable) {
            updateSignalColorBar(R.color.material_light_green);
        } else {
            updateSignalColorBar(R.color.material_pink);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketFailure(ErrorMessageEvent errorMessage) {
        NotifUtils.makePinkSnackbar(this, errorMessage.getMessage()).show();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: GoogleApiClient connection has connected");

        // nothing left to do with permission becase it has been added in login
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (reachMinimumLocationAccuracy == false)
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Accuracy : " + location.getAccuracy() + " " + location.getLatitude() + " " + location.getLongitude());

        float accuracy = location.getAccuracy();
        if (accuracy <= MINIMUM_ACCURACY_ACCEPTED && accuracy > NO_ACCURACY_ACCEPTED) {
            edLokasi.setText("X : " + location.getLatitude() + "\nY : " + location.getLongitude());
            tusbung.setLatitude(String.valueOf(location.getLatitude()));
            tusbung.setLongitude(String.valueOf(location.getLongitude()));

            stopLocationUpdates();
            reachMinimumLocationAccuracy = true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> spinner, View view, int position, long l) {
        if (spinner == spnPutus) {
            keteranganList.clear();
            keteranganList.add("Pilih Keterangan");
            if (position == 0) {
                keteranganList.addAll(putusList);
                toggleFormKeterangan(View.GONE);
                toggleFormMeter(View.VISIBLE);
                wajibIsiLwbp = true;
            } else {
                keteranganList.addAll(gagalPutusList);
                toggleFormKeterangan(View.VISIBLE);
                toggleFormMeter(View.GONE);
                wajibIsiLwbp = false;
            }
            keteranganAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
