package id.net.iconpln.apps.ito.model.eventbus;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ozcan on 05/07/2017.
 */

public class TempUploadEvent {
    @SerializedName("next")
    public String next;
    @SerializedName("pesan")
    public String pesan;
    @SerializedName("kode")
    public String kode;
    @SerializedName("nowo")
    public String noWo;

    @Override
    public String toString() {
        return "TempUploadEvent{" +
                "next='" + next + '\'' +
                ", pesan='" + pesan + '\'' +
                ", kode='" + kode + '\'' +
                ", noWo='" + noWo + '\'' +
                '}';
    }
}
