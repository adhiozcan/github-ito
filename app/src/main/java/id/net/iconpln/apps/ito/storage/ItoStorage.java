package id.net.iconpln.apps.ito.storage;

import android.app.Application;

import com.orhanobut.hawk.Hawk;

import id.net.iconpln.apps.ito.model.Model;

/**
 * Created by Ozcan on 17/04/2017.
 */

public class ItoStorage<T extends Model> extends StorageTransaction<T> {
    private static volatile ItoStorage storage;

    public static void init(Application context) {
        Hawk.init(context).build();
    }

    public static ItoStorage getStorageManager() {
        if (storage == null) {
            synchronized (ItoStorage.class) {
                if (storage == null) {
                    storage = new ItoStorage();
                }
            }
        }
        return storage;
    }

    private ItoStorage() {
        if (storage != null) {
            throw new RuntimeException("Please use getStorageManager() to use this class.");
        }
    }
}
