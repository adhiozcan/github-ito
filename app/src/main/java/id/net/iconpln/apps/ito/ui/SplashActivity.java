package id.net.iconpln.apps.ito.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.helper.CheckPermission;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d("ITO", "onCreate: Before executing from repository");
        checkAppConfig();
        displaySplash();

        boolean isGranted = CheckPermission.check(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!isGranted)
            CheckPermission.showRequestDialog(this, Manifest.permission.READ_EXTERNAL_STORAGE, 200, new CheckPermission.onCheckPermissionCallback() {
                @Override
                public void onYesAnswer() {

                }

                @Override
                public void onNoAnswer() {

                }
            });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void checkAppConfig() {
    }

    private void displaySplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}

