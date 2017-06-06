package id.net.iconpln.apps.ito.config;

import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

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


    public static void saveUserRememberConfiguration(boolean isRemember) {
        Hawk.put(Constants.IS_REMEMBER, isRemember);
    }

    private static boolean getUserRememberConfiguration() {
        return Hawk.get(Constants.IS_REMEMBER, false);
    }

    public static void saveUserRemember(String username, String password) {
        Hawk.put(Constants.USERNAME, username);
        Hawk.put(Constants.PASSWORD, password);
    }

    public static Map<String, String> getUserRemember() {
        String              user     = Hawk.get(Constants.USERNAME);
        String              password = Hawk.get(Constants.PASSWORD);
        Map<String, String> result   = new HashMap<>();
        result.put(Constants.USERNAME, user);
        result.put(Constants.PASSWORD, password);
        return result;
    }

    public static void cleanDataSafely() {
        USERNAME = "";
        PASSWORD = "";
        KODE_PETUGAS = "";
        ID_UNIT_UP = "";
        saveUserRememberConfiguration(false);
        saveUserRemember("", "");
    }
}
