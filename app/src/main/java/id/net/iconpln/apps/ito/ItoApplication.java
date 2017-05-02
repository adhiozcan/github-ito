package id.net.iconpln.apps.ito;

import android.Manifest;
import android.app.Application;

import com.crashlytics.android.Crashlytics;

import id.net.iconpln.apps.ito.helper.CheckPermission;
import id.net.iconpln.apps.ito.socket.ItoHttpClient;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.storage.ItoStorage;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class ItoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ItoStorage.init(this);
        ItoHttpClient.init(this);
        SocketTransaction.init();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}