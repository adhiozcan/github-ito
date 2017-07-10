package id.net.iconpln.apps.ito.storage;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import id.net.iconpln.apps.ito.ItoApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;

/**
 * Created by Ozcan on 28/06/2017.
 */

public class LocalDb {
    //private static LocalDb instance;
    private static Realm sRealm;

    public static Realm getInstance() {
        if (sRealm == null) {
            sRealm = Realm.getDefaultInstance();
        }
        return sRealm;
    }

    public static void setupRealm(ItoApplication application) {
        RealmConfiguration config = new RealmConfiguration
                .Builder(application)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        Stetho.initialize(
                Stetho.newInitializerBuilder(application)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(application))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(application).build())
                        .build());
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

    public static void makeSafeTransaction(Realm.Transaction transaction) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(transaction);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public void prepareTransaction() {
        if (sRealm.isInTransaction())
            sRealm.beginTransaction();
    }

    public void finishTransacion() {
        if (sRealm.isInTransaction())
            sRealm.commitTransaction();
    }

    private LocalDb() {
    }
}
