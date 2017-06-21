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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.SyncPendingAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.helper.JunkTrashDialog;
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
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class SynchPendingFragment extends Fragment {
    private static final String TAG = SynchPendingFragment.class.getSimpleName();

    private SyncPendingAdapter mAdapter;
    private RecyclerView       mRecyclerView;
    private List<Tusbung>      mTusbungList;

    private View btnSyncStart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        for (int i = 0; i < mTusbungList.size(); i++) {
            if (mTusbungList.get(i).getStatusSinkron().equals(Constants.SINKRONISASI_PENDING))
                upload(mTusbungList.get(i));
        }
    }

    private void upload(Tusbung tusbung) {
        tusbung.setStatusSinkron(Constants.SINKRONISASI_PROSES);
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

        String statusSinkron = progressEvent.isSuccess() ? Constants.SINKRONISASI_SUKSES : Constants.SINKRONISASI_GAGAL;

        if (statusSinkron.equals(Constants.SINKRONISASI_GAGAL)) {
        }

        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction())
            realm.beginTransaction();
        realm.where(Tusbung.class)
                .equalTo("noWo", progressEvent.noWo, Case.INSENSITIVE)
                .findFirst()
                .setStatusSinkron(statusSinkron);
        realm.where(Tusbung.class)
                .equalTo("noWo", progressEvent.noWo, Case.INSENSITIVE)
                .findFirst()
                .setKeteranganSinkron(StringUtils.normalize(progressEvent.message));
        if (realm.isInTransaction())
            realm.commitTransaction();

        mTusbungList.clear();
        mTusbungList.addAll(getDataLocal());
        mAdapter.notifyDataSetChanged();
        SmileyLoading.close();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_synch, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_trash) {
            ItoDialog.simpleAlert(getActivity(), "Peringatan",
                    "Data yang dihapus tidak dapat dikembalikan lagi.",
                    "Mengerti, Lanjutkan",
                    "Batalkan",
                    new ItoDialog.Action() {
                        @Override
                        public void onYesButtonClicked() {
                            doHapusDataSinkronisasi();
                        }

                        @Override
                        public void onNoButtonClicked() {
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doHapusDataSinkronisasi() {
        new JunkTrashDialog(new JunkTrashDialog.OnClickListener() {
            @Override
            public void onHapusClicked(int type) {
                Realm realm = Realm.getDefaultInstance();
                if (!realm.isInTransaction())
                    realm.beginTransaction();
                switch (type) {
                    case JunkTrashDialog.OPT_PENDING:
                        realm.where(Tusbung.class)
                                .equalTo("statusSinkron", Constants.SINKRONISASI_PENDING)
                                .findAll()
                                .deleteAllFromRealm();
                        break;
                    case JunkTrashDialog.OPT_GAGAL:
                        realm.where(Tusbung.class)
                                .equalTo("statusSinkron", Constants.SINKRONISASI_GAGAL)
                                .findAll()
                                .deleteAllFromRealm();
                        break;
                    case JunkTrashDialog.OPT_ALL:
                        realm.where(Tusbung.class)
                                .equalTo("statusSinkron", Constants.SINKRONISASI_PENDING)
                                .equalTo("statusSinkron", Constants.SINKRONISASI_GAGAL)
                                .findAll()
                                .deleteAllFromRealm();
                        break;
                }
                if (realm.isInTransaction())
                    realm.commitTransaction();

                mTusbungList.clear();
                mTusbungList.addAll(getDataLocal());
                mAdapter.notifyDataSetChanged();
            }
        }).show(getFragmentManager(), "trash");
    }
}
