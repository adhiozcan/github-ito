package id.net.iconpln.apps.ito.utility;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.R;

import static android.R.attr.colorPrimary;
import static android.R.attr.value;

/**
 * Created by Ozcan on 03/04/2017.
 */

public class ChartUtils {

    protected static List<Integer> getStandardColor(Context context) {
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(context, R.color.material_yellow));
        colors.add(ContextCompat.getColor(context, R.color.material_pink));
        colors.add(ContextCompat.getColor(context, R.color.material_green));
        colors.add(ContextCompat.getColor(context, R.color.material_blue));
        colors.add(ContextCompat.getColor(context, R.color.material_lighr_green));
        colors.add(ContextCompat.getColor(context, R.color.material_amber));
        colors.add(ContextCompat.getColor(context, R.color.material_purple));
        return colors;
    }

    public static class Pie {

        protected static String[] mParties = new String[]{
                "BPBL", "BPSL",
                "SPSL", "SPBL",
                "S", "BB", "SB"
        };

        public static void setChartConfiguration(Context context, PieChart mChart) {
        //    mChart.setUsePercentValues(true);
            mChart.getDescription().setEnabled(false);
            mChart.setExtraOffsets(5, 10, 5, 5);

            mChart.setDragDecelerationFrictionCoef(0.95f);

         /*   mChart.setCenterTextTypeface(FontUtils.makeUpWith(context, FontUtils.SANS_SERIF_LIGHT));
            mChart.setCenterText(ChartUtils.Pie.generateCenterSpannableText());*/

            mChart.setDrawHoleEnabled(false);
         //   mChart.setHoleColor(R.color.colorPrimary);

          //  mChart.setTransparentCircleColor(Color.WHITE);
          //  mChart.setTransparentCircleAlpha(110);

            mChart.setHoleRadius(58f);
            mChart.setTransparentCircleRadius(61f);


          //  mChart.setDrawCenterText(true);

            mChart.setRotationAngle(0);
            mChart.setRotationEnabled(false);
            mChart.setHighlightPerTapEnabled(true);
            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            // entry label styling
          /*  mChart.setEntryLabelColor(Color.WHITE);
            mChart.setEntryLabelTypeface(FontUtils.makeUpWith(context, FontUtils.SANS_SERIF_REGULAR));
            mChart.setEntryLabelTextSize(12f);*/

            // hidden legend
            mChart.getLegend().setEnabled(false);
        }

        private static SpannableString generateCenterSpannableText() {
            SpannableString s = new SpannableString("i-To \nApplication");
            s.setSpan(new RelativeSizeSpan(1.5f), 0, 17, 0);
            return s;
        }

        public static void setData(Context context, Chart chart, int[] chartData) {
            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

            // NOTE: The order of the entries when being added to the entries array determines
            // their position around the center of the chart.
            for (int i = 0; i < mParties.length; i++) {
                entries.add(new PieEntry(chartData[i], mParties[i]));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Rekapitulasi Work Order");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            dataSet.setColors(getStandardColor(context));

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
         //   data.setValueTextSize(11f);
         //   data.setValueTextColor(Color.BLACK);
         //   data.setValueTypeface(FontUtils.makeUpWith(context, FontUtils.SANS_SERIF_LIGHT));
            chart.setData(data);

            // undo all highlights
            chart.highlightValues(null);

            chart.invalidate();
        }

    }

    public static class Bar {
        protected static String[] xAxis = new String[]{
                "BPBL", "BPSL", "SPBL", "SPSL", "Sambung", "Belum Bongkar", "Bongkar"
        };

        public static void setChartConfiguration(Context context, BarChart mChart) {
            mChart.setDrawBarShadow(false);
            mChart.setDrawValueAboveBar(true);
            mChart.getLegend().setEnabled(false);

            mChart.getDescription().setEnabled(false);

            // if more than 8 entries are displayed in the chart, no values will be
            // drawn
            mChart.setMaxVisibleValueCount(8);

            // scaling can now only be done on x- and y-axis separately
            mChart.setPinchZoom(false);

            mChart.setDrawGridBackground(false);
            // mChart.setDrawYLabels(false);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setTextColor(Color.WHITE);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f); // only intervals of 1 day
            xAxis.setLabelCount(7);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setTextColor(Color.WHITE);
            leftAxis.setDrawGridLines(false);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            leftAxis.setLabelCount(8, false);

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setEnabled(false);
        }

        public static void setData(Context context, Chart chart, int[] chartData) {
            List<BarEntry> yVals = new ArrayList<>();
            for (int i = 0; i < chartData.length; i++) {
                yVals.add(new BarEntry(i, chartData[i]));
            }

            List<IBarDataSet> dataSets = new ArrayList<>();
            BarDataSet        set      = null;
            for (int i = 0; i < chartData.length; i++) {
                set = new BarDataSet(yVals, xAxis[i]);
                // set each label color.
                set.setColor(getStandardColor(context).get(i));
                dataSets.add(set);
            }

            // set each bar color.
            if (set != null)
                set.setColors(getStandardColor(context));

            BarData barData = new BarData(dataSets);
            barData.setValueTextSize(10f);
            barData.setValueTypeface(FontUtils.makeUpWith(context, FontUtils.SANS_SERIF_LIGHT));
            barData.setBarWidth(0.9f);
            chart.setData(barData);
            chart.animateXY(2000, 2000);
        }
    }
}
