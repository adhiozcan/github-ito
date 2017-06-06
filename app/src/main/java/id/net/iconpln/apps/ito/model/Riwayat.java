package id.net.iconpln.apps.ito.model;

import io.realm.RealmObject;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class Riwayat extends RealmObject {
    private long   unixTimeStamp;
    private String tanggal;
    private String waktu;
    private String jumlahData;

    public Riwayat() {
    }


    public Riwayat(long unixTimeStamp, String tanggal, String waktu) {
        this.unixTimeStamp = unixTimeStamp;
        this.tanggal = tanggal;
        this.waktu = waktu;
    }

    public long getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public void setUnixTimeStamp(long unixTimeStamp) {
        this.unixTimeStamp = unixTimeStamp;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getJumlahData() {
        return jumlahData;
    }

    public void setJumlahData(String jumlahData) {
        this.jumlahData = jumlahData;
    }

    @Override
    public String toString() {
        return "Riwayat{" +
                "tanggal='" + tanggal + '\'' +
                ", jumlahData='" + jumlahData + '\'' +
                '}';
    }
}
