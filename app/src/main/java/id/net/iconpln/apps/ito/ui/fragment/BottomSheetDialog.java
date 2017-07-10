package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.socket.SocketTransaction;

/**
 * Created by Ozcan on 17/04/2017.
 */

public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    String message;

    public static BottomSheetDialog newInstance(String message) {

        Bundle args = new Bundle();
        args.putString("message", message);
        BottomSheetDialog fragment = new BottomSheetDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = getArguments().getString("message");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view          = inflater.inflate(R.layout.bottom_sheet_failure, container, false);
        View btnYesConfirm = view.findViewById(R.id.yes_confirm);
        btnYesConfirm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.yes_confirm) {
            SocketTransaction.shouldReinitSocket();
        }
    }
}
