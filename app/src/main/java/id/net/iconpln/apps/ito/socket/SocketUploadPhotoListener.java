package id.net.iconpln.apps.ito.socket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.model.eventbus.TempUploadEvent;
import id.net.iconpln.apps.ito.socket.envelope.MessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.SocketEcho;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by Ozcan on 05/07/2017.
 */

public class SocketUploadPhotoListener extends WebSocketListener {
    private static String TAG = SocketUploadPhotoListener.class.getSimpleName();

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d("SocketTransaction", "[Connect]\nConnected to websocket!");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "[Echo]: --");
        Log.d(TAG, "[Echo]: -----------------------------------------------------------------");
        Log.d(TAG, "[Body] >>> " + text);
        Log.d(TAG, "[Echo]: -----------------------------------------------------------------");
        Log.d(TAG, "[Echo]: --");

        Gson       gson       = new GsonBuilder().setLenient().setPrettyPrinting().create();
        SocketEcho socketEcho = gson.fromJson(text, SocketEcho.class);
        if (socketEcho.envelope != null)
            produceMessageEvent(socketEcho);
    }

    private void produceMessageEvent(SocketEcho echo) {
        Log.d(TAG, "[Dispatching Incoming Message]");
        String runFunction = echo.envelope[0].method;
        if (runFunction.equals("tempuploadwo")) {
            MessageEvent messageEvent = echo.envelope[0];
            messageEvent.entities[0].toString();
            EventBusProvider.getInstance().post(produceTempUploadWo(messageEvent.entities[0].toString()));
        }
    }

    /**
     * Produce temp upload wo
     *
     * @param message
     * @return
     */
    private TempUploadEvent produceTempUploadWo(String message) {
        TempUploadEvent tempEvent = new Gson().fromJson(message, TempUploadEvent.class);
        return tempEvent;
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        System.out.println("[Closing] " + code + " " + reason);
        webSocket.close(1000, null);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("[Shutdown] " + code + " Socket is closed due to " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.out.println("[Failure - Socket] " + t.getLocalizedMessage());
    }

}