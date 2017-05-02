package id.net.iconpln.apps.ito.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.config.NetworkConfig;
import id.net.iconpln.apps.ito.helper.CheckPermission;
import id.net.iconpln.apps.ito.model.FlagTusbung;
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.model.WoSummary;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.storage.StorageTransaction;
import id.net.iconpln.apps.ito.storage.entity.FlagTusbugStorage;
import id.net.iconpln.apps.ito.utility.DeviceUtils;
import id.net.iconpln.apps.ito.utility.SmileyLoading;
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
    private View     formLogin;
    private View     viewLoading;

    private SocketTransaction socketTransaction;

    private boolean LOGIN_COMPLETE          = false;
    private boolean MASTER_TUSBUNG_COMPLETE = false;
    private boolean WORK_ORDER_COMPLETE     = false;

    private Realm realm;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        realm = Realm.getDefaultInstance();

        socketTransaction = SocketTransaction.prepareStatement();

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
        chkRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppConfig.saveUserRememberConfiguration(b);
            }
        });

        setHardwareIdInfo();
        checkUserIsRemember();
    }

    private void setHardwareIdInfo() {
        NetworkConfig.MYDEBUG = DeviceUtils.getPhoneNumber(this);
        String   hd         = DeviceUtils.getPhoneNumber(this);
        TextView hardwareid = (TextView) findViewById(R.id.hardwareid);
        hardwareid.setText(hd);
    }

    private void checkUserIsRemember() {
        boolean isUserRemembered = AppConfig.isRemember;
        if (isUserRemembered) {
            // check is user logged on
            if (!AppConfig.ID_UNIT_UP.isEmpty() && !AppConfig.KODE_PETUGAS.isEmpty())
                startActivity(new Intent(this, MainActivity.class));
        }
    }

    public void onRevealPassword(View viewId) {
        TransformationMethod passwordStyle = edPassword.getTransformationMethod();
        if (passwordStyle == PasswordTransformationMethod.getInstance())
            edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        if (passwordStyle == HideReturnsTransformationMethod.getInstance())
            edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

    }

    /**
     * User clicked the login button
     *
     * @param viewId
     */
    public void onLoginButtonClicked(View viewId) {
        String user     = "53581.akhyar";
        String password = "icon+123";
        //String user     = edUser.getText().toString();
        //String password = edPassword.getText().toString();

        if (validateInput(user, password))
            doLogin(user, password);
    }

    private boolean isReadExternalAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
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
        socketTransaction.sendMessage(ParamDef.GET_MASTER_TUSBUNG);
        socketTransaction.sendMessage(ParamDef.GET_WO_CHART);
    }

    /**
     * Hide login form and start to request important data.
     */
    private void hideLoginFormView() {
        formLogin.setVisibility(View.GONE);
        viewLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Show login form and hide loading view. Intended to use when login activity
     * need to be reset into default.
     */
    private void clearViewStuff() {
        formLogin.setVisibility(View.VISIBLE);
        viewLoading.setVisibility(View.GONE);
    }

    /**
     * Listen when all data has complete.
     * There are 3 request : login, master tusbung and work order.
     *
     * @return boolean
     */
    private void listenDataToComplete() {
        if (LOGIN_COMPLETE && MASTER_TUSBUNG_COMPLETE && WORK_ORDER_COMPLETE) {
            moveToDashboard();
        }
    }

    private void moveToDashboard() {
        startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        //clearViewStuff();
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
        SmileyLoading.close();

        if (isLoginSuccess(userProfile)) {
            onLoginSuccess(userProfile);
            LOGIN_COMPLETE = true;
            listenDataToComplete();
        } else {
            Toast.makeText(this, "Username atau password tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFlagTusbungResponse(FlagTusbung[] flagTusbung) {
        Log.d(TAG, "[Flag Tusbung] " + flagTusbung.length + "--------------------");
        MASTER_TUSBUNG_COMPLETE = true;
        listenDataToComplete();

        //TODO save to Realm in here <DONE>
        realm.beginTransaction();
        for (FlagTusbung aFlagTusbung : flagTusbung) {
            realm.insert(aFlagTusbung);
        }
        realm.commitTransaction();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWoSummaryResponse(WoSummary woSummary) {
        WORK_ORDER_COMPLETE = true;
        listenDataToComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketFailure(ErrorMessageEvent messageEvent) {
        Toast.makeText(this, messageEvent.getMessage(), Toast.LENGTH_SHORT).show();
        clearViewStuff();
    }
}
