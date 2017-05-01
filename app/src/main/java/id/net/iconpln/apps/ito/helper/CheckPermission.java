package id.net.iconpln.apps.ito.helper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Ozcan on 24/04/2017.
 */

public class CheckPermission {
    public static int READ_EXTERNAL_STORAGE  = 001;
    public static int WRITE_EXTERNAL_STORAGE = 002;

    public interface onCheckPermissionCallback {
        void onYesAnswer();

        void onNoAnswer();
    }

    /**
     * How to use :
     * if (!CheckPermission.check(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
     * CheckPermission.showRequestDialog(
     * this,
     * Manifest.permission.READ_EXTERNAL_STORAGE,
     * CheckPermission.READ_EXTERNAL_STORAGE);
     * }
     */

    public static boolean check(AppCompatActivity activity, String permission) {
        // Here, thisActivity is the current activity
        int result = ContextCompat.checkSelfPermission(activity, permission);

        if (result != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    public static void showRequestDialog(final AppCompatActivity activity, String permission, int permission_request, onCheckPermissionCallback callback) {
        ActivityCompat.requestPermissions(activity,
                new String[]{permission},
                permission_request);
    }
}
