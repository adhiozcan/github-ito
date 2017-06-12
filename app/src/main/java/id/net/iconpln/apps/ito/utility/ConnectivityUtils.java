package id.net.iconpln.apps.ito.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import id.net.iconpln.apps.ito.ItoApplication;

/**
 * Created by Ozcan on 12/06/2017.
 */

public class ConnectivityUtils {
    public static final int DISCONNECT    = -1;
    public static final int SEARCHING     = 0;
    public static final int SIGNAL_WEAK   = 1;
    public static final int SIGNAL_STRONG = 2;

    private static int            signalStrength;
    private static SignalListener signalListener;

    public static boolean isHaveInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static void register(Context context, SignalListener signalListener) {
        ConnectivityUtils.signalListener = signalListener;
        PhoneStateListener phoneStateListener = new InnerPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public static void unregister(Context context) {
        PhoneStateListener phoneStateListener = new InnerPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    private static class InnerPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            if (signalStrength.isGsm()) {
                int asu = signalStrength.getGsmSignalStrength();
                if (asu == 0 || asu == 99) {
                    ConnectivityUtils.signalStrength = 0;
                    signalListener.onReceived(DISCONNECT);
                } else if (asu < 5) {
                    ConnectivityUtils.signalStrength = 2;
                    signalListener.onReceived(DISCONNECT);
                } else if (asu >= 5 && asu < 8) {
                    ConnectivityUtils.signalStrength = 3;
                    signalListener.onReceived(SIGNAL_WEAK);
                } else if (asu >= 8 && asu < 12) {
                    ConnectivityUtils.signalStrength = 4;
                    signalListener.onReceived(SIGNAL_WEAK);
                } else if (asu >= 12 && asu < 14) {
                    ConnectivityUtils.signalStrength = 5;
                    signalListener.onReceived(SIGNAL_STRONG);
                } else if (asu >= 14) {
                    ConnectivityUtils.signalStrength = 6;
                    signalListener.onReceived(SIGNAL_STRONG);
                }
            } else {
                int cdmaDbm = signalStrength.getCdmaDbm();
                if (cdmaDbm >= -89) {
                    ConnectivityUtils.signalStrength = 6;
                    signalListener.onReceived(SIGNAL_STRONG);
                } else if (cdmaDbm >= -97) {
                    ConnectivityUtils.signalStrength = 5;
                    signalListener.onReceived(SIGNAL_STRONG);
                } else if (cdmaDbm >= -103) {
                    ConnectivityUtils.signalStrength = 4;
                    signalListener.onReceived(SIGNAL_WEAK);
                } else if (cdmaDbm >= -107) {
                    ConnectivityUtils.signalStrength = 3;
                    signalListener.onReceived(SIGNAL_WEAK);
                } else if (cdmaDbm >= 109) {
                    ConnectivityUtils.signalStrength = 2;
                    signalListener.onReceived(DISCONNECT);
                } else {
                    ConnectivityUtils.signalStrength = 0;
                    signalListener.onReceived(DISCONNECT);
                }
            }
        }
    }
}
