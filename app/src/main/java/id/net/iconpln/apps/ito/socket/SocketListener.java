package id.net.iconpln.apps.ito.socket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.MessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.SocketEcho;
import id.net.iconpln.apps.ito.utility.SmileyLoading;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by Ozcan on 23/03/2017.
 */

class SocketListener extends WebSocketListener {
    private static String TAG = SocketListener.class.getSimpleName();

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d("SocketListener", "[Connect]\nConnected to websocket!");
        response.body().close();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "[Echo]: -");
        Log.d(TAG, "[Echo]: --");
        Log.d(TAG, "[Echo]: -----");
        Log.d(TAG, "[Echo]: -----------------------------------------------------------------");
        Log.d(TAG, "[Body] >>> " + text);
        Log.d(TAG, "[Echo]: -----------------------------------------------------------------");
        Log.d(TAG, "[Echo]: -----");
        Log.d(TAG, "[Echo]: --");
        Log.d(TAG, "[Echo]: -");

        SmileyLoading.close();

        Gson       gson       = new GsonBuilder().setLenient().setPrettyPrinting().create();
        SocketEcho socketEcho = gson.fromJson(text, SocketEcho.class);
        if (socketEcho.envelope != null)
            produceMessageEvent(socketEcho);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        System.out.println("[Closing] " + code + " " + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("[Shutdown] " + code + " Socket is closed due to " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.out.println("[Failure - Socket] " + t.getLocalizedMessage());
        if (t instanceof SocketTimeoutException) {
            System.out.println("Yes, it is SocketTimeoutException");
            MessageEvent message = new MessageEvent();
            message.response_code = Constants.SOCKET_FAILURE;
            new MessageDispatcher().dispatch(null, message);
            EventBusProvider.getInstance().post(new ErrorMessageEvent("Timeout! Gagal menghubungi server. Sementara itu, periksa jaringan Anda."));
        }
    }

    public void produceMessageEvent(SocketEcho echo) {
        Log.d(TAG, "[Dispatching Incoming Message]");
        String       runFunction  = echo.envelope[0].method;
        MessageEvent messageEvent = echo.envelope[0];
        new MessageDispatcher().dispatch(runFunction, messageEvent);
    }
}
