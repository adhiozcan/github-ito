package id.net.iconpln.apps.ito.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import java.util.Arrays;
import java.util.Map;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.config.NetworkConfig;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.model.FlagTusbung;
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.model.WoSummary;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.storage.StorageTransaction;
import id.net.iconpln.apps.ito.utility.DeviceUtils;
import id.net.iconpln.apps.ito.utility.NotifUtils;
import id.net.iconpln.apps.ito.utility.SmileyLoading;
import id.net.iconpln.apps.ito.utility.SynchUtils;
import io.realm.Realm;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login";

    private EditText edUser;
    private EditText edPassword;
    private CheckBox chkRememberMe;
    private TextView txtAppVersion;
    private TextView txtLoadingDesc;
    private View     formLogin;
    private View     viewLoading;

    private SocketTransaction socketTransaction;

    private boolean LOGIN_COMPLETE            = false;
    private boolean MASTER_TUSBUNG_COMPLETE   = false;
    private boolean WORK_ORDER_COMPLETE       = false;
    private boolean WORK_ORDER_ULANG_COMPLETE = false;

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
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppConfig.saveUserRememberConfiguration(b);
            }
        });

        setHardwareIdInfo();
        checkIfUserStillLoggedIn();
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
        SmileyLoading.show(this, "Mohon tunggu sebentar.");
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
            SynchUtils.writeSynchLog(SynchUtils.LOG_UNDUH);
            moveToDashboard();
        }
    }

    private void moveToDashboard() {
        startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginResponse(UserProfile userProfile) {
        Log.d(TAG, "onLoginResponse: ------------------------------------------------------------");
        Log.d(TAG, userProfile.toString());
        SmileyLoading.shouldCloseDialog();

        if (isLoginSuccess(userProfile)) {
            onLoginSuccess(userProfile);
            LOGIN_COMPLETE = true;
            listenDataToComplete();
        } else {
            Snackbar snackbar = NotifUtils.makePinkSnackbar(this, "Username atau Password tidak ditemukan");
            snackbar.show();
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

        txtLoadingDesc.setText("Memperbarui data chart...");
        socketTransaction.sendMessage(ParamDef.GET_WO_CHART);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWoSummaryResponse(WoSummary woSummary) {
        txtLoadingDesc.setText("Memperbarui data wo pelaksanaan ulang...");
        socketTransaction.sendMessage(ParamDef.GET_WO_ULANG);

        WORK_ORDER_COMPLETE = true;
        listenDataToComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataWoUlangResponse(final WorkOrder[] workOrders) {
        Log.d(TAG, "[Wo Ulang] " + workOrders.length + "--------------------");
        txtLoadingDesc.setText("Data complete...");

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (WorkOrder wo : workOrders) {
                    wo.setWoUlang(true);
                    realm.insertOrUpdate(wo);
                }
            }
        });

        WORK_ORDER_ULANG_COMPLETE = true;
        listenDataToComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onError(ErrorMessageEvent messageEvent) {
        SmileyLoading.shouldCloseDialog();
        Snackbar snackbar = NotifUtils.makePinkSnackbar(this, messageEvent.getMessage());
        snackbar.show();
    }
}
