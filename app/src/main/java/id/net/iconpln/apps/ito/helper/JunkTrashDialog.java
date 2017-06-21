package id.net.iconpln.apps.ito.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import id.net.iconpln.apps.ito.R;

/**
 * Created by Ozcan on 21/06/2017.
 */

public class JunkTrashDialog extends DialogFragment {
    public static final int OPT_PENDING = 0;
    public static final int OPT_GAGAL   = 1;
    public static final int OPT_ALL     = 2;

    private int OPT_CHOOSED = 0;

    private OnClickListener onClickListener;

    public JunkTrashDialog() {
        super();
    }

    @SuppressLint("ValidFragment")
    public JunkTrashDialog(OnClickListener onClickListener) {
        super();
        this.onClickListener = onClickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Kriteria Status Sinkronisasi");
        builder.setSingleChoiceItems(R.array.tipe_penghapusan, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        OPT_CHOOSED = OPT_PENDING;
                        break;
                    case 1:
                        OPT_CHOOSED = OPT_GAGAL;
                        break;
                    case 2:
                        OPT_CHOOSED = OPT_ALL;
                        break;
                }
            }
        });
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onClickListener.onHapusClicked(OPT_CHOOSED);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder.create();
    }

    public interface OnClickListener {
        void onHapusClicked(int type);
    }
}