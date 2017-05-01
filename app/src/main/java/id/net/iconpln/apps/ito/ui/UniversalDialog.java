package id.net.iconpln.apps.ito.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.net.iconpln.apps.ito.R;

/**
 * Created by Ozcan on 24/03/2017.
 */

public class UniversalDialog {
    public enum TYPE {
        BLUE,
        GREEN,
        RED
    }

    public static class Descriptor {
        public UniversalDialog.TYPE ud_type;
        public String               title;
        public String               content;
    }

    public interface Action {
        void onYesButtonClicked();

        void onNoButtonClicked();
    }

    public static void showMessage(Context context, Descriptor descriptor) {
        final Dialog dialog = new Dialog(context, R.style.UniversalDialog);
        dialog.setContentView(R.layout.dialog_fragment_universal);
        dialog.setCancelable(true);
        dialog.show();

        TextView sTitle       = (TextView) dialog.findViewById(R.id.dfu_title);
        TextView sContent     = (TextView) dialog.findViewById(R.id.dfu_content);
        TextView sCloseAction = (TextView) dialog.findViewById(R.id.dfu_btn_close);

        sTitle.setText(descriptor.title);
        sContent.setText(descriptor.content);
        switch (descriptor.ud_type) {
            case RED:
                sTitle.setBackgroundResource(R.color.universalDialogRed);
                break;
            case GREEN:
                sTitle.setBackgroundResource(R.color.universalDialogGreen);
                break;
            case BLUE:
                sTitle.setBackgroundResource(R.color.universalDialogBlue);
                break;
        }

        sCloseAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public static void simpleAlert(Context context, String message, final Action action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mobile AP2T i-TO");
        builder.setMessage(message);
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        action.onNoButtonClicked();
                    }
                });
        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        action.onYesButtonClicked();
                    }
                });
        builder.show();
    }
}
