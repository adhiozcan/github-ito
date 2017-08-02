package id.net.iconpln.apps.ito.socket;

import android.Manifest;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class SocketTransaction {
    private static final String TAG = SocketTransaction.class.getSimpleName();
    private static volatile SocketTransaction     socketTransaction;
    private static          OkHttpClient          client;
    private static          Request               request;
    private static          WebSocket             ws;
    private static          WeakReference<String> socketRequest;

    public static SocketTransaction getInstance() {
        // its called double check locking pattern
        if (socketTransaction == null) {
            synchronized (SocketTransaction.class) {
                if (socketTransaction == null) {
                    socketTransaction = new SocketTransaction();
                }
            }
        }

        if (ws == null || client == null) {
            start();
        }

        return socketTransaction;
    }

    public static void start() {
        //client = ItoHttpClient.getItoHttpClient();
        client = new OkHttpClient.Builder()
                .addInterceptor(provideLoggingAbility())
                .build();
        request = new Request.Builder().url(SocketAddress.SOCKET_ITO).build();
        ws = client.newWebSocket(request, new SocketListener());
        Log.d(TAG, "[Socket][OK] Socket Transaction has been start");
    }

    private static HttpLoggingInterceptor provideLoggingAbility() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    public static void close() {
        if (ws != null) {
            ws.close(1000, "Goodbye!");
        }
    }

    public static void shouldReinitSocket() {
        Log.d(TAG, "[Socket] Re init our socket");
        getInstance().stop();

        socketTransaction = null;
        socketTransaction.start();
    }

    public static void stop() {
        close();
        client.dispatcher().executorService().shutdown();

    }

    @RequiresPermission(Manifest.permission.INTERNET)
    public void sendMessage(@ParamDef.Param int service) {
        String param = null;
        switch (service) {
            case ParamDef.LOGIN:
                param = Param.login();
                break;
            case ParamDef.GET_MASTER_TUSBUNG:
                param = Param.getMasterTusbung();
                break;
            case ParamDef.GET_WO:
                //param = Param.getWoSync();
                param = Param.getWoAll();
                break;
            case ParamDef.GET_WO_ULANG:
                param = Param.getWoUlang();
                break;
            case ParamDef.GET_WO_CHART:
                param = Param.getWoChart();
                break;
            case ParamDef.GET_WO_MONITOR:
                param = Param.monitoring();
                break;
            case ParamDef.SET_WO:
                param = Param.markAsLocal();
                break;
            case ParamDef.DO_TUSBUNG:
                param = Param.doTusbung();
                break;
            case ParamDef.DO_TUSBUNG_ULANG:
                param = Param.doTusbungUlang();
                break;
        }

        socketRequest = new WeakReference<>(param);
        ws.send(socketRequest.get());

        Log.d(TAG, "[Send]: -----------------------------------------------------------------");
        Log.d(TAG, "[Send;]: >>>" + socketRequest.get());
    }

    /**
     * Disable object creation outside this class
     */
    private SocketTransaction() {
        if (socketTransaction != null) {
            throw new RuntimeException("Please use getInstance() to get the single instance of this class.");
        }
    }
}
