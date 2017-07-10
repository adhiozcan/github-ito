package id.net.iconpln.apps.ito.socket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import id.net.iconpln.apps.ito.socket.envelope.MessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.SocketEcho;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by Ozcan on 05/07/2017.
 */

public class SocketEchoListener extends WebSocketListener {
    private static String TAG = SocketEchoListener.class.getSimpleName();

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d("SocketTransaction", "[Connect]\nConnected to websocket!");
        response.body().close();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "[Echo]: -----------------------------------------------------------------");
        Log.d(TAG, "[Body] >>> " + text);
        Log.d(TAG, "[Echo]: -----------------------------------------------------------------");

        Gson       gson       = new GsonBuilder().setLenient().setPrettyPrinting().create();
        SocketEcho socketEcho = gson.fromJson(text, SocketEcho.class);
        if (socketEcho.envelope != null)
            produceMessageEvent(socketEcho);
    }

    private void produceMessageEvent(SocketEcho echo) {
        Log.d(TAG, "[Dispatching Incoming Message]");
        String       runFunction  = echo.envelope[0].method;
        MessageEvent messageEvent = echo.envelope[0];
        new MessageDispatcher().dispatch(runFunction, messageEvent);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.out.println("[Failure - Socket] " + t.getLocalizedMessage());
    }

}
