package id.net.iconpln.apps.ito.helper;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alsahfer on 3/26/17.
 */

public class MainApplication extends Application{

    public static final String IMAGE_PATH = createResourceFolder("/pln/image/"); // .(dot) for hide folder/image from gallery

    @Override
    public void onCreate() {
        super.onCreate();
        initFile();
    }

    static String createResourceFolder(String path) {
        if (Environment.isExternalStorageEmulated() || Environment.getExternalStorageDirectory() != null) {
            return Environment.getExternalStorageDirectory().getPath().concat(path);
        } else {
            return Environment.getDataDirectory().getPath().concat(path);
        }
    }

    public static String convertDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy", Locale.US);
        return dateFormat.format(date);
    }

    /**
     * CREATE FOLDER
     * method untuk membuat folder pada storage internal android
     */
    private void initFile() {
        final File imageFolder = new File(MainApplication.IMAGE_PATH);
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
            Log.d("FOLDER", "IMAGE_PATH folder created!");
        } else {
            Log.d("FOLDER", "folder exist!");
        }
    }
}
