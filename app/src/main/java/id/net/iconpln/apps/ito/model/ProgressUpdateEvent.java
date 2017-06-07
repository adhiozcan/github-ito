package id.net.iconpln.apps.ito.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ozcan on 07/06/2017.
 */

public class ProgressUpdateEvent {
    @SerializedName("kode")
    public String kode;
    @SerializedName("pesan")
    public String message;

    @Override
    public String toString() {
        return "ProgressUpdateEvent{" +
                "kode='" + kode + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
