package id.net.iconpln.apps.ito.utility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Ozcan on 30/03/2017.
 */

public class CameraUtils {

    public static boolean canOpenCamera(Context context) {
        if (!CameraUtils.isDeviceSupportCamera(context)) {
            Toast.makeText(context, "Device tidak mendukung camera", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!CameraUtils.checkCameraPermission(context)) {
            Toast.makeText(context, "Aplikasi belum mendapat izin untuk menggunakan kamera",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private static boolean checkCameraPermission(Context context) {
        final String packageName = context.getApplicationContext().getPackageName();
        try {
            final PackageInfo packageInfo = context.getApplicationContext().getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(android.Manifest.permission.CAMERA)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Camera", "checkCameraPermission: ", e);
        }
        return false;
    }

    /**
     * Check if the device has camera.
     *
     * @param ctx
     * @return
     */
    public static boolean isDeviceSupportCamera(Context ctx) {
        if (ctx.getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get image from uri
     *
     * @param context
     * @param selectedFileUri
     * @return
     */
    public static Bitmap uriToBitmap(Context context, Uri selectedFileUri) {
        Bitmap image = null;
        try {
            InputStream image_stream;
            try {
                image_stream = context.getContentResolver().openInputStream(selectedFileUri);
                image = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

}
