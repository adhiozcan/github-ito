package id.net.iconpln.apps.ito.model;

import io.realm.RealmObject;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class Tusbung extends RealmObject {
    private String noTul;
    private String noWo;
    private String pelangganId;
    private String namaPelanggan;
    private String alamat;
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
    private String latitude;
    private String longitude;
    private String photoPath1;
    private String photoPath2;
    private String photoPath3;
    private String photoPath4;

    private String statusSinkron;

    public Tusbung() {
    }

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

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(String pelangganId) {
        this.pelangganId = pelangganId;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhotoPath1() {
        return photoPath1;
    }

    public void setPhotoPath1(String photoPath1) {
        this.photoPath1 = photoPath1;
    }

    public String getPhotoPath2() {
        return photoPath2;
    }

    public void setPhotoPath2(String photoPath2) {
        this.photoPath2 = photoPath2;
    }

    public String getPhotoPath3() {
        return photoPath3;
    }

    public void setPhotoPath3(String photoPath3) {
        this.photoPath3 = photoPath3;
    }

    public String getPhotoPath4() {
        return photoPath4;
    }

    public void setPhotoPath4(String photoPath4) {
        this.photoPath4 = photoPath4;
    }

    public String getStatusSinkron() {
        return statusSinkron;
    }

    public void setStatusSinkron(String statusSinkron) {
        this.statusSinkron = statusSinkron;
    }

    @Override
    public String toString() {
        return "Tusbung{" +
                "noTul='" + noTul + '\'' +
                ", noWo='" + noWo + '\'' +
                ", pelangganId='" + pelangganId + '\'' +
                ", namaPelanggan='" + namaPelanggan + '\'' +
                ", alamat='" + alamat + '\'' +
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
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", photoPath1='" + photoPath1 + '\'' +
                ", photoPath2='" + photoPath2 + '\'' +
                ", photoPath3='" + photoPath3 + '\'' +
                ", photoPath4='" + photoPath4 + '\'' +
                ", statusSinkron='" + statusSinkron + '\'' +
                '}';
    }
}
