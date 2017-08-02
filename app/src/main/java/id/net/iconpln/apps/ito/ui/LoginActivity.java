package id.net.iconpln.apps.ito.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.config.NetworkConfig;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.helper.WorkOrderComparator;
import id.net.iconpln.apps.ito.job.WoUploadService;
import id.net.iconpln.apps.ito.model.FlagTusbung;
import id.net.iconpln.apps.ito.model.Statistic;
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.model.WoSummary;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.WoUploadServiceEvent;
import id.net.iconpln.apps.ito.model.eventbus.WorkOrderEvent;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.storage.StorageTransaction;
import id.net.iconpln.apps.ito.utility.DateUtils;
import id.net.iconpln.apps.ito.utility.DeviceUtils;
import id.net.iconpln.apps.ito.utility.NotifUtils;
import id.net.iconpln.apps.ito.utility.SmileyLoading;
import id.net.iconpln.apps.ito.utility.SynchUtils;
import io.realm.Realm;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();

    private boolean LOGIN_COMPLETE            = false;
    private boolean MASTER_TUSBUNG_COMPLETE   = false;
    private boolean WORK_ORDER_COMPLETE       = false;
    private boolean WORK_ORDER_ULANG_COMPLETE = false;

    private SocketTransaction socketTransaction;

    private EditText edUser;
    private EditText edPassword;
    private CheckBox chkRememberMe;
    private TextView txtAppVersion;
    private TextView txtLoadingDesc;
    private View     formLogin;
    private View     viewLoading;

    private List<WorkOrder> mWoBackupList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SocketTransaction.start();

        txtLoadingDesc = (TextView) findViewById(R.id.loading_description);
        formLogin = findViewById(R.id.login_form);
        viewLoading = findViewById(R.id.view_loading);

        edPassword = (EditText) findViewById(R.id.pin);
        edPassword.setTypeface(Typeface.DEFAULT);
        edPassword.setTransformationMethod(new PasswordTransformationMethod());

        txtAppVersion = (TextView) findViewById(R.id.app_version);
        txtAppVersion.setText(NetworkConfig.USER_AGENT);
        txtAppVersion.requestFocus();

        edUser = (EditText) findViewById(R.id.user);
        edUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    edUser.setError(null);
                }
            }
        });

        chkRememberMe = (CheckBox) findViewById(R.id.remember);
        chkRememberMe.setChecked(AppConfig.isRemember);
        chkRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                AppConfig.saveUserRememberConfiguration(value);
            }
        });

        setHardwareIdInfo();
        checkIfUserStillLoggedIn();

        edUser.setText("22100.KURNIA");
        edPassword.setText("icon+123");
    }


    private void setHardwareIdInfo() {
        NetworkConfig.MYDEBUG = DeviceUtils.getPhoneNumber(this);
        String   hd         = DeviceUtils.getPhoneNumber(this);
        TextView hardwareid = (TextView) findViewById(R.id.hardwareid);
        hardwareid.setText(hd);
    }

    private void checkIfUserStillLoggedIn() {
        String kodeUser = AppConfig.getUserInformation().getKodePetugas();
        if (!kodeUser.isEmpty()) {
            AppConfig.KODE_PETUGAS = AppConfig.getUserInformation().getKodePetugas();
            AppConfig.ID_UNIT_UP = AppConfig.getUserInformation().getUnitup();
            moveToDashboard();
        } else {
            checkUserIsRemember();
        }
    }

    private void checkUserIsRemember() {
        boolean isUserRemembered = AppConfig.isRemember;
        if (isUserRemembered) {
            Map<String, String> userInfo = AppConfig.getUserCredential();
            edUser.setText(userInfo.get(Constants.USERNAME));
            edPassword.setText(userInfo.get(Constants.PASSWORD));
        }
    }

    public void onRevealPassword(View viewId) {
        System.out.println("Posisi Tag : " + viewId.getTag());
        if (viewId.getTag() == "true") {
            edPassword.setTransformationMethod(new HideReturnsTransformationMethod());
            edPassword.setSelection(edPassword.getText().length());
            viewId.setTag("false");
        } else {
            edPassword.setTransformationMethod(new PasswordTransformationMethod());
            edPassword.setSelection(edPassword.getText().length());
            viewId.setTag("true");
        }
    }

    /**
     * User clicked the login button
     *
     * @param viewId
     */
    public void onLoginButtonClicked(View viewId) {
        socketTransaction = SocketTransaction.getInstance();

        //String user = "53581.akhyar" +"";
        //String password = "icon+123";
        String user     = edUser.getText().toString();
        String password = edPassword.getText().toString();

        if (validateInput(user, password))
            doLogin(user, password);
    }

    private boolean validateInput(String username, String password) {
        if (username.equals("")) {
            edUser.setError("Username tidak boleh kosong");
            return false;
        }
        if (password.equals("")) {
            edUser.setError("Password tidak boleh kosong");
            return false;
        }
        return true;
    }

    private void doLogin(String username, String password) {
        SmileyLoading.show(this, "Mohon tunggu sebentar", 10_000, new SmileyLoading.LoadingTimer() {
            @Override
            public void onTimeout() {
                NotifUtils.makePinkSnackbar(LoginActivity.this, "Timeout! Coba beberapa saat lagi").show();
            }
        });

        Log.d(TAG, "Im trying to send new message using formal format, please wait :)");
        AppConfig.USERNAME = username;
        AppConfig.PASSWORD = password;
        socketTransaction.sendMessage(ParamDef.LOGIN);
    }

    private boolean isLoginSuccess(UserProfile userProfile) {
        if (userProfile.getKodePetugas() == null || userProfile.getKodePetugas().isEmpty()) {
            return false;
        }
        return true;
    }

    private void onLoginSuccess(UserProfile userProfile) {
        hideLoginFormView();

        AppConfig.saveUserInformation(userProfile);

        if (AppConfig.isRemember) {
            AppConfig.saveUserCredential(AppConfig.USERNAME, AppConfig.PASSWORD);
        }

        backupWoSelesai();

        /**
         * Save user information into local
         */
        StorageTransaction<UserProfile> storageTransaction = new StorageTransaction<>();
        storageTransaction.save(UserProfile.class, userProfile);

        /**
         * Save user information on application session
         */
        AppConfig.KODE_PETUGAS = userProfile.getKodePetugas();
        AppConfig.ID_UNIT_UP = userProfile.getUnitup();

        /**
         * Doing many request call. Intended to checking new data on application on beginning.
         */
        txtLoadingDesc.setText("Memperbarui data master tusbung...");
        socketTransaction.sendMessage(ParamDef.GET_MASTER_TUSBUNG);
    }

    /**
     * Hide login form and start to request important data.
     */
    private void hideLoginFormView() {
        formLogin.setVisibility(View.GONE);
        viewLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Listen when all data has complete.
     * There are 3 request : login, master tusbung and work order.
     *
     * @return boolean
     */
    private void listenDataToComplete() {
        if (LOGIN_COMPLETE && MASTER_TUSBUNG_COMPLETE && WORK_ORDER_COMPLETE && WORK_ORDER_ULANG_COMPLETE) {
            moveToDashboard();
        }
    }

    private void moveToDashboard() {
        startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
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

    @Override
    protected void onStart() {
        super.onStart();
        EventBusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusProvider.getInstance().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginResponse(UserProfile userProfile) {
        Log.d(TAG, "onLoginResponse: ------------------------------------------------------------");
        Log.d(TAG, userProfile.toString());
        SmileyLoading.shouldCloseDialog();

        AppConfig.TANGGAL = userProfile.getTanggalServer();
        DateUtils.saveDateFromLogin(userProfile.getTanggalServer());
        System.out.println(DateUtils.getDateFromLogin());
        System.out.println("Tanggal tercatat");

        if (isLoginSuccess(userProfile)) {
            String namaUnitUpi = userProfile.getNamaunitup();
            if (userProfile.getUnitupi().trim().toLowerCase().equals("54")) {
                userProfile.setNamaunitup("Ar. " + namaUnitUpi);
            } else {
                userProfile.setNamaunitup("Ry. " + namaUnitUpi);
            }
            onLoginSuccess(userProfile);
            LOGIN_COMPLETE = true;
            listenDataToComplete();
        } else {
            NotifUtils.makePinkSnackbar(this, "Username atau Password tidak ditemukan").show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFlagTusbungResponse(final FlagTusbung[] flagTusbung) {
        Log.d(TAG, "[Flag Tusbung] " + flagTusbung.length + "--------------------");

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(FlagTusbung.class);
                realm.copyToRealmOrUpdate(Arrays.asList(flagTusbung));
            }
        });

        MASTER_TUSBUNG_COMPLETE = true;
        listenDataToComplete();

        /*txtLoadingDesc.setText("Memperbarui data chart...");
        socketTransaction.sendMessage(ParamDef.GET_WO_CHART);*/

        txtLoadingDesc.setText("Memperbarui data wo pelaksanaan...");
        socketTransaction.sendMessage(ParamDef.GET_WO);
        socketTransaction.sendMessage(ParamDef.GET_WO_ULANG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWoSummaryResponse(WoSummary woSummary) {
        txtLoadingDesc.setText("Memperbarui data wo pelaksanaan...");
        socketTransaction.sendMessage(ParamDef.GET_WO);
        socketTransaction.sendMessage(ParamDef.GET_WO_ULANG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataWoReceived(final WorkOrderEvent woEvent) {
        Log.d(TAG, "onDataWoReceived : " + AppConfig.KODE_PETUGAS);
        Log.d(TAG, "------------------------------------------");
        if (woEvent.workOrders.length > 0)
            System.out.println("Wo Kode Petugas : " + woEvent.workOrders[0].getKodePetugas());
        Log.d(TAG, "Wo ulang : " + woEvent.isUlang + ", Total Data : " + woEvent.workOrders.length);
        Log.d(TAG, "Total Backup data : " + mWoBackupList.size());

        txtLoadingDesc.setText("Finishing...");

        /*List<WorkOrder> localWoData = LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("isWoUlang", false)
                        .findAll());

        List<WorkOrder> localWoUlangData = LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("isWoUlang", true)
                        .findAll());

        final List<WorkOrder> woFromServer = Arrays.asList(woEvent.workOrders);
        final List<WorkOrder> woClean      = new ArrayList<>();

        System.out.println("[Starting] Proses pembandingan");
        WorkOrderComparator comparator = new WorkOrderComparator();
        if (woEvent.isUlang) {
            for (WorkOrder serverWo : woFromServer) {
                boolean found = false;
                for (WorkOrder localWo : localWoUlangData) {
                    if (comparator.compare(localWo, serverWo) == 1) {
                        System.out.println("Data sama " + serverWo.getNoWo());
                        found = true;
                    }
                }
                System.out.println("Status found : " + found);
                if (!found) {
                    System.out.println("Menambahkan wo baru : " + serverWo.getNoWo());
                    woClean.add(serverWo);
                }
            }

            System.out.println("Data wo yang akan ditambahkan " + woClean.size());
            System.out.println("-----------------------------------------");

        } else {

            for (WorkOrder serverWo : woFromServer) {
                boolean found = false;
                for (WorkOrder localWo : localWoData) {
                    if (comparator.compare(localWo, serverWo) == 1) {
                        System.out.println("Data sama " + serverWo.getNoWo());
                        found = true;
                    }
                }
                System.out.println("Status found : " + found);
                if (!found) {
                    System.out.println("Menambahkan wo baru : " + serverWo.getNoWo());
                    woClean.add(serverWo);
                }
            }

            System.out.println("Data wo yang akan ditambahkan " + woClean.size());
            System.out.println("-----------------------------------------");
        }*/

        final List<WorkOrder> woFromServer = Arrays.asList(woEvent.workOrders);
        final List<WorkOrder> woClean      = new ArrayList<>();
        woClean.addAll(mWoBackupList);

        System.out.println("[Starting] Proses pembandingan");
        WorkOrderComparator comparator = new WorkOrderComparator();
        if (woEvent.isUlang) {
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

        } else {
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
        }

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < woClean.size(); i++) {
                    realm.insertOrUpdate(woClean);
                }
            }
        });

        StorageTransaction<Statistic> transaction = new StorageTransaction<>();
        Statistic                     statistic   = transaction.find(Statistic.class);
        if (woEvent.isUlang) {
            statistic.setSumWoUlang(woEvent.workOrders.length);

            WORK_ORDER_ULANG_COMPLETE = true;
            listenDataToComplete();
        } else {
            statistic.setSumWo(woEvent.workOrders.length);

            WORK_ORDER_COMPLETE = true;
            listenDataToComplete();
        }

        transaction.save(Statistic.class, statistic);
        SynchUtils.writeSynchLog(SynchUtils.LOG_UNDUH, String.valueOf(woEvent.workOrders.length));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onError(ErrorMessageEvent messageEvent) {
        SmileyLoading.shouldCloseDialog();
        NotifUtils.makePinkSnackbar(this, messageEvent.getMessage()).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUploadBackgroundFinished(WoUploadServiceEvent wusEvent) {
        this.stopService(new Intent(this, WoUploadService.class));
        System.out.println("[Service] Stop uploading in the background");
    }
}
