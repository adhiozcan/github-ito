package id.net.iconpln.apps.ito.model;

import com.google.gson.annotations.SerializedName;

import id.net.iconpln.apps.ito.utility.StringUtils;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by Ozcan on 20/04/2017.
 */

public class FlagTusbung extends RealmObject {

    public FlagTusbung() {
    }

    @SerializedName("flag_tusbung")
    private String kode;
    @SerializedName("keterangan")
    private String keterangan;
    @SerializedName("deskripsi")
    private String deskripsi;

    public FlagTusbung(String kode, String keterangan, String deskripsi) {
        this.kode = kode;
        this.keterangan = keterangan;
        this.deskripsi = deskripsi;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void formatPretty() {
        this.kode = StringUtils.normalize(kode);
        this.keterangan = StringUtils.normalize(keterangan);
        this.deskripsi = StringUtils.normalize(deskripsi);
    }

    @Override
    public String toString() {
        return "FlagTusbung{" +
                "kode='" + kode + '\'' +
                ", keterangan='" + keterangan + '\'' +
                ", deskripsi='" + deskripsi + '\'' +
                '}';
    }
}
