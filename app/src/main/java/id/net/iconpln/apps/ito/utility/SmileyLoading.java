package id.net.iconpln.apps.ito.utility;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.helper.SmileyLoadingView;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.ui.LoginActivity;
import id.net.iconpln.apps.ito.ui.SplashActivity;

/**
 * Created by Ozcan on 30/03/2017.
 */

public class SmileyLoading {
    private static final String TAG = SmileyLoading.class.getSimpleName();

    private static final int SHOW_TIMEOUT = 10_000;

    private static CountDownTimer timer;
    private static Dialog         dialog;
    private static Context        context;

    private static SmileyLoadingView slv;

    public static void show(final Context context, final String content) {
        Log.d(TAG, "[Start] Smiley loading will be showing");
        SmileyLoading.context = context;
        SmileyLoading.timer = countDownTimer();

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

    private static CountDownTimer countDownTimer() {
        Log.d(TAG, "Watch until Timeout -----------------------------");
        return new CountDownTimer(SHOW_TIMEOUT, 1_000) {
            @Override
            public void onTick(long l) {
                Log.d(TAG, "|---- " + l + " [OK]");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "|-- Ouch, we reach timeout! I will call socket to be init again :(");
                if (dialog != null) {
                    dialog.dismiss();
                    slv.stop();

                    SocketTransaction.doReInit();

                    Toast.makeText(context, "Timeout, coba beberapa saat lagi",
                            Toast.LENGTH_SHORT).show();
                }

            }
        };
    }

    public static boolean isShowing() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    public static void closeWithMessage(String message) {
        Log.d(TAG, "[Stop] Smiley loading will be close if any shown");
        if (isShowing()) {
            Log.d(TAG, "|-- is showing : " + isShowing());
            SmileyLoading.slv.stop();
            SmileyLoading.dialog.dismiss();
            timer.cancel();
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void close() {
        Log.d(TAG, "[Stop] Smiley loading will be close if any shown");
        if (isShowing()) {
            Log.d(TAG, "|-- is showing : " + isShowing());
            SmileyLoading.slv.stop();
            SmileyLoading.dialog.dismiss();
            SmileyLoading.timer.cancel();
        }
    }
}
