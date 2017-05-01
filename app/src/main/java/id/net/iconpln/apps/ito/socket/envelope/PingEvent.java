package id.net.iconpln.apps.ito.socket.envelope;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ozcan on 07/04/2017.
 */

public class PingEvent {
    @SerializedName("tgl")
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
