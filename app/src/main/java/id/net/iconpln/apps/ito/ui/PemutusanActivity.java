package id.net.iconpln.apps.ito.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.RefreshEvent;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.PingEvent;
import id.net.iconpln.apps.ito.utility.CameraUtils;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.DateUtils;
import id.net.iconpln.apps.ito.utility.ImageUtils;

/**
 * Created by Ozcan on 30/03/2017.
 */

public class PemutusanActivity extends AppCompatActivity {
    private static final String TAG = PemutusanActivity.class.getSimpleName();

    public static final int CAMERA_REQUEST_CODE_1 = 101;
    public static final int CAMERA_REQUEST_CODE_2 = 102;
    public static final int CAMERA_REQUEST_CODE_3 = 103;
    public static final int CAMERA_REQUEST_CODE_4 = 104;

    private static int REQUEST_TAKE_PHOTO = 0;

    private Spinner spn_StatusPekerjaan;

    private TextView txt_noTul,
            txt_idPelanggan,
            txt_nama,
            txt_alamat,
            txt_tanggal;

    private EditText ed_TanggalPutus,
            ed_lwbp,
            ed_wbp,
            ed_kvarh;

    private ImageView img_foto1,
            img_foto2,
            img_foto3,
            img_foto4;

    private String
            petugasId,
            petugasUnitUpId;

    private String mCurrentPhotoPath;
    private Uri[] mPhotoPath = new Uri[4];

    private WorkOrder wo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_progress);
        CommonUtils.installToolbar(this);
        initView();

        // manipulate spinner status pekerjaan.
        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this,
                R.array.jenis_pekerjaan, android.R.layout.simple_spinner_item);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_StatusPekerjaan.setAdapter(adapterStatus);
    }

    private void initView() {
        spn_StatusPekerjaan = (Spinner) findViewById(R.id.status_pekerjaan);
        txt_noTul = (TextView) findViewById(R.id.nomor_tul);
        txt_idPelanggan = (TextView) findViewById(R.id.pelanggan_id);
        txt_nama = (TextView) findViewById(R.id.pelanggan_nama);
        txt_alamat = (TextView) findViewById(R.id.pelanggan_alamat);
        txt_tanggal = (TextView) findViewById(R.id.tanggal_tul);
        ed_TanggalPutus = (EditText) findViewById(R.id.tanggal_putus);
        ed_lwbp = (EditText) findViewById(R.id.lwbp);
        ed_wbp = (EditText) findViewById(R.id.wbp);
        ed_kvarh = (EditText) findViewById(R.id.kvarh);
        img_foto1 = (ImageView) findViewById(R.id.foto_1);
        img_foto2 = (ImageView) findViewById(R.id.foto_2);
        img_foto3 = (ImageView) findViewById(R.id.foto_3);
        img_foto4 = (ImageView) findViewById(R.id.foto_4);
    }

    /**
     * Update customer information.
     *
     * @param wo
     */
    private void updateInfoPelangganDisplay(WorkOrder wo) {
        this.wo = wo;
        txt_noTul.setText(wo.getNoTul601());
        txt_idPelanggan.setText(wo.getPelangganId());
        txt_nama.setText(wo.getNama());
        txt_alamat.setText(wo.getAlamat());
        txt_tanggal.setText(wo.getTanggalWo());
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
                // Error occurred while creating the File
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
                    img_foto1.setImageBitmap(imageBitmap);
                    img_foto1.setTag("Flag to be post!");
                    break;
                case CAMERA_REQUEST_CODE_2:
                    img_foto2.setImageBitmap(imageBitmap);
                    img_foto2.setTag("Flag to be post!");
                    break;
                case CAMERA_REQUEST_CODE_3:
                    img_foto3.setImageBitmap(imageBitmap);
                    img_foto3.setTag("Flag to be post!");
                    break;
                case CAMERA_REQUEST_CODE_4:
                    img_foto4.setImageBitmap(imageBitmap);
                    img_foto4.setTag("Flag to be post!");
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusProvider.getInstance().unregister(this);
    }

    private void doPemutusan() {
        String noTul       = txt_noTul.getText().toString();
        String noTul601    = wo.getNoTul601();
        String noWo        = wo.getNoWo();
        String unitUpId    = wo.getUnitUp();
        String kodePetugas = wo.getKodePetugas();
        //String tanggalPutus  = ed_TanggalPutus.getText().toString();
        String tanggalPutus = "20170413";
        String lwbp         = "2000018990";
        String wbp          = null;
        String kvarh        = null;
        //String lwbp          = ed_lwbp.getText().toString();
        //String wbp           = ed_wbp.getText().toString();
        //String kvarh         = ed_kvarh.getText().toString();
        String isGagalPutus = "0";
        String namaPetugas  = wo.getNama();

        Uri[] fotoTobePosted = new Uri[4];
        int   jumlahFoto     = 0;
        for (Uri uri : mPhotoPath) {
            if (uri != null) {
                fotoTobePosted[jumlahFoto] = uri;
                jumlahFoto++;
            }
        }

        Tusbung tusbung = new Tusbung();
        tusbung.setKodePetugas(kodePetugas);
        tusbung.setNamaPetugas(namaPetugas);
        tusbung.setUnitUpId(unitUpId);
        tusbung.setNoTul(noTul601);
        tusbung.setNoWo(noWo);
        tusbung.setTanggalPemutusan(tanggalPutus);
        tusbung.setStandLWBP(lwbp);
        tusbung.setStandWBP(wbp);
        tusbung.setStandKVARH(kvarh);
        tusbung.setGagalPutus(isGagalPutus);
        tusbung.setJumlahFoto(String.valueOf(jumlahFoto));

        int part = 1;
        for (int i = 0; i < jumlahFoto; i++) {
            tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(this, fotoTobePosted[i]));
            tusbung.setPart(String.valueOf(part));
            AppConfig.TUSBUNG = tusbung;
            //SocketTransaction.prepareStatement().sendMessage(ParamDef.DO_TUSBUNG);
            part++;
        }

        // mark wo has done.
        EventBusProvider.getInstance().post(new RefreshEvent(true));
        finish();

        Log.d(TAG, "doPemutusan: [Param]--------------------------------------------------------");
        Log.d(TAG, "doPemutusan: Param No Tul\t" + tusbung.getNoTul());
        Log.d(TAG, "doPemutusan: Param Id Unit UP\t" + tusbung.getUnitUpId());
        Log.d(TAG, "doPemutusan: Param Tanggal \t" + tusbung.getTanggalPemutusan());
        Log.d(TAG, "doPemutusan: Param LWBP\t" + tusbung.getStandLWBP());
        Log.d(TAG, "doPemutusan: Param WBP\t" + tusbung.getStandWBP());
        Log.d(TAG, "doPemutusan: Param KVARH\t" + tusbung.getStandKVARH());
        Log.d(TAG, "doPemutusan: Param Kode Petugas\t" + tusbung.getKodePetugas());
    }

    public void onBtnSimpanClicked(View btnId) {
        doPemutusan();
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
        updateTanggalDisplay(dateString);
    }

    private void updateTanggalDisplay(String date) {
        ed_TanggalPutus.setText(date);
    }
}
