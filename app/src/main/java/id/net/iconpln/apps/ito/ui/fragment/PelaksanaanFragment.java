package id.net.iconpln.apps.ito.ui.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.ItoApplication;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.TabMapAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.model.eventbus.RefreshEvent;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.PingEvent;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.ui.PencarianActivity;
import id.net.iconpln.apps.ito.utility.DateUtils;
import io.realm.Realm;

public class PelaksanaanFragment extends Fragment implements OnMapReadyCallback {

    private View view;

    private final String TAG = getClass().getSimpleName();
    private LinearLayout mLayoutContent, mLayoutContainer;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private View loadingView;
    private View noDataFound;

    private int     heightContainer = 0;
    private boolean isPanelExpand   = true;
    private boolean isBack          = false;
    private GoogleMap googleMap;

    private ArrayList<WorkOrder> workOrdersLunasList     = new ArrayList<>();
    private ArrayList<WorkOrder> workOrderBelumLunasList = new ArrayList<>();
    private ArrayList<WorkOrder> workOrderSelesaiList    = new ArrayList<>();
    private List<WorkOrder>      woList                  = new ArrayList<>();

    private int WO_NETWORK_ITERATION = 100;

    private List<WorkOrder> testMaxList = new ArrayList<>();

    private Realm realm = LocalDb.getInstance();


