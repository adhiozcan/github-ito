package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.SynchAdapter;
import id.net.iconpln.apps.ito.model.Riwayat;
import id.net.iconpln.apps.ito.utility.Formater;
import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class SinkronisasiFragment extends Fragment {
    private ViewPager    viewPager;
    private TabLayout    tabLayout;
    private SynchAdapter adapter;

    private TextView mTxtLastSynch;

    private View btnSync;

    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sinkronisasi, container, false);
        realm = Realm.getDefaultInstance();

        btnSync = view.findViewById(R.id.btn_sync);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSyncButtonClicked();
            }
        });
        mTxtLastSynch = (TextView) view.findViewById(R.id.last_sync);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SynchRiwayatFragment());
        fragmentList.add(new SynchPendingFragment());

        adapter = new SynchAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        setLastSyncInfo();
        return view;
    }

    private void setLastSyncInfo() {
        if (getLatest() == null) return;
        mTxtLastSynch.setText(getLatest().getTanggal() + " " + getLatest().getWaktu());
    }

    private Riwayat getLatest() {
        List<Riwayat> riwayats = new ArrayList<>();
        riwayats.addAll(realm.copyFromRealm(realm.where(Riwayat.class).findAllSorted("unixTimeStamp", Sort.DESCENDING)));
        if (!riwayats.isEmpty()) {
            return riwayats.get(0);
        } else {
            return null;
        }
    }

    private void onSyncButtonClicked() {
        String unixTimeStamp = Formater.getTimeStamp();
        String date          = Formater.unixTimeToDateString(unixTimeStamp);
        String time          = Formater.unixTimeToTimeString(unixTimeStamp);

        Riwayat riwayat = new Riwayat(Long.parseLong(unixTimeStamp), date, time);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(riwayat);
        realm.commitTransaction();

        EventBusProvider.getInstance().post(riwayat);
        setLastSyncInfo();
    }
}
