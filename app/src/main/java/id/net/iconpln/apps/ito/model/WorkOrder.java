package id.net.iconpln.apps.ito.model;

import com.google.gson.annotations.SerializedName;

import id.net.iconpln.apps.ito.utility.StringUtils;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class WorkOrder extends Model {
    @SerializedName("no_wo")
    private String noWo;
    @SerializedName("idpelanggan")
    private String pelangganId;
    @SerializedName("nama")
    private String nama;
    @SerializedName("tgl_wo")
    private String tanggalWo;
    @SerializedName("tgl_pelunasan_601")
    private String tanggalPelunasan;
    @SerializedName("alamat")
    private String alamat;
    @SerializedName("tarif")
    private String tarif;
    @SerializedName("daya")
    private String daya;
    @SerializedName("no_tul601")
    private String noTul601;
    @SerializedName("no_601")
    private String noTul;
    @SerializedName("tagihan_601")
    private String tagihan601;
    @SerializedName("unitupi")
    private String unitUpi;
    @SerializedName("unitup")
    private String unitUp;
    @SerializedName("unitap")
    private String unitAp;
    @SerializedName("kdgardu")
    private String noGardu;
    @SerializedName("notiang")
    private String noTiang;
    @SerializedName("kddk")
    private String kddk;

    private String rbm;
    private String statusPiutang;
    private String rpTotal;
    private String jumlahRpTag;
    private String jumlahRbpk;
    @SerializedName("lembar_601")
    private String jumlahLembar;
    private String total;

    @SerializedName("koordinat_x")
    private String koordinatX;
    @SerializedName("koordinat_y")
    private String koordinatY;
    @SerializedName("kode_vendor")
    private String kodeVendor;
    @SerializedName("expired")
    private String expired;
    @SerializedName("kode_petugas")
    private String kodePetugas;

    @SerializedName("flag_tusbung")
    private String flag;
    @SerializedName("totalloop")
    private String jumlahData;

    public String getJumlahData() {
        return jumlahData;
    }

    public void setJumlahData(String jumlahData) {
        this.jumlahData = jumlahData;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getNoWo() {
        return noWo;
    }

    public void setNoWo(String noWo) {
        this.noWo = noWo;
    }

    public String getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(String pelangganId) {
        this.pelangganId = pelangganId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTanggalWo() {
        return tanggalWo;
    }

    public void setTanggalWo(String tanggalWo) {
        this.tanggalWo = tanggalWo;
    }

    public String getTanggalPelunasan() {
        return tanggalPelunasan;
    }

    public void setTanggalPelunasan(String tanggalPelunasan) {
        this.tanggalPelunasan = tanggalPelunasan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTarif() {
        return tarif;
    }

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public String getDaya() {
        return daya;
    }

    public void setDaya(String daya) {
        this.daya = daya;
    }

    public String getNoTul601() {
        return noTul601;
    }

    public void setNoTul601(String noTul601) {
        this.noTul601 = noTul601;
    }

    public String getNoTul() {
        return noTul;
    }

    public void setNoTul(String noTul) {
        this.noTul = noTul;
    }

    public String getTagihan601() {
        return tagihan601;
    }

    public void setTagihan601(String tagihan601) {
        this.tagihan601 = tagihan601;
    }

    public String getUnitUpi() {
        return unitUpi;
    }

    public void setUnitUpi(String unitUpi) {
        this.unitUpi = unitUpi;
    }

    public String getUnitUp() {
        return unitUp;
    }

    public void setUnitUp(String unitUp) {
        this.unitUp = unitUp;
    }

    public String getUnitAp() {
        return unitAp;
    }

    public void setUnitAp(String unitAp) {
        this.unitAp = unitAp;
    }

    public String getNoGardu() {
        return noGardu;
    }

    public void setNoGardu(String noGardu) {
        this.noGardu = noGardu;
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

    public String getRbm() {
        return rbm;
    }

    public void setRbm(String rbm) {
        this.rbm = rbm;
    }

    public String getStatusPiutang() {
        if (tanggalPelunasan != null && !tanggalPelunasan.isEmpty() && tanggalPelunasan.length() > 2) {
            return "Lunas";
        } else {
            return "Belum Lunas";
        }
    }

    public void setStatusPiutang(String statusPiutang) {
        this.statusPiutang = statusPiutang;
    }

    public String getRpTotal() {
        return rpTotal;
    }

    public void setRpTotal(String rpTotal) {
        this.rpTotal = rpTotal;
    }

    public String getJumlahRpTag() {
        return jumlahRpTag;
    }

    public void setJumlahRpTag(String jumlahRpTag) {
        this.jumlahRpTag = jumlahRpTag;
    }

    public String getJumlahRbpk() {
        return jumlahRbpk;
    }

    public void setJumlahRbpk(String jumlahRbpk) {
        this.jumlahRbpk = jumlahRbpk;
    }

    public String getJumlahLembar() {
        return jumlahLembar;
    }

    public void setJumlahLembar(String jumlahLembar) {
        this.jumlahLembar = jumlahLembar;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getKoordinatX() {
        return koordinatX;
    }

    public void setKoordinatX(String koordinatX) {
        this.koordinatX = koordinatX;
    }

    public String getKoordinatY() {
        return koordinatY;
    }

    public void setKoordinatY(String koordinatY) {
        this.koordinatY = koordinatY;
    }

    public String getKodeVendor() {
        return kodeVendor;
    }

    public void setKodeVendor(String kodeVendor) {
        this.kodeVendor = kodeVendor;
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

    @Override
    public String toString() {
        return "WorkOrder{" +
                "noWo='" + noWo + '\'' +
                ", pelangganId='" + pelangganId + '\'' +
                ", nama='" + nama + '\'' +
                ", tanggalWo='" + tanggalWo + '\'' +
                ", tanggalPelunasan='" + tanggalPelunasan + '\'' +
                ", alamat='" + alamat + '\'' +
                ", tarif='" + tarif + '\'' +
                ", daya='" + daya + '\'' +
                ", noTul601='" + noTul601 + '\'' +
                ", noTul='" + noTul + '\'' +
                ", tagihan601='" + tagihan601 + '\'' +
                ", unitUpi='" + unitUpi + '\'' +
                ", unitUp='" + unitUp + '\'' +
                ", unitAp='" + unitAp + '\'' +
                ", noGardu='" + noGardu + '\'' +
                ", noTiang='" + noTiang + '\'' +
                ", kddk='" + kddk + '\'' +
                ", rbm='" + rbm + '\'' +
                ", statusPiutang='" + statusPiutang + '\'' +
                ", rpTotal='" + rpTotal + '\'' +
                ", jumlahRpTag='" + jumlahRpTag + '\'' +
                ", jumlahRbpk='" + jumlahRbpk + '\'' +
                ", jumlahLembar='" + jumlahLembar + '\'' +
                ", total='" + total + '\'' +
                ", koordinatX='" + koordinatX + '\'' +
                ", koordinatY='" + koordinatY + '\'' +
                ", kodeVendor='" + kodeVendor + '\'' +
                ", expired='" + expired + '\'' +
                ", kodePetugas='" + kodePetugas + '\'' +
                '}';
    }

    public void formatPretty() {
        this.nama = StringUtils.normalize(nama);
        this.alamat = StringUtils.normalize(alamat);
        this.noTul = StringUtils.normalize(noTul);
        this.noTiang = StringUtils.normalize(noTiang);
    }
}