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
    @SerializedName("nowo")
    public String noWo;

    public boolean isSuccess() {
        return kode == "1" ? true : false;
    }

    @Override
    public String toString() {
        return "ProgressUpdateEvent{" +
                "kode='" + kode + '\'' +
                ", message='" + message + '\'' +
                ", noWo='" + noWo + '\'' +
                '}';
    }
}
