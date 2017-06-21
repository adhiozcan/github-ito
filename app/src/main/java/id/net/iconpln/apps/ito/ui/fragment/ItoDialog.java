package id.net.iconpln.apps.ito.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Ozcan on 24/03/2017.
 */

public class ItoDialog {
    public interface Action {
        void onYesButtonClicked();

        void onNoButtonClicked();
    }

    public static void simpleAlert(Context context, String message, final Action action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mobile AP2T i-TO");
        builder.setMessage(message);
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        action.onNoButtonClicked();
                        dialog.dismiss();
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

    public static void simpleAlert(Context context, String title, String message, String yesButton, String noButton, final Action action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(noButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                action.onNoButtonClicked();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(yesButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                action.onYesButtonClicked();
            }
        });
        builder.show();
    }
}
