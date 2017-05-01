package id.net.iconpln.apps.ito.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ozcan on 27/03/2017.
 */

public class WoSummary extends Model {
    @SerializedName("belum_putus_sudah_lunas")
    private String belumPutusSudahLunas;
    @SerializedName("belum_bongkar")
    private String belumBongkar;
    @SerializedName("gagal_putus")
    private String gagalPutus;
    @SerializedName("gagal_bongkar")
    private String gagalBongkar;
    @SerializedName("sudah_putus")
    private String sudahputus;
    @SerializedName("sambung")
    private String sambung;
    @SerializedName("gagal_sambung")
    private String gagalSambung;
    @SerializedName("belum_putus")
    private String belumPutus;
    @SerializedName("bongkar")
    private String bongkar;
    @SerializedName("sudah_putus_sudah_lunas")
    private String sudahPutusSudahLunas;

    @Expose
    private String total;

    public String getBelumPutusSudahLunas() {
        return belumPutusSudahLunas;
    }

    public void setBelumPutusSudahLunas(String belumPutusSudahLunas) {
        this.belumPutusSudahLunas = belumPutusSudahLunas;
    }

    public String getBelumBongkar() {
        return belumBongkar;
    }

    public void setBelumBongkar(String belumBongkar) {
        this.belumBongkar = belumBongkar;
    }

    public String getGagalPutus() {
        return gagalPutus;
    }

    public void setGagalPutus(String gagalPutus) {
        this.gagalPutus = gagalPutus;
    }

    public String getGagalBongkar() {
        return gagalBongkar;
    }

    public void setGagalBongkar(String gagalBongkar) {
        this.gagalBongkar = gagalBongkar;
    }

    public String getSudahputus() {
        return sudahputus;
    }

    public void setSudahputus(String sudahputus) {
        this.sudahputus = sudahputus;
    }

    public String getSambung() {
        return sambung;
    }

    public void setSambung(String sambung) {
        this.sambung = sambung;
    }

    public String getGagalSambung() {
        return gagalSambung;
    }

    public void setGagalSambung(String gagalSambung) {
        this.gagalSambung = gagalSambung;
    }

    public String getBelumPutus() {
        return belumPutus;
    }

    public void setBelumPutus(String belumPutus) {
        this.belumPutus = belumPutus;
    }

    public String getBongkar() {
        return bongkar;
    }

    public void setBongkar(String bongkar) {
        this.bongkar = bongkar;
    }

    public String getSudahPutusSudahLunas() {
        return sudahPutusSudahLunas;
    }

    public void setSudahPutusSudahLunas(String sudahPutusSudahLunas) {
        this.sudahPutusSudahLunas = sudahPutusSudahLunas;
    }

    public String countTotal() {
        int belumPutusSudahLunas = checkNull(this.belumPutusSudahLunas);
        int belumBongkar         = checkNull(this.belumBongkar);
        int gagalPutus           = checkNull(this.gagalPutus);
        int gagalBongkar         = checkNull(this.gagalBongkar);
        int sudahputus           = checkNull(this.sudahputus);
        int sambung              = checkNull(this.sambung);
        int gagalSambung         = checkNull(this.gagalSambung);
        int belumPutus           = checkNull(this.belumPutus);
        int bongkar              = checkNull(this.bongkar);
        int sudahPutusSudahLunas = checkNull(this.sudahPutusSudahLunas);

        int summary = belumPutusSudahLunas
                + belumBongkar
                + gagalPutus
                + gagalBongkar
                + sudahputus
                + sambung
                + sambung
                + gagalSambung
                + belumPutus
                + bongkar
                + sudahPutusSudahLunas;

        return Integer.toString(summary);
    }

    private int checkNull(String object) {
        if (object == null) return 0;
        return Integer.parseInt(object);
    }

    @Override
    public String toString() {
        return "WoSummary{" +
                "belumPutusSudahLunas='" + belumPutusSudahLunas + '\'' +
                ", belumBongkar='" + belumBongkar + '\'' +
                ", gagalPutus='" + gagalPutus + '\'' +
                ", gagalBongkar='" + gagalBongkar + '\'' +
                ", sudahputus='" + sudahputus + '\'' +
                ", sambung='" + sambung + '\'' +
                ", gagalSambung='" + gagalSambung + '\'' +
                ", belumPutus='" + belumPutus + '\'' +
                ", bongkar='" + bongkar + '\'' +
                ", sudahPutusSudahLunas='" + sudahPutusSudahLunas + '\'' +
                '}';
    }

    public void formatPretty() {

    }
}
