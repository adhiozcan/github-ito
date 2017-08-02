package id.net.iconpln.apps.ito.job;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.ItoApplication;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.ProgressUpdateEvent;
import id.net.iconpln.apps.ito.model.eventbus.RefreshEvent;
import id.net.iconpln.apps.ito.model.eventbus.TempUploadEvent;
import id.net.iconpln.apps.ito.model.eventbus.WoUploadServiceEvent;
import id.net.iconpln.apps.ito.socket.ItoHttpClient;
import id.net.iconpln.apps.ito.socket.Param;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketAddress;
import id.net.iconpln.apps.ito.socket.SocketListener;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.SocketWatcher;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.utility.ImageUtils;
import id.net.iconpln.apps.ito.utility.NotifUtils;
import id.net.iconpln.apps.ito.utility.StringUtils;
import id.net.iconpln.apps.ito.utility.SynchUtils;
import io.realm.Case;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Ozcan on 19/07/2017.
 */

public class WoUploadService extends Service {
    private static final String TAG = WoUploadService.class.getSimpleName();

    // Services variables
    private static boolean isRunning = false;

    // Socket variables
    private SocketWatcher mSocketWatcher;

    // Tusbung varibles
    private List<Tusbung> mTusbungList     = new ArrayList<>();
    private int           mTusbungPosition = 0;
    private int           mTusbungTotal    = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    public void start() {
        SocketTransaction.shouldReinitSocket();
        performJob();
        isRunning = true;
    }

    public void stop() {
        stopSelf();
        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            start();
        } else {
            stop();
            start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stop();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void performJob() {
        System.out.println("-------------- [Performing Job] --------------");

        /*if (ItoApplication.isAppIsInBackground()) {
            Log.d(TAG, "onReceive: is app is in background : true");
        }*/

        makePreparationData();

        if (mTusbungTotal > 0) {
            if (mTusbungList.get(mTusbungPosition).getStatusSinkron().equals(Constants.SINKRONISASI_PENDING)) {
                Log.d(TAG, "Starting to upload on background state");
                AppConfig.TUSBUNG = mTusbungList.get(mTusbungPosition);
                AppConfig.TUSBUNG.setPart("1");
                upload();
            }
        }
    }

    private void makePreparationData() {
        mTusbungList.addAll(getDataLocal());
        mTusbungTotal = mTusbungList.size();

        System.out.println("[1/2] Preparing Data");
        for (Tusbung tusbung : mTusbungList) {
            System.out.println("\t0> " + tusbung.getNoWo());
        }
    }

    private List<Tusbung> getDataLocal() {
        Realm realm = Realm.getDefaultInstance();
        return realm.copyFromRealm(
                realm.where(Tusbung.class).findAll()
        );
    }

    private void upload() {
        System.out.println("[2/2] Start uploading data");
        SocketTransaction.getInstance().sendMessage(ParamDef.DO_TUSBUNG);
        mSocketWatcher = new SocketWatcher(new SocketWatcher.WatcherListener() {
            @Override
            public void onWatcherTimeOut() {
                AppConfig.TUSBUNG.setKeteranganSinkron("Timeout!");
            }
        });
        mSocketWatcher.monitor();
    }

    private Uri choosePhotoToUpload(int tempEventCounter) {
        Uri photo = null;
        switch (tempEventCounter) {
            case 1:
                photo = Uri.parse(AppConfig.TUSBUNG.getPhotoPath1());
                break;
            case 2:
                photo = Uri.parse(AppConfig.TUSBUNG.getPhotoPath2());
                break;
            case 3:
                photo = Uri.parse(AppConfig.TUSBUNG.getPhotoPath3());
                break;
            case 4:
                photo = Uri.parse(AppConfig.TUSBUNG.getPhotoPath4());
                break;
        }
        return photo;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onTempUploadEvent(TempUploadEvent tempEvent) {
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, "Return from tempuploadwo " + tempEvent.toString());

        if (tempEvent.next != null) {
            int tempEventCounter = Integer.parseInt(tempEvent.next);

            if (tempEventCounter <= Integer.parseInt(AppConfig.TUSBUNG.getJumlahFoto())) {
                Log.d(TAG, "doSinkronisasi: [Part Foto] " + AppConfig.TUSBUNG.getPart() + " dari " + AppConfig.TUSBUNG.getJumlahFoto());

                Uri photo = choosePhotoToUpload(tempEventCounter);
                if (photo != null) {
                    AppConfig.TUSBUNG.setBase64Foto(ImageUtils.getURLEncodeBase64(this, photo));
                    AppConfig.TUSBUNG.setPart(tempEvent.next);
                    upload();
                } else {
                    Log.d(TAG, "onTempUploadEvent: tidak ada foto");
                }

            } else {
                Log.d(TAG, "onTempUploadEvent: Done " + AppConfig.TUSBUNG.getPart() + " dari " + AppConfig.TUSBUNG.getJumlahFoto());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onTusbungRespond(final ProgressUpdateEvent progressEvent) {
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, progressEvent.toString());

        // stop socket watcher to prevent wrong alarm
        mSocketWatcher.stop();

        if (progressEvent.isSuccess()) {
            // remove the already successfull tusbung
            LocalDb.makeSafeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    System.out.println("making changes on successfull wo");
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

            // continue to upload the next data with first photo
            mTusbungPosition++;
            if (mTusbungPosition < mTusbungTotal) {
                Uri photo = choosePhotoToUpload(1);
                if (photo != null) {
                    AppConfig.TUSBUNG = mTusbungList.get(mTusbungPosition);
                    AppConfig.TUSBUNG.setBase64Foto(ImageUtils.getURLEncodeBase64(this, photo));
                    AppConfig.TUSBUNG.setPart("1");
                    upload();
                } else {
                    Log.d(TAG, "onTusbungRespond: tidak ada foto");
                }

            } else {
                Log.d(TAG, "--- Sinkronisasi successfully ---");
                NotifUtils.makeTrayNotification(this, "Sinkronisasi Work Order", "Sinkronisasi selesai dilakukan");
                EventBusProvider.getInstance().post(new WoUploadServiceEvent());
                mTusbungPosition = 0;
                mTusbungTotal = 0;
                mTusbungList.clear();
            }
        } else {
            /*LocalDb.makeSafeTransaction(new Realm.Transaction() {
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
            });*/

            LocalDb.makeSafeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    System.out.println("making changes on successfull wo");
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

            Log.d(TAG, "--- Sinkronisasi successfully ---");
            NotifUtils.makeTrayNotification(this, "Sinkronisasi Work Order", "Sinkronisasi selesai dilakukan");
            EventBusProvider.getInstance().post(new WoUploadServiceEvent());
            mTusbungPosition = 0;
            mTusbungTotal = 0;
            mTusbungList.clear();
        }

        EventBusProvider.getInstance().post(new RefreshEvent());
        SynchUtils.writeSynchLog(SynchUtils.LOG_UPLOAD, String.valueOf(getDataLocal().size()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketFailure(ErrorMessageEvent errorMessage) {
        System.out.println("Failed because of " + errorMessage.getMessage());
    }
}
