package id.net.iconpln.apps.ito.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.ItoApplication;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.job.WoUploadService;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.NetworkAvailabilityEvent;
import id.net.iconpln.apps.ito.model.eventbus.ProgressUpdateEvent;
import id.net.iconpln.apps.ito.model.eventbus.RefreshEvent;
import id.net.iconpln.apps.ito.model.eventbus.TempUploadEvent;
import id.net.iconpln.apps.ito.model.eventbus.WoUploadServiceEvent;
import id.net.iconpln.apps.ito.socket.Param;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketAddress;
import id.net.iconpln.apps.ito.socket.SocketListener;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.SocketWatcher;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.PingEvent;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.utility.ConnectivityUtils;
import id.net.iconpln.apps.ito.utility.DateUtils;
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
 * Created by Ozcan on 12/07/2017.
 */

public class ConnectionListener extends BroadcastReceiver {
    private static String TAG = ConnectionListener.class.getSimpleName();
    // Socket variables
    private SocketWatcher mSocketWatcher;
    // Tusbung varibles
    private List<Tusbung> mTusbungList     = new ArrayList<>();
    private int           mTusbungPosition = 0;
    private int           mTusbungTotal    = 0;

    private Context mContext;

    private static boolean isUploadRunning     = false;
    private static boolean readySendingMessage = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        EventBusProvider.getInstance().register(this);
        this.mContext = context;
        if (ConnectivityUtils.isHaveInternetConnection(context)) {
            System.out.println("We've got internet access");
            EventBusProvider.getInstance().post(new NetworkAvailabilityEvent(ConnectivityUtils.INTERNET_OK));

            System.out.println("Upload Running " + isUploadRunning + "----------------------------------------");

            if (!readySendingMessage) {
                SocketTransaction.shouldReinitSocket(new SocketListener.SocketState() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Good News!");
                        makePreparationData();
                        if (mTusbungTotal > 0 && isUploadRunning == false) {
                            performJob();
                        }
                    }

                    @Override
                    public void onFailed() {
                        System.out.println("Bad News!");
                    }
                });
            }
            makePreparationData();
            if (mTusbungTotal > 0 && isUploadRunning == false) {
                performJob();
            }
        } else {
            System.out.println("We dont have any internet access");
            EventBusProvider.getInstance().post(new NetworkAvailabilityEvent(ConnectivityUtils.INTERNET_NOT_OK));
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

    public void performJob() {
        System.out.println("-------------- [Performing Job] --------------");
        isUploadRunning = true;

        if (mTusbungList.get(mTusbungPosition).getStatusSinkron().equals(Constants.SINKRONISASI_PENDING)) {
            Log.d(TAG, "Starting to upload on background state");

            Tusbung tusbung = mTusbungList.get(mTusbungPosition);

            Pattern p = Pattern.compile("[^A-Za-z0-9]");
            Matcher m = p.matcher(tusbung.getNamaPetugas());
            if (m.find()) {
                System.out.println("Nama mengandung escape character");
                String nameNormalize = tusbung.getNamaPetugas().replace("\"", "");
                tusbung.setNamaPetugas(nameNormalize);
            }

            AppConfig.TUSBUNG = tusbung;
            AppConfig.TUSBUNG.setPart("1");

            upload();
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
                if (isUploadRunning == true) {
                    System.out.println("isUploadRunning");
                    if (getDataLocal().size() > 0) {
                        performJob();
                    } else {
                        System.out.println("Tidak ada sinkronisasi lagi");
                    }
                }
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
                    AppConfig.TUSBUNG.setBase64Foto(ImageUtils.getURLEncodeBase64(mContext, photo));
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
        if (mSocketWatcher != null)
            mSocketWatcher.stop();

        if (progressEvent.isSuccess()) {
            // remove the already successful tusbung
            LocalDb.makeSafeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    System.out.println("Update changes on successfull wo");
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
                    AppConfig.TUSBUNG.setBase64Foto(ImageUtils.getURLEncodeBase64(mContext, photo));
                    AppConfig.TUSBUNG.setPart("1");

                    Pattern p = Pattern.compile("[^A-Za-z0-9]");
                    Matcher m = p.matcher(AppConfig.TUSBUNG.getNamaPetugas());
                    if (m.find()) {
                        System.out.println("Nama mengandung escape character");
                        String nameNormalize = AppConfig.TUSBUNG.getNamaPetugas().replace("\"", "");
                        AppConfig.TUSBUNG.setNamaPetugas(nameNormalize);
                    }

                    upload();
                } else {
                    Log.d(TAG, "onTusbungRespond: tidak ada foto");
                }

            } else {
                Log.d(TAG, "--- Sinkronisasi successfully ---");
                NotifUtils.makeTrayNotification(mContext, "Sinkronisasi Work Order", "Sinkronisasi selesai dilakukan");
                EventBusProvider.getInstance().post(new WoUploadServiceEvent());
                mTusbungPosition = 0;
                mTusbungTotal = 0;
                mTusbungList.clear();
                isUploadRunning = false;
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
            System.out.println("Gagal mengupload wo : " + progressEvent.noWo);
        }

        EventBusProvider.getInstance().post(new RefreshEvent());
        SynchUtils.writeSynchLog(SynchUtils.LOG_UPLOAD, String.valueOf(getDataLocal().size()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketFailure(ErrorMessageEvent errorMessage) {
        System.out.println("Failed because of " + errorMessage.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPingReceived(PingEvent pingEvent) {
        Date   date       = DateUtils.parseToDate(pingEvent.getDate());
        String dateString = DateUtils.parseToString(date);
        readySendingMessage = true;
    }
}
