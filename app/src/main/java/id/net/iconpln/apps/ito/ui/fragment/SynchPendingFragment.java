package id.net.iconpln.apps.ito.ui.fragment;

import android.net.Uri;
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
import id.net.iconpln.apps.ito.model.eventbus.ProgressUpdateEvent;
import id.net.iconpln.apps.ito.model.eventbus.TempUploadEvent;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import id.net.iconpln.apps.ito.utility.ImageUtils;
import id.net.iconpln.apps.ito.utility.SmileyLoading;
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

    private Tusbung tusbung = new Tusbung();

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

        mAdapter = new SyncPendingAdapter(mTusbungList);
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
        return view;
    }

    private void onButtonStartClicked() {
        ItoDialog.simpleAlert(getActivity(), "Apakah Anda yakin akan memproses tusbung sinkronisasi ini?", new ItoDialog.Action() {
            @Override
            public void onYesButtonClicked() {
                if (mTusbungList.size() > 0) {
                    for (int i = 0; i < mTusbungList.size(); i++) {
                        if (mTusbungList.get(i).getStatusSinkron().equals(Constants.SINKRONISASI_PENDING)) {
                            tusbung = mTusbungList.get(i);
                            tusbung.setPart("1");
                            upload();
                        }
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
        tusbung.setStatusSinkron(Constants.SINKRONISASI_PROSES);
        mAdapter.notifyDataSetChanged();
    }

    private List<Tusbung> getDataLocal() {
        return LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(Tusbung.class).findAll()
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTempUploadEvent(TempUploadEvent tempEvent) {
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, "Return from tempuploadwo " + tempEvent.toString());

        if (tempEvent.next != null) {
            int tempEventCounter = Integer.parseInt(tempEvent.next);
            if (tempEventCounter <= Integer.parseInt(tusbung.getJumlahFoto())) {
                Log.d(TAG, "doSinkronisasi: [Part Foto] " + tusbung.getPart() + " dari " + tusbung.getJumlahFoto());

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

                if (photo != null) {
                    tusbung.setBase64Foto(ImageUtils.getURLEncodeBase64(getActivity(), photo));
                    tusbung.setPart(tempEvent.next);
                    AppConfig.TUSBUNG = tusbung;

                    new CountDownTimer(3000, 1000) {
                        @Override
                        public void onTick(long l) {
                            System.out.println("Ticking ...");
                        }

                        @Override
                        public void onFinish() {
                            System.out.println("Finish ...");
                            SocketTransaction.getInstance().sendMessage(ParamDef.DO_TUSBUNG);
                        }
                    }.start();
                } else {
                    Log.d(TAG, "onTempUploadEvent: tidak ada foto");
                }

            } else {
                SmileyLoading.shouldCloseDialog();
                Log.d(TAG, "onTempUploadEvent: Done " + tusbung.getPart() + " dari " + tusbung.getJumlahFoto());
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTusbungRespond(final ProgressUpdateEvent progressEvent) {
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, progressEvent.toString());

        final String statusSinkron = progressEvent.isSuccess() ? Constants.SINKRONISASI_SUKSES : Constants.SINKRONISASI_GAGAL;

        if (statusSinkron.equals(Constants.SINKRONISASI_GAGAL)) {
            //todo I dont know what will going in here
        }

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                LocalDb.getInstance().where(Tusbung.class)
                        .equalTo("noWo", progressEvent.noWo, Case.INSENSITIVE)
                        .findFirst()
                        .setStatusSinkron(statusSinkron);
                LocalDb.getInstance().where(Tusbung.class)
                        .equalTo("noWo", progressEvent.noWo, Case.INSENSITIVE)
                        .findFirst()
                        .setKeteranganSinkron(StringUtils.normalize(progressEvent.message));
            }
        });

        mTusbungList.clear();
        mTusbungList.addAll(getDataLocal());
        mAdapter.notifyDataSetChanged();

        SynchUtils.writeSynchLog(SynchUtils.LOG_UPLOAD);
        SmileyLoading.shouldCloseDialog();
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
}
