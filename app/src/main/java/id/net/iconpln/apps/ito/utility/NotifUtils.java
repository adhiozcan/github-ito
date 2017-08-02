package id.net.iconpln.apps.ito.utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.ui.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Ozcan on 10/07/2017.
 */

public class NotifUtils {

    public static Snackbar makeGreenSnackbar(AppCompatActivity activity, String message) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.container_layout), message,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.material_light_green));
        return snackbar;
    }

    public static Snackbar makePinkSnackbar(AppCompatActivity activity, String message) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.container_layout), message,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.material_pink));
        return snackbar;
    }

    public static void makeTrayNotification(Context context, String title, String content) {
        MainActivity.navItemIndex = 3;
        MainActivity.CURRENT_TAG = MainActivity.TAG_SINKRONISASI;
        Intent resultIntent = new Intent(context, MainActivity.class);

        // because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // det an instance of NotificationManager
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content);
        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 1001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
