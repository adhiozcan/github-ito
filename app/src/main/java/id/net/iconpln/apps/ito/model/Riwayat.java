package id.net.iconpln.apps.ito.model;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class Riwayat extends Model {
    private String tanggal;
    private String jumlahData;

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
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
