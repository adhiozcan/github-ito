package id.net.iconpln.apps.ito.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Ozcan on 26/04/2017.
 */

public class ChartVPAdapter extends FragmentPagerAdapter {
    final int CHART_PIE = 0;
    final int CHART_BAR = 1;

    private List<Fragment> fragmentList;

    public ChartVPAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragmentList = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Pie Chart";
        } else {
            return "Bar Chart";
        }
    }
}
