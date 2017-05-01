package id.net.iconpln.apps.ito.utility;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class DeviceUtils {

    public static String getIMEI(Context context) {
        String imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return imei;
    }

    public static String getPhoneNumber(Context context) {
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //return "357726060926192";
        return mngr.getDeviceId();
    }
}
