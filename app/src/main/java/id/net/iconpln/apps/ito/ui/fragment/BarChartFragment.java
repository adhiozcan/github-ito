package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.utility.ChartUtils;

/**
 * Created by Ozcan on 27/04/2017.
 */

public class BarChartFragment extends Fragment {
    public static BarChartFragment newInstance(int[] barChartData) {

        Bundle args = new Bundle();
        args.putIntArray("bar_chart_data", barChartData);
        BarChartFragment fragment = new BarChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BarChart mBarChart;
    private int[]    mBarChartData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBarChartData = getArguments().getIntArray("bar_chart_data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart_bar, container, false);
        initBarChart(view);
        return view;
    }

    private void initBarChart(View rootView) {
        mBarChart = (BarChart) rootView.findViewById(R.id.chart_bar);
        ChartUtils.Bar.setChartConfiguration(getActivity(), mBarChart);
        ChartUtils.Bar.setData(getActivity(), mBarChart, mBarChartData);
    }
}
