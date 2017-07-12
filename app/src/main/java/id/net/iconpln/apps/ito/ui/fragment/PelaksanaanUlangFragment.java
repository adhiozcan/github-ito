package id.net.iconpln.apps.ito.ui.fragment;

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
import id.net.iconpln.apps.ito.adapter.PelaksanaanUlangAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.utility.CommonUtils;

/**
 * Created by Ozcan on 10/07/2017.
 */

public class PelaksanaanUlangFragment extends Fragment {
    private List<WorkOrder>         mWorkOrderList;
    private RecyclerView            mRecyclerView;
    private PelaksanaanUlangAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pelaksanaan_ulang, container, false);

        mWorkOrderList = new ArrayList<>();
        mAdapter = new PelaksanaanUlangAdapter(getActivity(), mWorkOrderList);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));

        List<WorkOrder> localWork = getDataLocal();
        if (localWork.size() == 0) {
            view.findViewById(R.id.no_data_view).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.no_data_view).setVisibility(View.GONE);
            mWorkOrderList.addAll(getDataLocal());
            mAdapter.notifyDataSetChanged();
        }

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
}