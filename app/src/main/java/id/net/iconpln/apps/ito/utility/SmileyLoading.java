package id.net.iconpln.apps.ito.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.helper.SmileyLoadingView;
import id.net.iconpln.apps.ito.socket.SocketTransaction;

/**
 * Created by Ozcan on 30/03/2017.
 */

public class SmileyLoading {
    private static final String TAG = SmileyLoading.class.getSimpleName();

    private static int SHOW_TIMEOUT;

    private static CountDownTimer timer;
    private static Dialog         dialog;
    private static Context        context;

    private static LoadingTimer      loadingTimer;
    private static SmileyLoadingView slv;

    public static void show(final Context context, final String content) {
        Log.d(TAG, "[Start] Smiley loading will be showing");
        SmileyLoading.context = context;
        SmileyLoading.timer = countDownTimer(10_000);

        dialog = new Dialog(context, R.style.UniversalDialog);
        dialog.setContentView(R.layout.dialog_progress_loading);
        dialog.setCancelable(false);

        slv = (SmileyLoadingView) dialog.findViewById(R.id.loading_view);
        slv.setOnAnimPerformCompletedListener(new SmileyLoadingView.OnAnimPerformCompletedListener() {
            @Override
            public void onCompleted() {
                slv.setVisibility(View.INVISIBLE);
            }
        });
        slv.start();

        TextView keteranganLoading = (TextView) dialog.findViewById(R.id.loading_title);
        keteranganLoading.setText(content);

        dialog.show();
        timer.start();
    }

    public static void show(final Context context, final String content, int longmillis) {
        Log.d(TAG, "[Start] Smiley loading will be showing");
        SmileyLoading.context = context;
        SmileyLoading.timer = countDownTimer(longmillis);

        dialog = new Dialog(context, R.style.UniversalDialog);
        dialog.setContentView(R.layout.dialog_progress_loading);
        dialog.setCancelable(false);

        slv = (SmileyLoadingView) dialog.findViewById(R.id.loading_view);
        slv.setOnAnimPerformCompletedListener(new SmileyLoadingView.OnAnimPerformCompletedListener() {
            @Override
            public void onCompleted() {
                slv.setVisibility(View.INVISIBLE);
            }
        });
        slv.start();

        TextView keteranganLoading = (TextView) dialog.findViewById(R.id.loading_title);
        keteranganLoading.setText(content);

        dialog.show();
        timer.start();
    }

    public static void show(final Context context, final String content, int longmillis, LoadingTimer loadingTimer) {
        Log.d(TAG, "[Start] Smiley loading will be showing");
        SmileyLoading.context = context;
        SmileyLoading.timer = countDownTimer(longmillis);
        SmileyLoading.loadingTimer = loadingTimer;

        dialog = new Dialog(context, R.style.UniversalDialog);
        dialog.setContentView(R.layout.dialog_progress_loading);
        dialog.setCancelable(false);

        slv = (SmileyLoadingView) dialog.findViewById(R.id.loading_view);
        slv.setOnAnimPerformCompletedListener(new SmileyLoadingView.OnAnimPerformCompletedListener() {
            @Override
            public void onCompleted() {
                slv.setVisibility(View.INVISIBLE);
            }
        });
        slv.start();

        TextView keteranganLoading = (TextView) dialog.findViewById(R.id.loading_title);
        keteranganLoading.setText(content);

        dialog.show();
        timer.start();
    }

    private static CountDownTimer countDownTimer(int longmillis) {
        Log.d(TAG, "Watch until Timeout -----------------------------");
        SHOW_TIMEOUT = longmillis;

        return new CountDownTimer(SHOW_TIMEOUT, 1_000) {
            @Override
            public void onTick(long l) {
                Log.d(TAG, "|---- " + l + " [OK]");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "|-- Ouch, we reach timeout! I will call socket to be start again :(");
                if (isShowing()) {
                    if (loadingTimer != null) {
                        loadingTimer.onTimeout();
                    }
                    dialog.dismiss();
                    slv.stop();

                    SocketTransaction.shouldReinitSocket();

                    //Toast.makeText(context, "Timeout, coba beberapa saat lagi",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public static boolean isShowing() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    public static void shouldCloseDialog() {
        if (isShowing()) {
            SmileyLoading.slv.stop();
            SmileyLoading.dialog.dismiss();
            SmileyLoading.timer.cancel();
        }
    }

    public interface LoadingTimer {
        void onTimeout();
    }
}
