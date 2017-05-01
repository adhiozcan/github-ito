package id.net.iconpln.apps.ito.socket.envelope;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class SocketEcho {
    @SerializedName("prop")
    public String prop;
    @SerializedName("etype")
    public String etype;

    @SerializedName("value")
    public MessageEvent[] envelope;

    @Override
    public String toString() {
        return "SocketEcho{" +
                "prop='" + prop + '\'' +
                ", etype='" + etype + '\'' +
                ", envelope=" + Arrays.toString(envelope) +
                '}';
    }
}
