package id.net.iconpln.apps.ito.config;

import com.orhanobut.hawk.Hawk;

import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.model.Tusbung;

/**
 * Created by Ozcan on 31/03/2017.
 */

public class AppConfig {

    public static String  KODE_PETUGAS  = "";
    public static String  ID_UNIT_UP    = "";
    public static String  USERNAME      = "";
    public static String  PASSWORD      = "";
    public static String  NO_WO_LOCAL   = "";
    public static String  MONITOR_BULAN = "";
    public static String  MONITOR_TAHUN = "";
    public static Tusbung TUSBUNG       = null;

    public static int WO_PAGE_START = 0;
    public static int WO_PAGE_END   = 100;

    public static boolean isRemember = getUserRememberConfiguration();


    public static void saveUserRememberConfiguration(boolean b) {
        Hawk.put(Constants.IS_REMEMBER, b);
    }

    private static boolean getUserRememberConfiguration() {
        return Hawk.get(Constants.IS_REMEMBER, false);
    }

    public static void cleanDataSafely() {
        KODE_PETUGAS = "";
        ID_UNIT_UP = "";
        saveUserRememberConfiguration(false);
    }
}
