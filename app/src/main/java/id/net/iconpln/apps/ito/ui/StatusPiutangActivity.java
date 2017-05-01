package id.net.iconpln.apps.ito.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
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
import java.util.Locale;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.utility.CommonUtils;

/**
 * Created by Ozcan on 30/03/2017.
 */

public class StatusPiutangActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = StatusPiutangActivity.class.getSimpleName();
    private TextView txtPelangganId;
    private TextView txtName;
    private TextView txtTanggalTul;
    private TextView txtNoTul;
    private TextView txtKodeKddk;
    private TextView txtAlamat;
    private TextView txtTarifDaya;
    private TextView txtGarduTiang;
    private TextView txtTanggalPutus;
    private TextView txtStatusPelunasan;
    private TextView txtJumlahLembar;
    private TextView txtNominalRpTag;
    private TextView txtNominalRpbk;
    private TextView txtTagihanTul601;

    private GoogleMap googleMap;
    private LatLng    customerLocation;
    private WorkOrder woInfo;
    private boolean   isAllowToCut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_piutang);
        CommonUtils.installToolbar(this);
        initView();
        initMap();
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
        txtTanggalPutus = (TextView) findViewById(R.id.tanggal_putus);
        txtStatusPelunasan = (TextView) findViewById(R.id.status_pelunasan);
        txtJumlahLembar = (TextView) findViewById(R.id.jumlah_lembar);
        txtNominalRpTag = (TextView) findViewById(R.id.nilai_rp_tag);
        txtNominalRpbk = (TextView) findViewById(R.id.nilai_rbpk);
        txtTagihanTul601 = (TextView) findViewById(R.id.tagihan_tul_601);
    }

    private void updateCustomerInfoDisplay(WorkOrder workOrder) {
        txtStatusPelunasan.setText(workOrder.getStatusPiutang());
        txtPelangganId.setText(workOrder.getPelangganId());
        txtName.setText(workOrder.getNama());
        txtNoTul.setText(workOrder.getNoTul());
        txtKodeKddk.setText(workOrder.getKddk());
        txtAlamat.setText(workOrder.getAlamat());
        txtTarifDaya.setText(workOrder.getTarif());
        txtGarduTiang.setText(workOrder.getNoGardu());

        String jumlahLembar = "Jumlah Lembar (" + workOrder.getJumlahLembar() + ")";
        txtJumlahLembar.setText(jumlahLembar);

        // check if status pelunasan is belum lunas, then show the tusbung button
        if (workOrder.getStatusPiutang().equals("Belum Lunas")) {
            findViewById(R.id.btnTusbung).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btnTusbung).setVisibility(View.GONE);
        }

        // parse customer location using lat lng in order to show in google map
        double lat = Double.parseDouble(workOrder.getKoordinatX());
        double lng = Double.parseDouble(workOrder.getKoordinatY());
        System.out.println("------------- " + lat + " | " + lng);
        customerLocation = new LatLng(lat, lng);
        shouldUpdateMarker();


        NumberFormat nf                = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        int          tagihan_raw       = Integer.parseInt(workOrder.getTagihan601());
        String       tagihan_terformat = nf.format(tagihan_raw) + ",00";
        txtTagihanTul601.setText(tagihan_terformat);
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
            NavUtils.navigateUpFromSameTask(this);
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
        }
    }

    private void checkTusbungIsAllowed(WorkOrder workOrder) {
        // check jika user sudah melakukan pelunasan, dihitung dari field tanggal pelunasan,
        // jika value is not null, maka status = belum lunas
        /*if (workOrder.getStatusPiutang().equals("Belum Lunas")) {
            isAllowToCut = true;
        } else {
            isAllowToCut = false;
        }*/
        isAllowToCut = true;
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
}
