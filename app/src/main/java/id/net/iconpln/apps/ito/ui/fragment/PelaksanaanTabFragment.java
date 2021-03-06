package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.PelaksanaanAdapter;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.utility.CommonUtils;


public class PelaksanaanTabFragment extends Fragment {

    private String tagTab;
    private ArrayList<WorkOrder> workOrders = new ArrayList<>();
    private View view;

    public PelaksanaanTabFragment() {
        // Required empty public constructor
    }


    public static PelaksanaanTabFragment newInstance(ArrayList<WorkOrder> workOrders, String tagTab) {
        PelaksanaanTabFragment fragment = new PelaksanaanTabFragment();
        Bundle                 args     = new Bundle();
        args.putParcelableArrayList("woList", workOrders);
        args.putString("tag", tagTab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            workOrders = getArguments().getParcelableArrayList("woList");
            tagTab = getArguments().getString("tag");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pelaksanaan_tab, container, false);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(new PelaksanaanAdapter(getActivity(), tagTab, workOrders));
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));
        return view;
    }
}
