package id.net.iconpln.apps.ito.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Ozcan on 12/04/2017.
 */

public class SocketKeeper {

    public static boolean isSocketReachable() {
        String  serverAddress = SocketAddress.SOCKET_ITO;
        int     port          = 8041;
        int     reachableMs   = 1_000;
        boolean connected     = false;

        Socket socket;
        try {
            socket = new Socket();
            java.net.SocketAddress socketAddress = new InetSocketAddress(serverAddress, port);
            socket.connect(socketAddress, reachableMs);
            if (socket.isConnected()) {
                connected = true;
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket = null;
        }
        return connected;
    }
}
