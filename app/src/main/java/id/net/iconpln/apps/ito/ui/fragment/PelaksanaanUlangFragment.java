package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.PelaksanaanTabAdapter;
import id.net.iconpln.apps.ito.adapter.PelaksanaanUlangTabAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.RefreshEvent;
import id.net.iconpln.apps.ito.storage.LocalDb;
import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by Ozcan on 10/07/2017.
 */

public class PelaksanaanUlangFragment extends Fragment {
    private String TAG = PelaksanaanUlangFragment.class.getSimpleName();

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ArrayList<WorkOrder> woLunasList      = new ArrayList<>();
    private ArrayList<WorkOrder> woBelumLunasList = new ArrayList<>();
    private ArrayList<WorkOrder> woSelesaiList    = new ArrayList<>();
    private List<WorkOrder>      woList           = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pelaksanaan_ulang, container, false);
        initView(view);
        checkAvailableData();
        return view;
    }

    private void initView(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    private void checkAvailableData() {
        Log.d(TAG, "[Check Data] :-----------------------------------------------------------");
        Log.d(TAG, "checkAvailableData: " + getWoDataLocal().size());

        if (getWoDataLocal() != null)
            if ((!getWoDataLocal().isEmpty()) || (getWoDataLocal().size() > 0)) {
                Log.d(TAG, "[Get Local] : WorkOrder");
                woList.clear();
                woList.addAll(getWoDataLocal());
                initViewPager();
            } else {
                Log.d(TAG, "[Get Server] : WorkOrder");
                initViewPager();
                getWoDataServer();
            }
        else {
            Log.d(TAG, "[Get Server] : WorkOrder");
            getWoDataServer();
        }
    }

    private void getWoDataServer() {
    }

    private List<WorkOrder> getWoDataLocal() {
        ArrayList<WorkOrder> woList = new ArrayList<>();
        woList.addAll(LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("isWoUlang", true)
                        .findAllSorted("kddk", Sort.ASCENDING)));
        return woList;
    }

    private void initViewPager() {
        woLunasList = new ArrayList<>();
        woBelumLunasList = new ArrayList<>();
        woSelesaiList = new ArrayList<>();

        for (WorkOrder wo : woList) {
            System.out.println(wo.getNoWo() + "\tUploaded : " + wo.isUploaded() + "\tKeterangan : " + wo.getStatusSinkronisasi());
            System.out.println(wo.getStatusPiutang());
            System.out.println(wo.isSelesai());
            if (wo.getStatusPiutang().equals("Belum Lunas")) {
                if (wo.isSelesai()) {
                    woSelesaiList.add(wo);
                } else {
                    woBelumLunasList.add(wo);
                }
            } else {
                woLunasList.add(wo);
            }
        }

        PelaksanaanUlangTabAdapter adapter =
                new PelaksanaanUlangTabAdapter(
                        getActivity().getSupportFragmentManager(),
                        woLunasList,
                        woBelumLunasList,
                        woSelesaiList);
        mTabLayout.removeAllTabs();
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Pelaksanaan Ulang (" + woBelumLunasList.size() + ")");
        mTabLayout.getTabAt(1).setText("Selesai (" + woSelesaiList.size() + ")");
        mTabLayout.getTabAt(2).setText("Lunas (" + woLunasList.size() + ")");

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onRefreshTrigger(RefreshEvent event) {
        woList.clear();
        woList.addAll(getWoDataLocal());
        initViewPager();
    }
}