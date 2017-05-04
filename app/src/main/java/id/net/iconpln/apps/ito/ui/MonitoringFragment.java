package id.net.iconpln.apps.ito.ui;

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
import id.net.iconpln.apps.ito.model.WoMonitoring;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.DateUtils;
import id.net.iconpln.apps.ito.utility.Formater;

import static id.net.iconpln.apps.ito.utility.Formater.numberToMonth;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class MonitoringFragment extends Fragment {
    private View view;

    private String parameterMonth, parameterYear;

    private TextView mTxtDate;
    private FloatingActionButton fabDatePicker;
    private RMDatePickerDialog mRmDatePicker;
    private RecyclerView       mRecyclerView;
    private MonitoringAdapter  mAdapter;
    private List<WoMonitoring> woMonitorings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_monitoring, container, false);

        initView();
        doGetDataMonitoring();

        // manipulate spinner filter tahun.
        woMonitorings = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new MonitoringAdapter(getActivity(), woMonitorings);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));
        return view;
    }

    private void initView() {
        fabDatePicker = (FloatingActionButton) view.findViewById(R.id.fab);
        mTxtDate = (TextView) view.findViewById(R.id.txt_date);

        //canlender and periode
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

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceived(WoMonitoring[] womonitorings) {
        if (womonitorings == null) return;

        for (WoMonitoring wo : womonitorings) {
            woMonitorings.add(wo);
        }

        refreshData();
    }

    private void doGetDataMonitoring() {
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
