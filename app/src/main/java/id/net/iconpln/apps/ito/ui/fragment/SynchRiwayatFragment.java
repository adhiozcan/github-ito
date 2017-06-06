package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.SynchRiwayatAdapter;
import id.net.iconpln.apps.ito.model.RefreshEvent;
import id.net.iconpln.apps.ito.model.Riwayat;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import io.realm.Realm;
import io.realm.Sort;

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

        mRiwayatList = new ArrayList<>();
        mRiwayatList.addAll(getLocalData());

        mAdapter = new SynchRiwayatAdapter(mRiwayatList);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBusProvider.getInstance().unregister(this);
    }

    private List<Riwayat> getLocalData() {
        Realm realm = Realm.getDefaultInstance();
        return realm.copyFromRealm(realm.where(Riwayat.class).findAllSorted("unixTimeStamp", Sort.DESCENDING));
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

    @Subscribe
    public void onRefreshTriggerEvent(Riwayat riwayat) {
        mRiwayatList.add(0, riwayat);
        mAdapter.notifyDataSetChanged();
    }
}
