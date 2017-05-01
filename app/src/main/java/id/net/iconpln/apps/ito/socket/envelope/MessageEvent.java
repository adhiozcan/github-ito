package id.net.iconpln.apps.ito.socket.envelope;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class MessageEvent {
    @SerializedName("response")
    public String   response_code;
    @SerializedName("method")
    public String   method;
    @SerializedName("data")
    public Object[] entities;

    @Override
    public String toString() {
        return "MessageEvent{" +
                "response_code='" + response_code + '\'' +
                ", method='" + method + '\'' +
                ", value=" + Arrays.toString(entities) +
                '}';
    }
}
