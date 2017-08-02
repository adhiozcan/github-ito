package id.net.iconpln.apps.ito.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import id.net.iconpln.apps.ito.adapter.PelaksanaanTabAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.LocationFinder;
import id.net.iconpln.apps.ito.helper.WorkOrderComparator;
import id.net.iconpln.apps.ito.job.WoUploadService;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.RefreshEvent;
import id.net.iconpln.apps.ito.model.eventbus.WorkOrderEvent;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.PingEvent;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.ui.PencarianActivity;
import id.net.iconpln.apps.ito.utility.DateUtils;
import id.net.iconpln.apps.ito.utility.SynchUtils;
import io.realm.Realm;
import io.realm.Sort;

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
    private GoogleMap googleMap;

    private ArrayList<WorkOrder> woLunasList      = new ArrayList<>();
    private ArrayList<WorkOrder> woBelumLunasList = new ArrayList<>();
    private ArrayList<WorkOrder> woSelesaiList    = new ArrayList<>();
    private List<WorkOrder>      woList           = new ArrayList<>();
    private List<WorkOrder>      mWoBackupList    = new ArrayList<>();

    private List<WorkOrder> testMaxList = new ArrayList<>();

    private LocationFinder mLocationFinder;

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

        mLocationFinder = new LocationFinder(getActivity());

        AppConfig.NO_WO_LOCAL = "";
        checkAvailableData();

        mLocationFinder.find(new LocationFinder.LocationFinderListener() {
            @Override
            public void onLocationFound(LatLng location) {
                System.out.println("Found your location at " + location.latitude + " " + location.longitude);
                //shouldUpdateDeviceMarkerLocation(new LatLng(location.latitude, location.longitude));
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationFinder.stopUsingGPS();
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

    private void backupWoSelesai() {
        mWoBackupList = new ArrayList<>();
        List<WorkOrder> localWoSelesai = LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.getUserInformation().getKodePetugas())
                        .equalTo("isSelesai", true)
                        .findAll());
        mWoBackupList.addAll(localWoSelesai);

        System.out.println("[Starting] Backup!");
        System.out.println("--------------------------------------------------------------------");
        System.out.println(AppConfig.getUserInformation().getKodePetugas() + " " + AppConfig.KODE_PETUGAS + "mWoBackupList : " + localWoSelesai.size());
        List<WorkOrder> localWoAll = LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.getUserInformation().getKodePetugas())
                        .findAll());

        for (WorkOrder wo : localWoAll) {
            System.out.println(wo.getNoWo() + "\t" + wo.isSelesai() + "\t" + wo.getStatusSinkronisasi());
        }

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.getUserInformation().getKodePetugas())
                        .equalTo("isSelesai", false)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    private void getWoDataServer() {
        backupWoSelesai();
        SocketTransaction.getInstance().sendMessage(ParamDef.GET_WO);
    }

    private List<WorkOrder> getWoDataLocal() {
        ArrayList<WorkOrder> woList = new ArrayList<>();
        woList.addAll(LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("isWoUlang", false)
                        .findAllSorted("kddk", Sort.ASCENDING)));
        return woList;
    }

    private void categorizeWoList() {
        woLunasList = new ArrayList<>();
        woBelumLunasList = new ArrayList<>();
        woSelesaiList = new ArrayList<>();

        for (WorkOrder wo : woList) {
            System.out.println(wo.getNoWo() + "\tUploaded : " + wo.isUploaded() + "\tKeterangan : " + wo.getStatusSinkronisasi());
            if (wo.getStatusPiutang().equals("Belum Lunas")) {
                if (wo.isSelesai()) {
                    woSelesaiList.add(wo);
                } else {
                    woBelumLunasList.add(wo);
                }
            } else {
                woLunasList.add(wo);
            }
        }
    }

    private void initViewPager() {
        categorizeWoList();

        mLayoutContent.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mTabLayout.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);

        mTabLayout.removeAllTabs();
        mViewPager.setAdapter(new PelaksanaanTabAdapter(getActivity().getSupportFragmentManager(), woBelumLunasList, woSelesaiList, woLunasList));

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("WO Pelaksanaan (" + woBelumLunasList.size() + ")");
        mTabLayout.getTabAt(1).setText("WO Selesai (" + woSelesaiList.size() + ")");
        mTabLayout.getTabAt(2).setText("WO Lunas (" + woLunasList.size() + ")");

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        shouldUpdateAllWoMarker(woBelumLunasList);
                        break;
                    case 1:
                        shouldUpdateAllWoMarker(woSelesaiList);
                        break;
                    case 2:
                        shouldUpdateAllWoMarker(woLunasList);
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

        if (googleMap != null) {
            double latitude  = -7.783248;
            double longitude = 106.5338977;
            LatLng latLng    = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12f));
        }
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        /*try {
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
        }*/


        // Position the map's camera near Bandung, Indonesia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-17.115403, 119.041815)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(3.3f));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!isPanelExpand)
                    showPanel();
                else
                    hidePanel();
            }
        });

        /*
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            Criteria        criteria        = new Criteria();
            String          provider        = locationManager.getBestProvider(criteria, true);
            Location        location        = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                shouldUpdateDeviceMarkerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        */
        shouldUpdateAllWoMarker(woBelumLunasList);
    }

    private void shouldUpdateAllWoMarker(List<WorkOrder> woList) {
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
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(11f));
            }
        }
    }

    private void shouldUpdateDeviceMarkerLocation(LatLng latLng) {

        if (googleMap != null) googleMap.clear();

        // add marker and additional info on top of it.
        googleMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_blue_dot_marker)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11f));

        /*
        final Circle circle = googleMap.addCircle(new CircleOptions().center(latLng)
                .strokeColor(Color.BLUE).radius(8000));

        ValueAnimator vAnimator = new ValueAnimator();
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);  *//* PULSE *//*
        vAnimator.setIntValues(0, 100);
        vAnimator.setDuration(1000);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                // Log.e("", "" + animatedFraction);
                circle.setRadius(animatedFraction * 800);
            }
        });
        vAnimator.start();
        */
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusProvider.getInstance().unregister(this);
    }

    private void showNotFoundData() {
        noDataFound.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
        mTabLayout.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventReceived(final WorkOrderEvent woEvent) {
        Log.d(TAG, "[onWoAllReceived] ------------------------------------------------------------");
        if (woEvent.workOrders.length == 0) {
            Log.d(TAG, "onMessageEventReceived: " + woEvent.workOrders.length + " | " + getWoDataLocal().size());

            //continue using local data
            if (getWoDataLocal().size() > 0) {
                initViewPager();
            } else {
                showNotFoundData();
            }
        } else {
            // update marker on map

            for (WorkOrder _wo : woEvent.workOrders) {
                Log.d(TAG, _wo.toString());
                woList.add(_wo);
            }

            shouldUpdateAllWoMarker(woList);

            // save work order list into local.
            //StorageTransaction<WorkOrder> storageTransaction = new StorageTransaction<>();
            //storageTransaction.saveList(WorkOrder.class, woList);

            final List<WorkOrder> woFromServer = Arrays.asList(woEvent.workOrders);
            final List<WorkOrder> woClean      = new ArrayList<>();
            woClean.addAll(mWoBackupList);

            System.out.println("[Starting] Proses pembandingan");
            WorkOrderComparator comparator = new WorkOrderComparator();
            for (WorkOrder serverWo : woFromServer) {
                boolean found = false;
                for (WorkOrder localWo : mWoBackupList) {
                    if (comparator.compare(localWo, serverWo) == 1) {
                        System.out.println("Data sama " + serverWo.getNoWo());
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("Menambahkan wo baru : " + serverWo.getNoWo());
                    woClean.add(serverWo);
                }
            }

            System.out.println("Data terbackup : " + mWoBackupList.size());
            System.out.println("Data wo yang akan ditambahkan " + woClean.size());
            System.out.println("-----------------------------------------");

            LocalDb.makeSafeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (WorkOrder wo : woEvent.workOrders) {
                        realm.insertOrUpdate(wo);
                    }
                }
            });


            testMaxList.addAll(Arrays.asList(woEvent.workOrders));
            System.out.println("**********************************************************************");
            System.out.println("--> Current Size : " + woList.size() + "data");
            initViewPager();
            SynchUtils.writeSynchLog(SynchUtils.LOG_UNDUH, String.valueOf(woEvent.workOrders.length));
            Log.d(TAG, "[Wo Complete] Wo has been saved. :)");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onRefreshTrigger(RefreshEvent event) {
        Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
        woList.clear();
        woList.addAll(getWoDataLocal());
        initViewPager();
        /*woList.clear();
        woList.addAll(getWoDataLocal());
        mViewPager.getAdapter().notifyDataSetChanged();
        mTabLayout.getTabAt(0).setText("WO Pelaksanaan (" + woBelumLunasList.size() + ")");
        mTabLayout.getTabAt(1).setText("WO Selesai (" + woSelesaiList.size() + ")");
        mTabLayout.getTabAt(2).setText("WO Lunas (" + woLunasList.size() + ")");*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketFailure(ErrorMessageEvent errorMessage) {
        System.out.println(errorMessage.getErrorCode() + " | " + errorMessage.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventReceived(PingEvent pingEvent) {
        System.out.println(pingEvent.getDate());
        Date   date       = DateUtils.parseToDate(pingEvent.getDate());
        String dateString = DateUtils.parseToString(date);
        ItoApplication.pingDate = dateString;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUploadBackgroundFinished() {
        System.out.println("[Service] Stop uploading in the background");
    }
}
