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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sinkronisasi, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SynchPendingFragment());
        fragmentList.add(new SynchRiwayatFragment());

        adapter = new SynchAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
