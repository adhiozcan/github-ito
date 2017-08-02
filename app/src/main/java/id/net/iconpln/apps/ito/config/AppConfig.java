package id.net.iconpln.apps.ito.config;

import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.model.Statistic;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.storage.StorageTransaction;

/**
 * Created by Ozcan on 31/03/2017.
 */

public class AppConfig {

    /**
     * Variables act as global and can be shared across activity fragment.
     */
    public static String  KODE_PETUGAS  = "";
    public static String  ID_UNIT_UP    = "";
    public static String  USERNAME      = "";
    public static String  PASSWORD      = "";
    public static String  NO_WO_LOCAL   = "";
    public static String  MONITOR_BULAN = "";
    public static String  MONITOR_TAHUN = "";
    public static Tusbung TUSBUNG       = null;
    public static String  TANGGAL       = "";

    public static Statistic statistic = new Statistic();

    public static int WO_PAGE_START = 0;
    public static int WO_PAGE_END   = 100;

    public static boolean isRemember = getUserRememberConfiguration();


    public static void saveUserRememberConfiguration(boolean isRemember) {
        Hawk.put(Constants.IS_REMEMBER, isRemember);
    }

    private static boolean getUserRememberConfiguration() {
        return Hawk.get(Constants.IS_REMEMBER, false);
    }

    public static void saveUserCredential(String username, String password) {
        Hawk.put(Constants.USERNAME, username);
        Hawk.put(Constants.PASSWORD, password);
    }

    public static Map<String, String> getUserCredential() {
        String              user     = Hawk.get(Constants.USERNAME);
        String              password = Hawk.get(Constants.PASSWORD);
        Map<String, String> result   = new HashMap<>();
        result.put(Constants.USERNAME, user);
        result.put(Constants.PASSWORD, password);
        return result;
    }

    public static UserProfile getUserInformation() {
        UserProfile user = new UserProfile();
        user.setNama(String.valueOf(Hawk.get(Constants.NAMA_PETUGAS, "")));
        user.setKodePetugas(String.valueOf(Hawk.get(Constants.KODE_PETUGAS, "")));
        user.setUnitupi(String.valueOf(Hawk.get(Constants.ID_UNIT_UPI, "")));
        user.setUnitup(String.valueOf(Hawk.get(Constants.ID_UNIT_UP, "")));
        user.setNamaunitup(String.valueOf(Hawk.get(Constants.ID_UNIT_UP_NAMA, "")));
        user.setUnitap(String.valueOf(Hawk.get(Constants.ID_UNIT_AP, "")));
        return user;
    }

    public static void saveUserInformation(UserProfile user) {
        if (user == null) return;
        Hawk.put(Constants.KODE_PETUGAS, user.getKodePetugas());
        Hawk.put(Constants.NAMA_PETUGAS, user.getNama());
        Hawk.put(Constants.ID_UNIT_UP, user.getUnitup());
        Hawk.put(Constants.ID_UNIT_UP_NAMA, user.getNamaunitup());
        Hawk.put(Constants.ID_UNIT_UPI, user.getUnitupi());
        Hawk.put(Constants.ID_UNIT_AP, user.getUnitap());
    }

    private static void deleteUserRememberConfiguration() {
        Hawk.delete(Constants.IS_REMEMBER);
    }

    private static void deleteUserCredential() {
        Hawk.delete(Constants.USERNAME);
        Hawk.delete(Constants.PASSWORD);
    }

    public static void deleteUserInformation() {
        Hawk.delete(Constants.KODE_PETUGAS);
        Hawk.delete(Constants.NAMA_PETUGAS);
        Hawk.delete(Constants.ID_UNIT_UP);
        Hawk.delete(Constants.ID_UNIT_UP_NAMA);
        Hawk.delete(Constants.ID_UNIT_UPI);
        Hawk.delete(Constants.ID_UNIT_AP);
    }

    public static void cleanupData() {
        List<WorkOrder> localWoSelesai = LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("isSelesai", true)
                        .findAll());

        System.out.println("Logout : ---------------------------------------------");
        System.out.println("Kode petugas : " + AppConfig.KODE_PETUGAS);
        System.out.println("Size local wo backup : " + localWoSelesai.size());

        USERNAME = "";
        PASSWORD = "";
        KODE_PETUGAS = "";
        ID_UNIT_UP = "";
        isRemember = false;

        deleteUserRememberConfiguration();
        deleteUserCredential();
        deleteUserInformation();

        Hawk.delete(Constants.IS_REMEMBER);

        /**
         * Save user information into local
         */
        StorageTransaction<UserProfile> storageTransaction = new StorageTransaction<>();
        storageTransaction.save(UserProfile.class, new UserProfile());
    }
}
