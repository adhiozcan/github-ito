package id.net.iconpln.apps.ito.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.MonitoringAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.WoMonitoring;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.DateUtils;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class MonitoringFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private Spinner mSpnBulan, mSpnTahun;
    private RecyclerView       mRecyclerView;
    private MonitoringAdapter  mAdapter;
    private List<WoMonitoring> woMonitorings;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoring, container, false);
        initView(view);

        // manipulate spinner filter bulan.
        ArrayAdapter<CharSequence> adapterBulan = ArrayAdapter.createFromResource(getActivity(),
                R.array.urutan_bulan, R.layout.adapter_item_spinner_bulan);
        adapterBulan.setDropDownViewResource(R.layout.adapter_item_spinner_dropdown_bulan);
        mSpnBulan.setAdapter(adapterBulan);
        mSpnBulan.setOnItemSelectedListener(this);

        // manipulate spinner filter tahun.
        ArrayAdapter<CharSequence> adapterTahun = ArrayAdapter.createFromResource(getActivity(),
                R.array.urutan_tahun, R.layout.adapter_item_spinner_tahun);
        adapterBulan.setDropDownViewResource(R.layout.adapter_item_spinner_dropdown_tahun);
        mSpnTahun.setAdapter(adapterTahun);
        mSpnTahun.setOnItemSelectedListener(this);

        woMonitorings = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new MonitoringAdapter(getActivity(), woMonitorings);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));

        return view;
    }

    private void initView(View view) {
        mSpnBulan = (Spinner) view.findViewById(R.id.spinner_bulan);
        mSpnTahun = (Spinner) view.findViewById(R.id.spinner_tahun);
    }

    private void doGetDataMonitoring(String bulan, String tahun) {
        AppConfig.MONITOR_BULAN = bulan;
        AppConfig.MONITOR_TAHUN = tahun;
        SocketTransaction.prepareStatement().sendMessage(ParamDef.GET_WO_MONITOR);
    }

    private void refreshData() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        EventBusProvider.getInstance().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBusProvider.getInstance().unregister(this);
        super.onStop();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if ((mSpnBulan.getSelectedItemPosition() != 0) && (mSpnTahun.getSelectedItemPosition() != 0)) {
            String bulan = DateUtils.getMonth(mSpnBulan.getSelectedItem().toString());
            String tahun = mSpnTahun.getSelectedItem().toString();
            doGetDataMonitoring(bulan, tahun);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceived(WoMonitoring[] womonitorings) {
        if (womonitorings == null) return;

        for (WoMonitoring wo : womonitorings) {
            woMonitorings.add(wo);
        }

        refreshData();
    }
}
