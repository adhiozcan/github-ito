package id.net.iconpln.apps.ito.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class SynchAdapter extends FragmentPagerAdapter {
    final int PENDING_SINKRONISASI = 0;
    final int RIWAYAT_SINKRONISASI = 1;

    private List<Fragment> fragmentList;

    public SynchAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case PENDING_SINKRONISASI:
                return "Pending Transaksi";
            case RIWAYAT_SINKRONISASI:
                return "Riwayat";
            default:
                return "Riwayat";
        }
    }
}
