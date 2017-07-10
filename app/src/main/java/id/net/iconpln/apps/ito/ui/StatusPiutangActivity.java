package id.net.iconpln.apps.ito.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.ItoApplication;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.envelope.PingEvent;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.DateUtils;
import id.net.iconpln.apps.ito.utility.StringUtils;

/**
 * Created by Ozcan on 30/03/2017.
 */

public class StatusPiutangActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = StatusPiutangActivity.class.getSimpleName();

    private String tabName;

    private TextView txtPelangganId;
    private TextView txtName;
    private TextView txtTanggalTul;
    private TextView txtNoTul;
    private TextView txtKodeKddk;
    private TextView txtAlamat;
    private TextView txtTarifDaya;
    private TextView txtGarduTiang;
    private TextView txtTanggalPutusLunasLabel;
    private TextView txtTanggalPutusLunas;
    private TextView txtStatusPelunasan;
    private TextView txtJumlahLembar;
    private TextView txtTagihanTul601;
    private TextView btnTusbung;

    private GoogleMap googleMap;
    private LatLng    customerLocation;
    private WorkOrder woInfo;
    private boolean   isAllowToCut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_piutang);
        CommonUtils.installToolbar(this);
        getDataFromIntent();
        initView();
        initMap();
    }

    private void getDataFromIntent() {
        if (getIntent().getExtras() != null) {
            tabName = getIntent().getExtras().getString("tag_tab");
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    private void initView() {
        txtPelangganId = (TextView) findViewById(R.id.pelanggan_id);
        txtName = (TextView) findViewById(R.id.pelanggan_nama);
        txtTanggalTul = (TextView) findViewById(R.id.tanggal_tul);
        txtNoTul = (TextView) findViewById(R.id.nomor_tul);
        txtKodeKddk = (TextView) findViewById(R.id.kode_kddk);
        txtAlamat = (TextView) findViewById(R.id.pelanggan_alamat);
        txtTarifDaya = (TextView) findViewById(R.id.tarif_daya);
        txtGarduTiang = (TextView) findViewById(R.id.gardu_tiang);
        txtTanggalPutusLunasLabel = (TextView) findViewById(R.id.lbl_tanggal_putus_lunas);
        txtTanggalPutusLunas = (TextView) findViewById(R.id.tanggal_putus_lunas);
        txtStatusPelunasan = (TextView) findViewById(R.id.status_pelunasan);
        txtJumlahLembar = (TextView) findViewById(R.id.jumlah_lembar);
        txtTagihanTul601 = (TextView) findViewById(R.id.tagihan_tul_601);
        btnTusbung = (TextView) findViewById(R.id.btnTusbung);
    }

    private void adjustingFieldWithTabType(WorkOrder workOrder) {
        if (tabName != null || tabName.isEmpty()) {
            switch (tabName) {
                case "pelaksanaan":
                    txtTanggalPutusLunasLabel.setText("Tanggal Putus");
                    txtTanggalPutusLunas.setText("Tidak ada data");
                    txtTanggalPutusLunas.setTextColor(ContextCompat.getColor(this, R.color.colorVulcan));
                    break;
                case "selesai":
                    txtTanggalPutusLunasLabel.setText("Tanggal Putus");
                    txtTanggalPutusLunas.setText(StringUtils.normalize(workOrder.getTanggalWo()));
                    txtTanggalPutusLunas.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
                    break;
                case "lunas":
                    txtTanggalPutusLunasLabel.setText("Tanggal Lunas");
                    txtTanggalPutusLunas.setText(StringUtils.normalize(workOrder.getTanggalPelunasan()));
                    txtTanggalPutusLunas.setTextColor(ContextCompat.getColor(this, R.color.colorVulcan));
                    break;
            }
        }
    }

    private void updateCustomerInfoDisplay(WorkOrder workOrder) {
        txtStatusPelunasan.setText(workOrder.getStatusPiutang());
        txtPelangganId.setText(workOrder.getPelangganId());
        txtName.setText(workOrder.getNama());
        txtTanggalTul.setText(StringUtils.normalize(workOrder.getTanggalWo()));
        txtNoTul.setText(workOrder.getNoTul());
        txtKodeKddk.setText(workOrder.getKddk());
        txtAlamat.setText(workOrder.getAlamat());
        txtTarifDaya.setText(workOrder.getTarif());
        txtGarduTiang.setText(workOrder.getNoGardu());
        adjustingFieldWithTabType(workOrder);

        String jumlahLembar = "Jumlah Lembar (" + workOrder.getJumlahLembar() + ")";
        txtJumlahLembar.setText(jumlahLembar);

        shouldShowTusbung(workOrder);
        showCustomerLocationOnMap(workOrder);

        NumberFormat nf                = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        int          tagihan_raw       = Integer.parseInt(workOrder.getTagihan601());
        String       tagihan_terformat = nf.format(tagihan_raw) + ",00";
        txtTagihanTul601.setText(tagihan_terformat);
    }

    private boolean shouldShowTusbung(WorkOrder workOrder) {
        if (workOrder.getStatusPiutang().equals("Belum Lunas")) {
            btnTusbung.setVisibility(View.VISIBLE);
            if (workOrder.isSelesai()) {
                btnTusbung.setText("Selesai Dikerjakan");
                btnTusbung.setBackgroundResource(R.color.material_green);
                btnTusbung.setEnabled(false);
            } else if (workOrder.isExpired()) {
                btnTusbung.setText("Sudah Expired");
                btnTusbung.setBackgroundResource(R.color.material_pink);
                btnTusbung.setEnabled(false);
            } else {
                btnTusbung.setText("Tusbung");
                btnTusbung.setEnabled(true);
            }

            return true;
        } else {
            btnTusbung.setVisibility(View.GONE);
            return false;
        }
    }

    private void showCustomerLocationOnMap(WorkOrder workOrder) {
        if ((workOrder.getKoordinatX() != null) && workOrder.getKoordinatY() != null) {
            double lat = Double.parseDouble(workOrder.getKoordinatX());
            double lng = Double.parseDouble(workOrder.getKoordinatY());
            System.out.println("------------- " + lat + " | " + lng);
            customerLocation = new LatLng(lat, lng);
            shouldUpdateMarker();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEventReceived(WorkOrder workOrder) {
        // providing work order information to be post when user clicked tusbus button.
        woInfo = workOrder;

        checkTusbungIsAllowed(workOrder);
        updateCustomerInfoDisplay(workOrder);
        Log.d(TAG, "onStatusPiutangDataReceiver: -------------------------------------------------");
        Log.d(TAG, "onStatusPiutangDataReceiver: " + workOrder.toString());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            //  NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Move to activity tusbung if permitted.
     * Also send work order item into PemutusanActivity.class
     *
     * @param btnId
     */
    public void onTusbungButtonClicked(View btnId) {
        if (isAllowToCut) {
            startActivity(new Intent(this, PemutusanActivity.class));
            EventBusProvider.getInstance().postSticky(woInfo);
            finish();
        }
    }

    private void checkTusbungIsAllowed(WorkOrder workOrder) {
        if (workOrder.getStatusPiutang().equals("Belum Lunas")) {
            isAllowToCut = true;
        } else {
            isAllowToCut = false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = this.googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.shades_of_gray));

            if (!success) {
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        shouldUpdateMarker();
    }

    private void shouldUpdateMarker() {
        if ((googleMap != null) && (customerLocation != null)) {
            googleMap.clear();
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(customerLocation));
            googleMap.addMarker(new MarkerOptions().position(customerLocation));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventReceived(PingEvent pingEvent) {
        System.out.println("[PING] -----------------------");
        System.out.println(pingEvent.getDate());
        Date   date       = DateUtils.parseToDate(pingEvent.getDate());
        String dateString = DateUtils.parseToString(date);
        ItoApplication.pingDate = dateString;
    }

}