    public PelaksanaanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBusProvider.getInstance().register(this);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pelaksanaan, container, false);
        initView();
        initMap();
        //provideDummyData();

        AppConfig.NO_WO_LOCAL = "";
        checkAvailableData();

        return view;
    }

    private void checkAvailableData() {
        Log.d(TAG, "[Check Data] :-----------------------------------------------------------");
        Log.d(TAG, "checkAvailableData: " + getWoDataLocal().size());

        if (getWoDataLocal() != null)
            if ((!getWoDataLocal().isEmpty()) || (getWoDataLocal().size() > 0)) {
                Log.d(TAG, "[Get Local] : WorkOrder");
                woList.clear();
                woList.addAll(getWoDataLocal());
                initViewPager();
            } else {
                Log.d(TAG, "[Get Server] : WorkOrder");
                getWoDataServer();
            }
        else {
            Log.d(TAG, "[Get Server] : WorkOrder");
            getWoDataServer();
        }
    }

    private void getWoDataServer() {
        AppConfig.WO_PAGE_START = 0;
        AppConfig.WO_PAGE_END = 100;
        SocketTransaction.getInstance().sendMessage(ParamDef.GET_WO);

        AppConfig.WO_PAGE_START = 101;
        AppConfig.WO_PAGE_END = 200;
        SocketTransaction.getInstance().sendMessage(ParamDef.GET_WO);

        AppConfig.WO_PAGE_START = 201;
        AppConfig.WO_PAGE_END = 300;
        SocketTransaction.getInstance().sendMessage(ParamDef.GET_WO);
    }


    private List<WorkOrder> getWoDataLocal() {
        ArrayList<WorkOrder> woList = new ArrayList<>();
        woList.addAll(LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS).findAll()));
        return woList;
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
        loadingView = view.findViewById(R.id.loading_view);
        noDataFound = view.findViewById(R.id.no_data_view);

        loadingView.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
        noDataFound.setVisibility(View.GONE);
    }

    private void initViewPager() {
        workOrdersLunasList = new ArrayList<>();
        workOrderBelumLunasList = new ArrayList<>();
        workOrderSelesaiList = new ArrayList<>();

        for (WorkOrder wo : woList) {
            if (wo.getStatusPiutang().equals("Belum Lunas")) {
                if (wo.isSelesai()) {
                    workOrderSelesaiList.add(wo);
                } else {
                    workOrderBelumLunasList.add(wo);
                }
            } else {
                workOrdersLunasList.add(wo);
            }
        }

        mLayoutContent.setVisibility(View.VISIBLE);
        mTabLayout.removeAllTabs();
        mViewPager.setAdapter(new TabMapAdapter(getActivity().getSupportFragmentManager(), workOrderBelumLunasList, workOrderSelesaiList, workOrdersLunasList));
        mTabLayout.setVisibility(View.VISIBLE);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("WO Pelaksanaan (" + workOrderBelumLunasList.size() + ")");
        mTabLayout.getTabAt(1).setText("WO Selesai (" + workOrderSelesaiList.size() + ")");
        mTabLayout.getTabAt(2).setText("WO Lunas (" + workOrdersLunasList.size() + ")");

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        shouldUpdateMarker(workOrderBelumLunasList);
                        break;
                    case 1:
                        shouldUpdateMarker(workOrderSelesaiList);
                        break;
                    case 2:
                        shouldUpdateMarker(workOrdersLunasList);
                        break;
                }
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

        loadingView.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
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

        // Position the map's camera near Bandung, Indonesia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-6.915846, 107.604789)));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!isPanelExpand)
                    showPanel();
                else
                    hidePanel();
            }
        });
        shouldUpdateMarker(workOrderBelumLunasList);
    }

    private void shouldUpdateMarker(List<WorkOrder> woList) {
        googleMap.clear();
        if ((googleMap != null) && ((woList != null) && (!woList.isEmpty()))) {
            for (WorkOrder wo : woList) {
                // if can't parse lat lng for any specific reason like null variable, then continue
                // to the next iteration.
                boolean isSafeToContinue = isValidLatLng(wo.getKoordinatX(), wo.getKoordinatY());
                if (!isSafeToContinue) continue;

                // add marker and additional info on top of it.
                double lat    = Double.parseDouble(wo.getKoordinatX());
                double lng    = Double.parseDouble(wo.getKoordinatY());
                LatLng latLng = new LatLng(lat, lng);
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .title(wo.getPelangganId())
                                .snippet(wo.getAlamat()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }

    private boolean isValidLatLng(String lat, String lng) {
        if (((lat != null) && (lng != null)) &&
                ((!lat.isEmpty()) && (!lng.isEmpty()))) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pelaksanaan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(getActivity(), PencarianActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        EventBusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    private void showNotFoundData() {
        noDataFound.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
        mTabLayout.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }

    private void woValidate() {
        Log.d(TAG, "[Mark Wo Local] ---------------------------------------------------------");
        Log.d(TAG, "woValidate: " + AppConfig.NO_WO_LOCAL);
        SocketTransaction.getInstance().sendMessage(ParamDef.SET_WO);
        Log.d(TAG, "\t|>[" + WO_NETWORK_ITERATION + "/" + 300 + "]------ WO by kode petugas : " + AppConfig.KODE_PETUGAS + " has downloaded");
        AppConfig.NO_WO_LOCAL = "";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventReceived(final WorkOrder[] workOrder) {
        Log.d(TAG, "[onWoAllReceived] ------------------------------------------------------------");
        if (workOrder.length == 0) {
            Log.d(TAG, "onMessageEventReceived: " + workOrder.length + " | " + getWoDataLocal().size());
            //continue using local data
            if (getWoDataLocal().size() > 0) {
                initViewPager();
            } else {
                showNotFoundData();
            }
        } else {
            for (WorkOrder _wo : workOrder) {
                Log.d(TAG, _wo.toString());
            }

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
            //StorageTransaction<WorkOrder> storageTransaction = new StorageTransaction<>();
            //storageTransaction.saveList(WorkOrder.class, woList);

            LocalDb.makeSafeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (WorkOrder wo : workOrder) {
                        realm.insert(wo);
                    }
                }
            });

            /*
            realm.beginTransaction();
            for (WorkOrder wo : workOrder) {
                realm.insert(wo);
            }
            realm.commitTransaction();
            */

            testMaxList.addAll(Arrays.asList(workOrder));
            System.out.println("**********************************************************************");
            System.out.println("--> Current Size : " + woList.size() + "data");
            initViewPager();
            Log.d(TAG, "[Wo Complete] Wo has been saved. :)");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onRefreshTrigger(RefreshEvent event) {
        Realm realm = Realm.getDefaultInstance();
        woList.clear();
        woList.addAll(realm.copyFromRealm(realm.where(WorkOrder.class).findAll()));
        initViewPager();
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
                Toast.makeText(getActivity(), "Tidak ada jaringan internet", Toast.LENGTH_SHORT).show();

            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventReceived(PingEvent pingEvent) {
        System.out.println(pingEvent.getDate());
        Date   date       = DateUtils.parseToDate(pingEvent.getDate());
        String dateString = DateUtils.parseToString(date);
        ItoApplication.pingDate = dateString;
    }

}
