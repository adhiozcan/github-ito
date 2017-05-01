package id.net.iconpln.apps.ito.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class UserProfile extends Model {
    @SerializedName("kodepetugas")
    private String kodePetugas;
    @SerializedName("nama")
    private String nama;
    @SerializedName("unitap")
    private String unitap;
    @SerializedName("unitupi")
    private String unitupi;
    @SerializedName("unitup")
    private String unitup;
    @SerializedName("namaunitup")
    private String namaunitup;
    @SerializedName("jabatan")
    private String jabatan;
    @SerializedName("keyfcm")
    private String keyFcm;
    @SerializedName("keymap")
    private String keyMap;

    public String getKodePetugas() {
        return kodePetugas;
    }

    public void setKodePetugas(String kodePetugas) {
        this.kodePetugas = kodePetugas;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUnitap() {
        return unitap;
    }

    public void setUnitap(String unitap) {
        this.unitap = unitap;
    }

    public String getUnitupi() {
        return unitupi;
    }

    public void setUnitupi(String unitupi) {
        this.unitupi = unitupi;
    }

    public String getUnitup() {
        return unitup;
    }

    public void setUnitup(String unitup) {
        this.unitup = unitup;
    }

    public String getNamaunitup() {
        return namaunitup;
    }

    public void setNamaunitup(String namaunitup) {
        this.namaunitup = namaunitup;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getKeyFcm() {
        return keyFcm;
    }

    public void setKeyFcm(String keyFcm) {
        this.keyFcm = keyFcm;
    }

    public String getKeyMap() {
        return keyMap;
    }

    public void setKeyMap(String keyMap) {
        this.keyMap = keyMap;
    }

    public void formatPretty() {

    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "kodePetugas='" + kodePetugas + '\'' +
                ", nama='" + nama + '\'' +
                ", unitap='" + unitap + '\'' +
                ", unitupi='" + unitupi + '\'' +
                ", unitup='" + unitup + '\'' +
                ", namaunitup='" + namaunitup + '\'' +
                ", jabatan='" + jabatan + '\'' +
                ", keyFcm='" + keyFcm + '\'' +
                ", keyMap='" + keyMap + '\'' +
                '}';
    }
}
