package id.net.iconpln.apps.ito.ui.fragment;

import android.animation.Animator;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.PelaksanaanAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.Anim;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.storage.StorageTransaction;
import id.net.iconpln.apps.ito.utility.CommonUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class PelaksanaanFragmentOld extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;

    private RecyclerView       mRecyclerView;
    private PelaksanaanAdapter mAdapter;
    private List<WorkOrder>    woList;
    private View               viewNotification;

    private int WO_NETWORK_ITERATION = 100;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_pelaksanaan, container, false);
        initMap();


        viewNotification = view.findViewById(R.id.view_notification);
        viewNotification.findViewById(R.id.yes_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Anim.slideDown(viewNotification).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        viewNotification.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        });

        woList = new ArrayList<>();
        //provideDummyData();
        mAdapter = new PelaksanaanAdapter(getActivity(), "", woList);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));

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
                mAdapter.notifyDataSetChanged();
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

    private List<WorkOrder> getWoDataLocal() {
        StorageTransaction<WorkOrder> storageTransaction = new StorageTransaction<>();
        return storageTransaction.findAll(WorkOrder.class);
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

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    private void provideDummyData() {
        for (int i = 0; i < 10; i++) {
            WorkOrder wo = new WorkOrder();
            wo.setPelangganId("54438003771");
            wo.setNama("Ojan Suherman");
            wo.setAlamat("Jl. Socket Utama Bandwith Jaya Karsa");
            woList.add(wo);
        }
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
        shouldUpdateMarker();

        // Position the map's camera near Bandung, Indonesia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-6.915846, 107.604789)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_pelaksanaan, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
                break;
            /*case R.id.action_list:
                getActivity().findViewById(R.id.recycler_view).setVisibility(View.GONE);
                getActivity().findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
                break;
            case R.id.action_map:
                getActivity().findViewById(R.id.recycler_view).setVisibility(View.GONE);
                break;*/
        }
        return super.onOptionsItemSelected(item);
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

    private void shouldUpdateMarker() {
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
        shouldUpdateMarker();

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

        mAdapter.notifyDataSetChanged();
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
