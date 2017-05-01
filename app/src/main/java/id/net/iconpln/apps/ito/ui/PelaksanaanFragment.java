package id.net.iconpln.apps.ito.ui;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.PelaksanaanAdapter;
import id.net.iconpln.apps.ito.adapter.TabMapAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.Anim;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.storage.StorageTransaction;
import id.net.iconpln.apps.ito.utility.CommonUtils;

import static android.content.ContentValues.TAG;


public class PelaksanaanFragment extends Fragment
        implements PelaksanaanItemFragment.OnFragmentInteractionListener,
        OnMapReadyCallback {


    private OnFragmentInteractionListener mListener;
    private View view;
    private final String TAG = getClass().getSimpleName();
    private LinearLayout mLayoutContent, mLayoutContainer;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private int heightContainer = 0;
    private boolean isPanelExpand = true;
    private boolean isBack = false;
    private GoogleMap googleMap;
    private ArrayList<WorkOrder> workOrdersLunasList = new ArrayList<>();
    private ArrayList<WorkOrder> workOrdersBelumLunasLIst = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private PelaksanaanAdapter mAdapter;
    private List<WorkOrder> woList = new ArrayList<>();
    private View viewNotification;

    private int WO_NETWORK_ITERATION = 100;


    public PelaksanaanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pelaksanaan2, container, false);

        initView();
        //   initViewPager();
        initMap();

        woList = new ArrayList<>();
        //provideDummyData();

        AppConfig.NO_WO_LOCAL = "";
        checkAvailableData();


        return view;
    }

    private void checkAvailableData() {
        Log.d(TAG, "[Check Data] :-----------------------------------------------------------");
        if (getWoDataLocal() != null)
            if ((!getWoDataLocal().isEmpty()) || (getWoDataLocal().size() > 0)) {
                woList.clear();
                woList.addAll(getWoDataLocal());
                initViewPager();
                Log.d(TAG, "[Get Local] : WorkOrder");
            } else {
                getWoDataServer();
                Log.d(TAG, "[Get Server] : WorkOrder");
            }
        else {
            getWoDataServer();
            Log.d(TAG, "[Get Server] : WorkOrder");
        }
    }

    private void getWoDataServer() {
        AppConfig.WO_PAGE_START = 0;
        AppConfig.WO_PAGE_END = 100;
        SocketTransaction.prepareStatement().sendMessage(ParamDef.GET_WO);

        AppConfig.WO_PAGE_START = 101;
        AppConfig.WO_PAGE_END = 200;
        SocketTransaction.prepareStatement().sendMessage(ParamDef.GET_WO);

        AppConfig.WO_PAGE_START = 201;
        AppConfig.WO_PAGE_END = 300;
        SocketTransaction.prepareStatement().sendMessage(ParamDef.GET_WO);
    }


    private List<WorkOrder> getWoDataLocal() {
        StorageTransaction<WorkOrder> storageTransaction = new StorageTransaction<>();
        return storageTransaction.findAll(WorkOrder.class);
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().
                findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    private void initView() {
        mLayoutContent = (LinearLayout) view.findViewById(R.id.layout_content);
        mLayoutContainer = (LinearLayout) view.findViewById(R.id.layout_main_container);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    private void initViewPager() {
        workOrdersLunasList = new ArrayList<>();
        workOrdersBelumLunasLIst = new ArrayList<>();

        for (WorkOrder wo : woList) {
            if (wo.getStatusPiutang().equals("Belum Lunas")) {
                workOrdersBelumLunasLIst.add(wo);
            } else {
                workOrdersLunasList.add(wo);
            }
        }

        mLayoutContent.setVisibility(View.VISIBLE);
        mTabLayout.removeAllTabs();
        mViewPager.setAdapter(new TabMapAdapter(getActivity().getSupportFragmentManager(), workOrdersBelumLunasLIst, workOrdersLunasList));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("LUNAS");
        mTabLayout.getTabAt(1).setText("BELUM LUNAS");

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isPanelExpand)
                    showPanel();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (!isPanelExpand)
                    showPanel();
                else
                    hidePanel();
            }
        });
    }

    private void hidePanel() {
        isPanelExpand = false;
        if (heightContainer == 0)
            heightContainer = mViewPager.getHeight();
        mLayoutContainer.animate().translationY(heightContainer);
    }

    private void showPanel() {
        isPanelExpand = true;
        mLayoutContainer.animate().translationY(0);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        try {
            // customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = this.googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.shades_of_gray));

            if (!success) {
                Toast.makeText(getActivity(), "Ouch, we failed to load the map theme", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        //shouldUpdateMarker();

        // Position the map's camera near Bandung, Indonesia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-6.915846, 107.604789)));
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

    private void woValidate() {
        Log.d(TAG, "[Mark Wo Local] ---------------------------------------------------------");
        Log.d(TAG, "woValidate: " + AppConfig.NO_WO_LOCAL);
        SocketTransaction.prepareStatement().sendMessage(ParamDef.SET_WO);
        Log.d(TAG, "\t|>[" + WO_NETWORK_ITERATION + "/" + 300 + "]------ WO by kode petugas : " + AppConfig.KODE_PETUGAS + " has downloaded");
        AppConfig.NO_WO_LOCAL = "";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventReceived(WorkOrder[] workOrder) {
        Log.d(TAG, "[onWoAllReceived] ------------------------------------------------------------");
        System.out.println(workOrder);
        if (workOrder == null) return;

        // update marker on map
        //shouldUpdateMarker();

        //if (woList.size() == Integer.parseInt(workOrder.getJumlahData()))
        for (WorkOrder _wo : workOrder) {
            woList.add(_wo);
            if (AppConfig.NO_WO_LOCAL.length() == 0)
                AppConfig.NO_WO_LOCAL = _wo.getNoWo();
            else
                AppConfig.NO_WO_LOCAL = AppConfig.NO_WO_LOCAL.concat("," + _wo.getNoWo());
        }

        // mark as local and tell server that this data has been download
        woValidate();

        // save work order list into local.
        StorageTransaction<WorkOrder> storageTransaction = new StorageTransaction<>();
        storageTransaction.saveList(WorkOrder.class, woList);

        //  mAdapter.notifyDataSetChanged();
        initViewPager();
        Log.d(TAG, "[Wo Complete] Wo has been saved. :)");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketFailure(ErrorMessageEvent errorMessage) {
        System.out.println(errorMessage.getErrorCode() + " | " + errorMessage.getMessage());
        new CountDownTimer(3_000, 1_000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                Anim.slideUp(viewNotification);
            }
        }.start();

    }

}
