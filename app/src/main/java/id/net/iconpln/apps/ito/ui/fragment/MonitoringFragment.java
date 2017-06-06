package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.MonitoringAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.RMDatePickerDialog;
import id.net.iconpln.apps.ito.helper.SmileyLoadingView;
import id.net.iconpln.apps.ito.model.WoMonitoring;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.DateUtils;
import id.net.iconpln.apps.ito.utility.Formater;
import id.net.iconpln.apps.ito.utility.SmileyLoading;

import static id.net.iconpln.apps.ito.utility.Formater.numberToMonth;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class MonitoringFragment extends Fragment {
    private View              view;
    private SmileyLoadingView loadingView;

    private String parameterMonth, parameterYear;

    private TextView             mTxtDate;
    private View                 noDataView;
    private FloatingActionButton fabDatePicker;
    private RMDatePickerDialog   mRmDatePicker;
    private RecyclerView         mRecyclerView;
    private MonitoringAdapter    mAdapter;
    private List<WoMonitoring>   woMonitorings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_monitoring, container, false);
        initView();

        // manipulate spinner filter tahun.
        woMonitorings = new ArrayList<>();
        mAdapter = new MonitoringAdapter(getActivity(), woMonitorings);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        // calender and periode.
        Calendar now = Calendar.getInstance();
        mTxtDate.setText(Formater.numberToMonth(now.get(Calendar.MONTH) + 1) + " " + now.get(Calendar.YEAR));
        parameterMonth = Formater.intToMonth(now.get(Calendar.MONTH));
        parameterYear = String.valueOf(now.get(Calendar.YEAR));
        mRmDatePicker = new RMDatePickerDialog(getActivity(), RMDatePickerDialog.MONTH_AND_YEAR, now.get(Calendar.MONTH) + 1, now.get(Calendar.YEAR),
                new RMDatePickerDialog.OnDatePickerSet() {
                    @Override
                    public void onSet(int month, int year) {
                        parameterMonth = Formater.intToMonth(month - 1);
                        parameterYear = String.valueOf(year);
                        mTxtDate.setText(Formater.numberToMonth(month) + " " + year);
                        doGetDataMonitoring();
                    }
                });

        fabDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRmDatePicker.show();
            }
        });

        doGetDataMonitoring();

        return view;
    }

    private void initView() {
        noDataView = view.findViewById(R.id.no_data);
        loadingView = (SmileyLoadingView) view.findViewById(R.id.loading_view);
        fabDatePicker = (FloatingActionButton) view.findViewById(R.id.fab);
        mTxtDate = (TextView) view.findViewById(R.id.txt_date);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceived(WoMonitoring[] womonitorings) {
        SmileyLoading.close();
        if (womonitorings.length == 0) {
            noDataView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            return;
        }

        noDataView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        for (WoMonitoring wo : womonitorings) {
            woMonitorings.add(wo);
        }
        refreshData();
    }

    private void doGetDataMonitoring() {
        SmileyLoading.show(getActivity(), "Mohon tunggu sebentar");
        noDataView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        AppConfig.MONITOR_BULAN = parameterMonth;
        AppConfig.MONITOR_TAHUN = parameterYear;
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


}
