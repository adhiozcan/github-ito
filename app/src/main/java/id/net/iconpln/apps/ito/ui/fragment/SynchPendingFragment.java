package id.net.iconpln.apps.ito.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.SyncPendingAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.ProgressUpdateEvent;
import id.net.iconpln.apps.ito.model.RefreshEvent;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.ui.PemutusanActivity;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.ImageUtils;
import id.net.iconpln.apps.ito.utility.SmileyLoading;
import id.net.iconpln.apps.ito.utility.StringUtils;
import io.realm.Realm;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class SynchPendingFragment extends Fragment {
    private static final String TAG = SynchPendingFragment.class.getSimpleName();

    private SyncPendingAdapter mAdapter;
    private RecyclerView       mRecyclerView;
    private List<Tusbung>      mTusbungList;

    private final int MAX_POOL_UPLOAD  = 3;
    private       int BEGIN_POSITION   = 0;
    private       int CURRENT_POSITION = 0;
    private       int END_POSITION     = 0;

    private View btnSyncStart;

    private String in_progress_wo = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_synch_pending, container, false);

        // prepare local data
        mTusbungList = new ArrayList<>();
        mTusbungList.addAll(getDataLocal());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new SyncPendingAdapter(mTusbungList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));

        btnSyncStart = view.findViewById(R.id.btn_sync_up_start);
        btnSyncStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonStartClicked();
            }
        });
        return view;
    }

    private void onButtonStartClicked() {
        int totalData = mTusbungList.size();
        if (totalData > MAX_POOL_UPLOAD) {
            BEGIN_POSITION = 0;
            CURRENT_POSITION = 0;
            END_POSITION = MAX_POOL_UPLOAD;

            upload();
        } else {
            //TODO buat algoritma jika total data <= MAX_POOL_UPLOAD
            BEGIN_POSITION = 0;
            CURRENT_POSITION = 0;
            END_POSITION = totalData;

            upload();
        }
    }

    private void upload() {
        Log.d(TAG, "upload: \n" +
                "Begin Position : " + BEGIN_POSITION + "\n" +
                "End Position : " + END_POSITION + "\n" +
                "Current Position : " + CURRENT_POSITION);

        Tusbung tusbung = mTusbungList.get(CURRENT_POSITION);
        tusbung.setStatusSinkron("Proses");
        in_progress_wo = tusbung.getNoWo();
        mAdapter.notifyDataSetChanged();

        if (tusbung.getPhotoPath1() != null) {
            Log.d(TAG, "upload: photo 1");
            Uri photo1 = Uri.parse(tusbung.getPhotoPath1());
            tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(getActivity(), photo1));
            tusbung.setPart(String.valueOf(1));

            AppConfig.TUSBUNG = tusbung;
            SocketTransaction.prepareStatement().sendMessage(ParamDef.DO_TUSBUNG);
        }
        if (tusbung.getPhotoPath2() != null) {
            Log.d(TAG, "upload: photo 2");
            Uri photo2 = Uri.parse(tusbung.getPhotoPath2());
            tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(getActivity(), photo2));
            tusbung.setPart(String.valueOf(2));

            AppConfig.TUSBUNG = tusbung;
            SocketTransaction.prepareStatement().sendMessage(ParamDef.DO_TUSBUNG);
        }
        if (tusbung.getPhotoPath3() != null) {
            Log.d(TAG, "upload: photo 3");
            Uri photo3 = Uri.parse(tusbung.getPhotoPath3());
            tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(getActivity(), photo3));
            tusbung.setPart(String.valueOf(3));

            AppConfig.TUSBUNG = tusbung;
            SocketTransaction.prepareStatement().sendMessage(ParamDef.DO_TUSBUNG);
        }
        if (tusbung.getPhotoPath4() != null) {
            Log.d(TAG, "upload: photo 4");
            Uri photo4 = Uri.parse(tusbung.getPhotoPath4());
            tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(getActivity(), photo4));
            tusbung.setPart(String.valueOf(4));

            AppConfig.TUSBUNG = tusbung;
            SocketTransaction.prepareStatement().sendMessage(ParamDef.DO_TUSBUNG);
        }
    }

    private List<Tusbung> getDataLocal() {
        Realm realm = Realm.getDefaultInstance();
        return realm.copyFromRealm(realm.where(Tusbung.class).findAll());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTusbungRespond(ProgressUpdateEvent progressEvent) {
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, progressEvent.toString());

        SmileyLoading.close();
        if (progressEvent.kode == "1") {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(Tusbung.class).equalTo("noWo", in_progress_wo).findFirst().deleteFromRealm();
            realm.commitTransaction();

            mTusbungList.clear();
            mTusbungList.addAll(getDataLocal());
            mAdapter.notifyDataSetChanged();
        } else {
            //TODO ganti label
        }

        CURRENT_POSITION++;
        if (CURRENT_POSITION == END_POSITION) {
            BEGIN_POSITION = END_POSITION;
            if (mTusbungList.size() - END_POSITION > MAX_POOL_UPLOAD) {
                END_POSITION = END_POSITION + MAX_POOL_UPLOAD;
            } else {
                END_POSITION = mTusbungList.size() - END_POSITION;
            }
        }

        upload();

        Log.d(TAG, "onTusbungRespond: \n" +
                "Begin Position     : " + BEGIN_POSITION + "\n" +
                "Current Position   : " + CURRENT_POSITION + "\n" +
                "End Position       : " + END_POSITION);
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
}
