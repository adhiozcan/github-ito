package id.net.iconpln.apps.ito.utility;

import id.net.iconpln.apps.ito.model.Riwayat;
import id.net.iconpln.apps.ito.storage.LocalDb;
import io.realm.Realm;

/**
 * Created by Ozcan on 03/07/2017.
 */

public class SynchUtils {
    public static final String LOG_UNDUH       = "app.ito.synch.log.unduh";
    public static final String LOG_UPLOAD      = "app.ito.synch.log.upload";
    public static final String LOG_DEL_ALL     = "app.ito.synch.log.delete_all";
    public static final String LOG_DEL_PENDING = "app.ito.synch.log.delete_pending";
    public static final String LOG_DEL_GAGAL   = "app.ito.synch.log.delete_gagal";

    public static Riwayat writeSynchLog(String message) {
        String unixTimeStamp = Formater.getTimeStamp();
        String date          = Formater.unixTimeToDateString(unixTimeStamp);
        String time          = Formater.unixTimeToTimeString(unixTimeStamp);

        final Riwayat riwayat = new Riwayat(Long.parseLong(unixTimeStamp), date, time, message);

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(riwayat);
            }
        });

        return riwayat;
    }
}
