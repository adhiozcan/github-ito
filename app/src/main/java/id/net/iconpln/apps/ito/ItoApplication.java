package id.net.iconpln.apps.ito;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.crashlytics.android.Crashlytics;

import java.util.List;
import java.util.Locale;

import id.net.iconpln.apps.ito.socket.ItoHttpClient;
import id.net.iconpln.apps.ito.storage.ItoStorage;
import id.net.iconpln.apps.ito.storage.LocalDb;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class ItoApplication extends Application {
    private static String TAG = ItoApplication.class.getSimpleName();

    private static Application mApplication;

    public static String pingDate = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        Locale.setDefault(new Locale("in", "ID"));
        Fabric.with(this, new Crashlytics());
        ItoStorage.init(this);
        ItoHttpClient.init(this);
        LocalDb.setupRealm(this);
    }

    public static boolean isAppIsInBackground() {
        boolean         isInBackground = true;
        ActivityManager am             = (ActivityManager) mApplication.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(mApplication.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            //noinspection deprecation
            List<ActivityManager.RunningTaskInfo> taskInfo      = am.getRunningTasks(1);
            ComponentName                         componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(mApplication.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}