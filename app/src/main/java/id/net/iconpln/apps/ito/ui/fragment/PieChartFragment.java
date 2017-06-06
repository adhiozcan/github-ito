package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.utility.ChartUtils;

/**
 * Created by Ozcan on 26/04/2017.
 */

public class PieChartFragment extends Fragment {
    public static PieChartFragment newInstance(int[] pieChartData) {

        Bundle args = new Bundle();
        args.putIntArray("pie_chart_data", pieChartData);
        System.out.println(pieChartData.length);

        PieChartFragment fragment = new PieChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private PieChart mPieChart;
    private int[]    mPieChartData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPieChartData = getArguments().getIntArray("pie_chart_data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart_pie, container, false);
        initPieChart(view);
        return view;
    }

    private void initPieChart(View rootView) {
        mPieChart = (PieChart) rootView.findViewById(R.id.chart_pie);
        ChartUtils.Pie.setChartConfiguration(mPieChart);
        ChartUtils.Pie.setData(getActivity(), mPieChart, mPieChartData);
    }

}
