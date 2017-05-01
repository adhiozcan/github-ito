package id.net.iconpln.apps.ito.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.utility.CommonUtils;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        CommonUtils.installToolbar(this);
    }
}
