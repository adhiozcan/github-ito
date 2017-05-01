package id.net.iconpln.apps.ito.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.utility.DateUtils;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class MonitoringFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private Spinner spnBulan, spnTahun;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoring, container, false);
        initView(view);

        // manipulate spinner filter bulan.
        ArrayAdapter<CharSequence> adapterBulan = ArrayAdapter.createFromResource(getActivity(),
                R.array.urutan_bulan, R.layout.adapter_item_spinner_bulan);
        adapterBulan.setDropDownViewResource(R.layout.adapter_item_spinner_dropdown_bulan);
        spnBulan.setAdapter(adapterBulan);
        spnBulan.setOnItemSelectedListener(this);

        // manipulate spinner filter tahun.
        ArrayAdapter<CharSequence> adapterTahun = ArrayAdapter.createFromResource(getActivity(),
                R.array.urutan_tahun, R.layout.adapter_item_spinner_tahun);
        adapterBulan.setDropDownViewResource(R.layout.adapter_item_spinner_dropdown_tahun);
        spnTahun.setAdapter(adapterTahun);
        spnTahun.setOnItemSelectedListener(this);
        return view;
    }

    private void initView(View view) {
        spnBulan = (Spinner) view.findViewById(R.id.spinner_bulan);
        spnTahun = (Spinner) view.findViewById(R.id.spinner_tahun);
    }

    private void doGetDataMonitoring(String bulan, String tahun) {
        AppConfig.MONITOR_BULAN = bulan;
        AppConfig.MONITOR_TAHUN = tahun;
        SocketTransaction.prepareStatement().sendMessage(ParamDef.GET_WO_MONITOR);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if ((spnBulan.getSelectedItemPosition() != 0) && (spnTahun.getSelectedItemPosition() != 0)) {
            String bulan = DateUtils.getMonth(spnBulan.getSelectedItem().toString());
            String tahun = spnTahun.getSelectedItem().toString();
            doGetDataMonitoring(bulan, tahun);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
