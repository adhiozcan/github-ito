package id.net.iconpln.apps.ito;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import java.util.Locale;

import id.net.iconpln.apps.ito.socket.ItoHttpClient;
import id.net.iconpln.apps.ito.storage.ItoStorage;
import id.net.iconpln.apps.ito.storage.LocalDb;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class ItoApplication extends Application {

    public static String pingDate = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Locale.setDefault(new Locale("in", "ID"));
        Fabric.with(this, new Crashlytics());
        ItoStorage.init(this);
        ItoHttpClient.init(this);
        LocalDb.setupRealm(this);
    }
}