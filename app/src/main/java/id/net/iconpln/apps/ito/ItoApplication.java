package id.net.iconpln.apps.ito;

import android.Manifest;
import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.Locale;

import id.net.iconpln.apps.ito.helper.CheckPermission;
import id.net.iconpln.apps.ito.socket.ItoHttpClient;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.storage.ItoStorage;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;

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
        RealmConfiguration config = new RealmConfiguration
                .Builder(getApplicationContext())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        Stetho.initialize(
                Stetho.newInitializerBuilder(getApplicationContext())
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(getApplicationContext()))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(getApplicationContext()).build())
                        .build());
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }
}