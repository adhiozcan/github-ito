package id.net.iconpln.apps.ito.socket;

import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Ozcan on 17/07/2017.
 */

public class SocketWatcher {
    private static String TAG = SocketWatcher.class.getSimpleName();
    private WatcherListener mWatcherListener;
    private CountDownTimer  timer;

    public SocketWatcher(WatcherListener mWatcherListener) {
        this.mWatcherListener = mWatcherListener;
        this.timer = watcherTimer();
    }

    public void monitor() {
        if (timer == null) {
            System.out.println("stub!");
            return;
        }
        timer.start();
    }

    public void stop() {
        if (timer == null) {
            System.out.println("stub!");
            return;
        }
        timer.cancel();
    }

    private CountDownTimer watcherTimer() {
        Log.d(TAG, "Watching -----------------------------");

        return new CountDownTimer(12_000, 1_000) {
            @Override
            public void onTick(long l) {
                Log.d(TAG, "|---- " + l + " [OK]");
            }

            @Override
            public void onFinish() {
                mWatcherListener.onWatcherTimeOut();
                Log.d(TAG, "|-- Watcher is on finish");
            }
        };
    }


    public interface WatcherListener {
        void onWatcherTimeOut();
    }
}
