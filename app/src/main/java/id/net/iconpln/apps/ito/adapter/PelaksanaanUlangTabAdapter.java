package id.net.iconpln.apps.ito.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.ui.fragment.PelaksanaanUlangTabFragment;

/**
 * Created by Ozcan on 01/08/2017.
 */

public class PelaksanaanUlangTabAdapter extends FragmentStatePagerAdapter {

    private ArrayList<WorkOrder> woLunas;
    private ArrayList<WorkOrder> woBelumLunas;
    private ArrayList<WorkOrder> woSelesai;

    public PelaksanaanUlangTabAdapter(FragmentManager fm, ArrayList<WorkOrder> woLunas, ArrayList<WorkOrder> woBelumLunas, ArrayList<WorkOrder> woSelesai) {
        super(fm);
        this.woLunas = woLunas;
        this.woBelumLunas = woBelumLunas;
        this.woSelesai = woSelesai;

        System.out.println("Pelaksanaan : " + woBelumLunas.size());
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                System.out.println("Pelaksanaan on size: " + woBelumLunas.size());
                return PelaksanaanUlangTabFragment.newInstance(woBelumLunas, "pelaksanaan");
            case 1:
                return PelaksanaanUlangTabFragment.newInstance(woSelesai, "selesai");
            case 2:
                return PelaksanaanUlangTabFragment.newInstance(woLunas, "lunas");
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
