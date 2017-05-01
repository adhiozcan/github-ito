package id.net.iconpln.apps.ito.socket;

import android.Manifest;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.lang.ref.WeakReference;

import id.net.iconpln.apps.ito.config.AppConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class SocketTransaction {
    private static final String TAG = SocketTransaction.class.getSimpleName();
    private static volatile SocketTransaction     socketTransaction;
    private static          OkHttpClient          client;
    private static          Request               request;
    private static          WebSocket             ws;
    private static          SocketListener        listener;
    private static          WeakReference<String> socketRequest;

    public static SocketTransaction prepareStatement() {
        // its called double check locking pattern
        if (socketTransaction == null) {
            synchronized (SocketTransaction.class) {
                if (socketTransaction == null) socketTransaction = new SocketTransaction();
            }
        }
        if (ws == null)
            throw new NullPointerException("Tidak dapat menggunakan websocket, instance belum terinisialisasi");
        if (client == null)
            throw new NullPointerException("Tidak dapat menggunakan socket tanpa Http Client");
        if (listener == null)
            throw new NullPointerException("tidak dapat menggunakan socket tanpa listener");
        return socketTransaction;
    }

    public static void init() {
        listener = new SocketListener();
        client = ItoHttpClient.getItoHttpClient();
        request = new Request.Builder().url(SocketAddress.SOCKET_ITO).build();
        ws = client.newWebSocket(request, listener);
        Log.d(TAG, "[Success] Socket Transaction has been init");
    }

    public static void doReInit() {
        listener = null;
        client = null;
        request = null;
        ws = null;

        Log.d(TAG, "We will re-init our socket.");
        init();
    }

    public void close() {
        ws.close(1000, "Socket will be terminated");
        client.dispatcher().executorService().shutdown();
    }

    private void composeParam(String parameter) {
        socketRequest = new WeakReference<>(parameter);
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    public void sendMessage(@ParamDef.Param int service) {
        switch (service) {
            case ParamDef.LOGIN:
                composeParam(Param.login());
                break;
            case ParamDef.GET_MASTER_TUSBUNG:
                composeParam(Param.getMasterTusbung());
                break;
            case ParamDef.GET_WO:
                composeParam(Param.getWoSync());
                break;
            case ParamDef.GET_WO_CHART:
                composeParam(Param.getWoChart());
                break;
            case ParamDef.GET_WO_MONITOR:
                composeParam(Param.monitoring());
                break;
            case ParamDef.SET_WO:
                composeParam(Param.markAsLocal());
                break;
            case ParamDef.DO_TUSBUNG:
                composeParam(Param.doTusbung());
                break;
        }
        ws.send(socketRequest.get());
        Log.d(TAG, "[Send]: -");
        Log.d(TAG, "[Send]: --");
        Log.d(TAG, "[Send]: -----");
        Log.d(TAG, "[Send]: -----------------------------------------------------------------");
        Log.d(TAG, "[Body]: >>>" + socketRequest.get());
        Log.d(TAG, "[Send]: -----------------------------------------------------------------");
        Log.d(TAG, "[Send]: -----");
        Log.d(TAG, "[Send]: --");
        Log.d(TAG, "[Send]: -");
    }

    /**
     * Disable object creation outside this class
     */
    private SocketTransaction() {
        if (socketTransaction != null) {
            throw new RuntimeException("Please use prepareStatement() to get the single instance of this class.");
        }
    }
}
