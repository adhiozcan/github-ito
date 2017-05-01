package id.net.iconpln.apps.ito.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.SynchRiwayatAdapter;
import id.net.iconpln.apps.ito.model.Riwayat;
import id.net.iconpln.apps.ito.utility.CommonUtils;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class SynchRiwayatFragment extends Fragment {

    private SynchRiwayatAdapter mAdapter;
    private RecyclerView        mRecyclerView;
    private List<Riwayat>       mRiwayatList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_synch_riwayat, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new SynchRiwayatAdapter(provideListRiwayatMockupModel());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));
        return view;
    }

    private List<Riwayat> provideListRiwayatMockupModel() {
        List<Riwayat> riwayatList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Riwayat riwayat = new Riwayat();
            riwayat.setJumlahData(String.valueOf(20 + i));
            riwayat.setTanggal(i + 1 + " April " + 2017);
            riwayatList.add(riwayat);
        }

        return riwayatList;
    }
}
