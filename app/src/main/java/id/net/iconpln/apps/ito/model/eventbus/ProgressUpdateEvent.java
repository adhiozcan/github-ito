package id.net.iconpln.apps.ito.model.eventbus;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ozcan on 07/06/2017.
 */

public class ProgressUpdateEvent {
    @SerializedName("kode")
    public String  kode;
    @SerializedName("pesan")
    public String  message;
    @SerializedName("nowo")
    public String  noWo;
    public boolean isUlang;

    public boolean isSuccess() {
        return kode.equals("1") ? true : false;
    }

    @Override
    public String toString() {
        return "ProgressUpdateEvent{" +
                "kode='" + kode + '\'' +
                ", message='" + message + '\'' +
                ", noWo='" + noWo + '\'' +
                ", isUlang=" + isUlang +
                '}';
    }
}
