package id.net.iconpln.apps.ito.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.StringUtils;

/**
 * Created by Ozcan on 10/07/2017.
 */

public class UserActivity extends AppCompatActivity {
    private TextView textNamaPetugas;
    private TextView textKodePetugas;
    private TextView textAlamat;
    private TextView textUnitUpi;
    private TextView textUnitUp;
    private TextView textUnitAp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        CommonUtils.installToolbar(this);
        initView();

        showUserInformation(AppConfig.getUserInformation());
    }

    private void initView() {
        textNamaPetugas = (TextView) findViewById(R.id.text_nama_petugas);
        textKodePetugas = (TextView) findViewById(R.id.text_kode_petugas);
        textAlamat = (TextView) findViewById(R.id.text_alamat_petugas);
        textUnitUpi = (TextView) findViewById(R.id.text_unit_upi);
        textUnitUp = (TextView) findViewById(R.id.text_unit_up);
        textUnitAp = (TextView) findViewById(R.id.text_unit_ap);
    }

    private void showUserInformation(UserProfile user) {
        textNamaPetugas.setText(user.getNama());
        textAlamat.setText(StringUtils.normalize(user.getNamaunitup()));
        textKodePetugas.setText(user.getKodePetugas());
        textUnitUpi.setText(user.getUnitupi());
        textUnitUp.setText(user.getUnitup());
        textUnitAp.setText(user.getUnitap());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
