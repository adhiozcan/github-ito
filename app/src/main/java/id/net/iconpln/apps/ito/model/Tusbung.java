package id.net.iconpln.apps.ito.model;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class Tusbung {
    private String noTul;
    private String noWo;
    private String unitUpId;
    private String base64Foto;
    private String jumlahFoto;
    private String part;
    private String tanggalPemutusan;
    private String standLWBP;
    private String standWBP;
    private String standKVARH;
    private String kodePetugas;
    private String namaPetugas;
    private String gagalPutus;
    private String status;

    public String getNoTul() {
        return noTul;
    }

    public void setNoTul(String noTul) {
        this.noTul = noTul;
    }

    public String getNoWo() {
        return noWo;
    }

    public void setNoWo(String noWo) {
        this.noWo = noWo;
    }

    public String getUnitUpId() {
        return unitUpId;
    }

    public void setUnitUpId(String unitUpId) {
        this.unitUpId = unitUpId;
    }

    public String getBase64Foto() {
        return base64Foto;
    }

    public void setBase64Foto(String base64Foto) {
        this.base64Foto = base64Foto;
    }

    public String getJumlahFoto() {
        return jumlahFoto;
    }

    public void setJumlahFoto(String jumlahFoto) {
        this.jumlahFoto = jumlahFoto;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getTanggalPemutusan() {
        return tanggalPemutusan;
    }

    public void setTanggalPemutusan(String tanggalPemutusan) {
        this.tanggalPemutusan = tanggalPemutusan;
    }

    public String getStandLWBP() {
        return standLWBP;
    }

    public void setStandLWBP(String standLWBP) {
        this.standLWBP = standLWBP;
    }

    public String getStandWBP() {
        return standWBP;
    }

    public void setStandWBP(String standWBP) {
        this.standWBP = standWBP;
    }

    public String getStandKVARH() {
        return standKVARH;
    }

    public void setStandKVARH(String standKVARH) {
        this.standKVARH = standKVARH;
    }

    public String getKodePetugas() {
        return kodePetugas;
    }

    public void setKodePetugas(String kodePetugas) {
        this.kodePetugas = kodePetugas;
    }

    public String getNamaPetugas() {
        return namaPetugas;
    }

    public void setNamaPetugas(String namaPetugas) {
        this.namaPetugas = namaPetugas;
    }

    public String getGagalPutus() {
        return gagalPutus;
    }

    public void setGagalPutus(String gagalPutus) {
        this.gagalPutus = gagalPutus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Tusbung{" +
                "noTul='" + noTul + '\'' +
                ", noWo='" + noWo + '\'' +
                ", unitUpId='" + unitUpId + '\'' +
                ", base64Foto='" + base64Foto + '\'' +
                ", jumlahFoto='" + jumlahFoto + '\'' +
                ", part='" + part + '\'' +
                ", tanggalPemutusan='" + tanggalPemutusan + '\'' +
                ", standLWBP='" + standLWBP + '\'' +
                ", standWBP='" + standWBP + '\'' +
                ", standKVARH='" + standKVARH + '\'' +
                ", kodePetugas='" + kodePetugas + '\'' +
                ", namaPetugas='" + namaPetugas + '\'' +
                ", gagalPutus='" + gagalPutus + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
