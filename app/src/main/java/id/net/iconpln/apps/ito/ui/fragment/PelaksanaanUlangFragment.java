package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.net.iconpln.apps.ito.R;

/**
 * Created by Ozcan on 10/07/2017.
 */

public class PelaksanaanUlangFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pelaksanaan_ulang, container, false);
        return view;
    }
}