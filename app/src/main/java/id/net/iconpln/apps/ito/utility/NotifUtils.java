package id.net.iconpln.apps.ito.utility;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import id.net.iconpln.apps.ito.R;

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
}
