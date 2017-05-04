package id.net.iconpln.apps.ito.model;

import com.google.gson.annotations.SerializedName;

import id.net.iconpln.apps.ito.utility.StringUtils;

/**
 * Created by Ozcan on 03/05/2017.
 */

public class WoMonitoring {
    @SerializedName("lembar_601")
    private String lembar601;
    @SerializedName("no_601")
    private String no601;
    @SerializedName("no_tul601")
    private String noTul601;
    @SerializedName("tagihan_601")
    private String tagihan601;
    @SerializedName("no_wo")
    private String noWorkOrder;
    @SerializedName("tgl_pelunasan_601")
    private String tanggalPelunasan;
    @SerializedName("idpelanggan")
    private String idPelanggan;
    @SerializedName("unitupi")
    private String unitUpi;
    @SerializedName("unitap")
    private String unitAp;
    @SerializedName("unitup")
    private String unitUp;
    @SerializedName("kdgardu")
    private String kodeGardu;
    @SerializedName("notiang")
    private String noTiang;
    @SerializedName("kddk")
    private String kddk;
    @SerializedName("tarif")
    private String tarif;
    @SerializedName("expired")
    private String expired;
    @SerializedName("kode_petugas")
    private String kodePetugas;
    @SerializedName("daya")
    private String daya;
    @SerializedName("kode_vendor")
    private String kodeVendor;
    @SerializedName("tgl_wo")
    private String tanggalWo;
    @SerializedName("alamat")
    private String alamat;
    @SerializedName("nama")
    private String nama;
    @SerializedName("koordinat_x")
    private String koordinat_x;
    @SerializedName("koordinat_y")
    private String koordinat_y;
    @SerializedName("thblrek")
    private String thblRek;

    public String getLembar601() {
        return lembar601;
    }

    public void setLembar601(String lembar601) {
        this.lembar601 = lembar601;
    }

    public String getNo601() {
        return no601;
    }

    public void setNo601(String no601) {
        this.no601 = no601;
    }

    public String getNoTul601() {
        return noTul601;
    }

    public void setNoTul601(String noTul601) {
        this.noTul601 = noTul601;
    }

    public String getTagihan601() {
        return tagihan601;
    }

    public void setTagihan601(String tagihan601) {
        this.tagihan601 = tagihan601;
    }

    public String getNoWorkOrder() {
        return noWorkOrder;
    }

    public void setNoWorkOrder(String noWorkOrder) {
        this.noWorkOrder = noWorkOrder;
    }

    public String getTanggalPelunasan() {
        return tanggalPelunasan;
    }

    public void setTanggalPelunasan(String tanggalPelunasan) {
        this.tanggalPelunasan = tanggalPelunasan;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public String getUnitUpi() {
        return unitUpi;
    }

    public void setUnitUpi(String unitUpi) {
        this.unitUpi = unitUpi;
    }

    public String getUnitAp() {
        return unitAp;
    }

    public void setUnitAp(String unitAp) {
        this.unitAp = unitAp;
    }

    public String getUnitUp() {
        return unitUp;
    }

    public void setUnitUp(String unitUp) {
        this.unitUp = unitUp;
    }

    public String getKodeGardu() {
        return kodeGardu;
    }

    public void setKodeGardu(String kodeGardu) {
        this.kodeGardu = kodeGardu;
    }

    public String getNoTiang() {
        return noTiang;
    }

    public void setNoTiang(String noTiang) {
        this.noTiang = noTiang;
    }

    public String getKddk() {
        return kddk;
    }

    public void setKddk(String kddk) {
        this.kddk = kddk;
    }

    public String getTarif() {
        return tarif;
    }

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getKodePetugas() {
        return kodePetugas;
    }

    public void setKodePetugas(String kodePetugas) {
        this.kodePetugas = kodePetugas;
    }

    public String getDaya() {
        return daya;
    }

    public void setDaya(String daya) {
        this.daya = daya;
    }

    public String getKodeVendor() {
        return kodeVendor;
    }

    public void setKodeVendor(String kodeVendor) {
        this.kodeVendor = kodeVendor;
    }

    public String getTanggalWo() {
        return tanggalWo;
    }

    public void setTanggalWo(String tanggalWo) {
        this.tanggalWo = tanggalWo;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKoordinat_x() {
        return koordinat_x;
    }

    public void setKoordinat_x(String koordinat_x) {
        this.koordinat_x = koordinat_x;
    }

    public String getKoordinat_y() {
        return koordinat_y;
    }

    public void setKoordinat_y(String koordinat_y) {
        this.koordinat_y = koordinat_y;
    }

    public String getThblRek() {
        return thblRek;
    }

    public void setThblRek(String thblRek) {
        this.thblRek = thblRek;
    }

    public void formatPretty() {
        this.nama = StringUtils.normalize(nama);
        this.alamat = StringUtils.normalize(alamat);
        this.noTul601 = StringUtils.normalize(noTul601);
        this.noTiang = StringUtils.normalize(noTiang);
    }
}
