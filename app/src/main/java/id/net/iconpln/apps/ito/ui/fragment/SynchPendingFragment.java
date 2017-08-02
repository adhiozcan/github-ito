package id.net.iconpln.apps.ito.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import id.net.iconpln.apps.ito.helper.NotifyAdapterChangeListener;
import id.net.iconpln.apps.ito.job.WoUploadService;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.NetworkAvailabilityEvent;
import id.net.iconpln.apps.ito.model.eventbus.ProgressUpdateEvent;
import id.net.iconpln.apps.ito.model.eventbus.TempUploadEvent;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.eventbus.WoUploadServiceEvent;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.SocketWatcher;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.ConnectivityUtils;
import id.net.iconpln.apps.ito.utility.ImageUtils;
import id.net.iconpln.apps.ito.utility.NotifUtils;
import id.net.iconpln.apps.ito.utility.StringUtils;
import id.net.iconpln.apps.ito.utility.SynchUtils;
import io.realm.Case;
import io.realm.Realm;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class SynchPendingFragment extends Fragment {
    private static final String TAG = SynchPendingFragment.class.getSimpleName();

    private SyncPendingAdapter mAdapter;
    private RecyclerView       mRecyclerView;
    private List<Tusbung>      mTusbungList;

    private int tusbungPosition = 0;
    private int tusbungTotal    = 0;

    private Tusbung tusbung = new Tusbung();
    private SocketWatcher mSocketWatcher;

    private ViewGroup noDataView;
    private View      btnSyncStart;

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
        makeTusbungQueue();

        mAdapter = new SyncPendingAdapter(getActivity(), mTusbungList, new NotifyAdapterChangeListener() {
            @Override
            public void onNotifyDataSetChanged() {
                checkDataIsEmpty();
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(getActivity()));

        btnSyncStart = view.findViewById(R.id.btn_sync_up_start);
        btnSyncStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonStartClicked();
            }
        });

        noDataView = (ViewGroup) view.findViewById(R.id.no_data_view);
        checkDataIsEmpty();
        return view;
    }

    private void checkDataIsEmpty() {
        if (mTusbungList.size() == 0) {
            noDataView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            noDataView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void makeTusbungQueue() {
        mTusbungList = new ArrayList<>();
        mTusbungList.addAll(getDataLocal());
        tusbungTotal = mTusbungList.size();
    }

    private List<Tusbung> getDataLocal() {
        return LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance()
                        .where(Tusbung.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("statusSinkron", Constants.SINKRONISASI_PENDING)
                        .findAll()
        );
    }

    private void onButtonStartClicked() {
        ItoDialog.simpleAlert(getActivity(), "Apakah Anda yakin akan memproses tusbung sinkronisasi ini?", new ItoDialog.Action() {
            @Override
            public void onYesButtonClicked() {
                if (!ConnectivityUtils.isHaveInternetConnection(getActivity())) {
                    NotifUtils.makePinkSnackbar((AppCompatActivity) getActivity(), "Tidak ada jaringan").show();
                    return;
                }

                if (tusbungTotal > 0) {
                    if (mTusbungList.get(tusbungPosition).getStatusSinkron().equals(Constants.SINKRONISASI_PENDING)) {
                        tusbung = mTusbungList.get(tusbungPosition);
                        tusbung.setPart("1");
                        upload();
                    }
                } else {
                    Toast.makeText(getActivity(), "Tidak ada data yang dapat diunggah", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNoButtonClicked() {
            }
        });
    }

    private void upload() {
        updateTusbungStatus(Constants.SINKRONISASI_PROSES);

        AppConfig.TUSBUNG = tusbung;
        if (tusbung.isUlang()) {
            SocketTransaction.getInstance().sendMessage(ParamDef.DO_TUSBUNG_ULANG);
        } else {
            SocketTransaction.getInstance().sendMessage(ParamDef.DO_TUSBUNG);
        }
        mSocketWatcher = new SocketWatcher(new SocketWatcher.WatcherListener() {
            @Override
            public void onWatcherTimeOut() {
                SocketTransaction.shouldReinitSocket();
                tusbung.setKeteranganSinkron("Timeout!");
                updateTusbungStatus(Constants.SINKRONISASI_PENDING);
            }
        });
        mSocketWatcher.monitor();
    }

    private Uri choosePhotoToUpload(int tempEventCounter) {
        Uri photo = null;
        switch (tempEventCounter) {
            case 1:
                photo = Uri.parse(tusbung.getPhotoPath1());
                break;
            case 2:
                photo = Uri.parse(tusbung.getPhotoPath2());
                break;
            case 3:
                photo = Uri.parse(tusbung.getPhotoPath3());
                break;
            case 4:
                photo = Uri.parse(tusbung.getPhotoPath4());
                break;
        }
        return photo;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTempUploadEvent(TempUploadEvent tempEvent) {
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, "Return from tempuploadwo " + tempEvent.toString());

        if (tempEvent.next != null) {
            int tempEventCounter = Integer.parseInt(tempEvent.next);

            if (tempEventCounter <= Integer.parseInt(tusbung.getJumlahFoto())) {
                Log.d(TAG, "doSinkronisasi: [Part Foto] " + tusbung.getPart() + " dari " + tusbung.getJumlahFoto());

                Uri photo = choosePhotoToUpload(tempEventCounter);
                if (photo != null) {
                    tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(getActivity(), photo));
                    tusbung.setPart(tempEvent.next);
                    upload();
                } else {
                    Log.d(TAG, "onTempUploadEvent: tidak ada foto");
                }

            } else {
                Log.d(TAG, "onTempUploadEvent: Done " + tusbung.getPart() + " dari " + tusbung.getJumlahFoto());
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTusbungRespond(final ProgressUpdateEvent progressEvent) {
        Log.d(TAG, "---------------------------------------------" + progressEvent.isSuccess());
        Log.d(TAG, progressEvent.toString());
        System.out.println("Is Success : " + progressEvent.isSuccess());

        // stop socket watcher to prevent wrong alarm
        if (mSocketWatcher != null)
            mSocketWatcher.stop();

        if (progressEvent.isSuccess()) {
            // remove the already successfull tusbung
            LocalDb.makeSafeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    LocalDb.getInstance().where(Tusbung.class)
                            .equalTo("noWo", progressEvent.noWo, Case.INSENSITIVE)
                            .findFirst()
                            .deleteFromRealm();

                    LocalDb.getInstance().where(WorkOrder.class)
                            .equalTo("noWo", progressEvent.noWo, Case.INSENSITIVE)
                            .findFirst()
                            .setUploaded(true);
                }
            });

            tusbungPosition++;
            if (tusbungPosition < tusbungTotal) {
                // continue to upload the next data with first photo
                Uri photo = choosePhotoToUpload(1);
                if (photo != null) {
                    tusbung = mTusbungList.get(tusbungPosition);
                    tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(getActivity(), photo));
                    tusbung.setPart("1");
                    upload();
                } else {
                    Log.d(TAG, "onTempUploadEvent: tidak ada foto");
                }

            } else {
                Log.d(TAG, "--- Sinkronisasi successfully ---");
                Log.d(TAG, "--- Stopping job uploading data in background ---");
                tusbungPosition = 0;
                tusbungTotal = 0;
                getActivity().stopService(new Intent(getActivity(), WoUploadServiceEvent.class));
            }
        } else {
            LocalDb.makeSafeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    LocalDb.getInstance().where(Tusbung.class)
                            .equalTo("noWo", progressEvent.noWo, Case.INSENSITIVE)
                            .findFirst()
                            .setStatusSinkron(Constants.SINKRONISASI_PENDING);

                    LocalDb.getInstance().where(Tusbung.class)
                            .equalTo("noWo", progressEvent.noWo, Case.INSENSITIVE)
                            .findFirst()
                            .setKeteranganSinkron(StringUtils.normalize(progressEvent.message));

                    LocalDb.getInstance().where(WorkOrder.class)
                            .equalTo("noWo", progressEvent.noWo, Case.INSENSITIVE)
                            .findFirst()
                            .setUploaded(false);
                }
            });

            System.out.println("Failed update WO");
        }

        refreshTusbungFromLocal();
        SynchUtils.writeSynchLog(SynchUtils.LOG_UPLOAD, String.valueOf(getDataLocal().size()));
    }

    private void updateTusbungStatus(String status) {
        tusbung.setStatusSinkron(status);
        mAdapter.notifyDataSetChanged();
    }

    private void refreshTusbungFromLocal() {
        mTusbungList.clear();
        mTusbungList.addAll(getDataLocal());
        mAdapter.notifyDataSetChanged();

        for (Tusbung tus : mTusbungList) {
            Log.d(TAG, "refreshTusbungFromLocal:");
            Log.d(TAG, tus.toString());
        }
        checkDataIsEmpty();
    }

    private void doHapusDataSinkronisasi() {
        new JunkTrashDialog(new JunkTrashDialog.OnClickListener() {
            @Override
            public void onHapusClicked(int type) {
                switch (type) {
                    case JunkTrashDialog.OPT_PENDING:
                        SynchUtils.writeSynchLog(SynchUtils.LOG_DEL_PENDING);
                        LocalDb.makeSafeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                LocalDb.getInstance().where(Tusbung.class)
                                        .equalTo("statusSinkron", Constants.SINKRONISASI_PENDING)
                                        .findAll()
                                        .deleteAllFromRealm();
                            }
                        });
                        break;
                    case JunkTrashDialog.OPT_GAGAL:
                        SynchUtils.writeSynchLog(SynchUtils.LOG_DEL_GAGAL);
                        LocalDb.makeSafeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                LocalDb.getInstance().where(Tusbung.class)
                                        .equalTo("statusSinkron", Constants.SINKRONISASI_GAGAL)
                                        .findAll()
                                        .deleteAllFromRealm();
                            }
                        });
                        break;
                    case JunkTrashDialog.OPT_ALL:
                        SynchUtils.writeSynchLog(SynchUtils.LOG_DEL_ALL);
                        LocalDb.makeSafeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                LocalDb.getInstance().where(Tusbung.class)
                                        .equalTo("statusSinkron", Constants.SINKRONISASI_PENDING)
                                        .equalTo("statusSinkron", Constants.SINKRONISASI_GAGAL)
                                        .findAll()
                                        .deleteAllFromRealm();
                            }
                        });
                        break;
                }

                mTusbungList.clear();
                mTusbungList.addAll(getDataLocal());
                mAdapter.notifyDataSetChanged();
            }
        }).show(getFragmentManager(), "trash");
    }


    @Override
    public void onStart() {
        EventBusProvider.getInstance().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBusProvider.getInstance().unregister(this);
        setHasOptionsMenu(false);
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUploadBackgroundFinished(WoUploadServiceEvent wusEvent) {
        //getActivity().stopService(new Intent(getActivity(), WoUploadService.class));
        System.out.println("[Service] Stop uploading in the background");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkBroadcastReceived(NetworkAvailabilityEvent networkEvent) {
        /*if (networkEvent.isAvailable) {
            SocketTransaction.shouldReinitSocket();
        }*/
    }
}
