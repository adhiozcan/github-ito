package id.net.iconpln.apps.ito.utility;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.model.Riwayat;
import id.net.iconpln.apps.ito.storage.LocalDb;
import io.realm.Realm;
import io.realm.Sort;

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

    public static Riwayat writeSynchLog(String message, String jumlahData) {
        String unixTimeStamp = Formater.getTimeStamp();
        String date          = Formater.unixTimeToDateString(unixTimeStamp);
        String time          = Formater.unixTimeToTimeString(unixTimeStamp);

        final Riwayat riwayat = new Riwayat(Long.parseLong(unixTimeStamp), date, time, jumlahData, message);

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(riwayat);
            }
        });

        return riwayat;
    }

    public static Riwayat getLastSynchLog() {
        List<Riwayat> riwayats = new ArrayList<>();
        riwayats.addAll(LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance()
                        .where(Riwayat.class)
                        .findAllSorted("unixTimeStamp", Sort.DESCENDING)));

        if (!riwayats.isEmpty()) {
            return riwayats.get(0);
        } else {
            return null;
        }
    }
}
