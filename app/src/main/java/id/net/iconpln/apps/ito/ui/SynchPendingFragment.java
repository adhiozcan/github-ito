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
import id.net.iconpln.apps.ito.adapter.SyncPendingAdapter;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.utility.CommonUtils;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class SynchPendingFragment extends Fragment {
    private SyncPendingAdapter mAdapter;
    private RecyclerView       mRecyclerView;
    private List<Tusbung>      mTusbungList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_synch_pending, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new SyncPendingAdapter(provideListTusbungMockupModel());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));
        return view;
    }

    private List<Tusbung> provideListTusbungMockupModel() {
        List<Tusbung> tusbungList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Tusbung tusbung = new Tusbung();
            tusbung.setKodePetugas("GHC");
            tusbung.setNoTul("VI-01-12200019984");
            tusbungList.add(tusbung);
        }
        return tusbungList;
    }
}
