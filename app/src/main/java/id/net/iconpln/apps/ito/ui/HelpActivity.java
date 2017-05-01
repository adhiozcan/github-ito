package id.net.iconpln.apps.ito.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.utility.CommonUtils;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        CommonUtils.installToolbar(this);
        loadHelpAsset();
    }

    private void loadHelpAsset() {
        WebView browser = (WebView) findViewById(R.id.webview_help);
        browser.loadUrl("file:///android_asset/Piutang.html");
        browser.getSettings().setBuiltInZoomControls(true);

        browser.getSettings().setUseWideViewPort(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.getSettings().setSupportZoom(true);
        browser.getSettings().setDisplayZoomControls(false);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.setInitialScale(0);
    }
}
