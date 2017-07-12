package id.net.iconpln.apps.ito.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import id.net.iconpln.apps.ito.utility.ConnectivityUtils;

/**
 * Created by Ozcan on 12/07/2017.
 */

public class ConnectionListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityUtils.isHaveInternetConnection(context)) {
        }
    }
}
