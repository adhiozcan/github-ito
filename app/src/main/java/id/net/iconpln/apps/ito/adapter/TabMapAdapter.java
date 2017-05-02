package id.net.iconpln.apps.ito.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.ui.PelaksanaanItemFragment;


/**
 * Created by Rizki Maulana on 4/25/2016.
 */
public class TabMapAdapter extends FragmentStatePagerAdapter {
    private ArrayList<WorkOrder> woLunas;
    private ArrayList<WorkOrder> woBelumLunas;
    private ArrayList<WorkOrder> woSelesai;

    public TabMapAdapter(FragmentManager fm, ArrayList<WorkOrder> woBelumLunas, ArrayList<WorkOrder> woLunas, ArrayList<WorkOrder> woSelesai) {
        super(fm);
        this.woBelumLunas = woBelumLunas;
        this.woLunas = woLunas;
        this.woSelesai = woSelesai;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PelaksanaanItemFragment.newInstance(woBelumLunas);
            case 1:
                return PelaksanaanItemFragment.newInstance(woLunas);
            case 2:
                return PelaksanaanItemFragment.newInstance(woSelesai);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}

