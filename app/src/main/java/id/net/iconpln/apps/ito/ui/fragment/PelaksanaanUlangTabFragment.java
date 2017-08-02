package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.PelaksanaanUlangAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.Statistic;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.WorkOrderEvent;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.storage.StorageTransaction;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.SynchUtils;
import io.realm.Realm;

/**
 * Created by Ozcan on 01/08/2017.
 */

public class PelaksanaanUlangTabFragment extends Fragment {
    private final String TAG = PelaksanaanUlangTabFragment.class.getSimpleName();

    private String tagTab;
    private List<WorkOrder> mWoList = new ArrayList<>();

    private SwipeRefreshLayout mRefresh;

    public static PelaksanaanUlangTabFragment newInstance(ArrayList<WorkOrder> workOrders, String tagTab) {
        System.out.println("From Pelaksanaan Ulang : " + workOrders.size());
        PelaksanaanUlangTabFragment fragment = new PelaksanaanUlangTabFragment();
        Bundle                      args     = new Bundle();
        args.putParcelableArrayList("woList", workOrders);
        args.putString("tag", tagTab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWoList = getArguments().getParcelableArrayList("woList");
            tagTab = getArguments().getString("tag");
            System.out.println("From Pelaksanaan Ulang : " + mWoList.size());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pelaksanaan_ulang_tab, container, false);
        System.out.println("Ok");
        System.out.println(mWoList.size());
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(new PelaksanaanUlangAdapter(getActivity(), tagTab, mWoList));
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));

        for (WorkOrder wo : mWoList) {
            System.out.println("-------------------->>>>" + wo.getNoWo());
        }

        if (mWoList.size() == 0) {
            view.findViewById(R.id.no_data_view).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.no_data_view).setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        /*mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromNetwork();
            }
        });*/
        return view;
    }

    private List<WorkOrder> getDataLocal() {
        ArrayList<WorkOrder> woList = new ArrayList<>();
        woList.addAll(LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance()
                        .where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("isWoUlang", true)
                        .findAll()));
        return woList;
    }

    private void getDataFromNetwork() {
        SocketTransaction.getInstance().sendMessage(ParamDef.GET_WO_ULANG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataWoUlangResponse(final WorkOrderEvent woEvent) {
        Log.d(TAG, "[Wo Ulang] " + woEvent.workOrders.length + " --------------------");

        mRefresh.setRefreshing(false);

        AppConfig.statistic.setSumWoUlang(woEvent.workOrders.length);
        StorageTransaction<Statistic> transaction = new StorageTransaction<>();
        transaction.save(Statistic.class, AppConfig.statistic);

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (WorkOrder wo : woEvent.workOrders) {
                    wo.setWoUlang(true);
                    realm.insertOrUpdate(wo);
                }
            }
        });

        SynchUtils.writeSynchLog(SynchUtils.LOG_UNDUH, String.valueOf(woEvent.workOrders.length));
    }
}
