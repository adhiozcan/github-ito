package id.net.iconpln.apps.ito.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.facebook.stetho.common.StringUtil;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.net.iconpln.apps.ito.utility.StringUtils;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class WorkOrder extends RealmObject implements Parcelable {
    @PrimaryKey
    @SerializedName("no_wo")
    private String noWo;
    @SerializedName("idpelanggan")
    private String pelangganId;
    @SerializedName("nama")
    private String nama;
    @SerializedName("tgl_wo")
    private String tanggalWo;
    @SerializedName("tgl_pelaksanaan_putus")
    private String tanggalPutus;
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

    private boolean isSelesai;
    private boolean isUploaded;
    private String  statusSinkronisasi;
    private boolean isWoUlang;

    public WorkOrder() {
    }


    protected WorkOrder(Parcel in) {
        noWo = in.readString();
        pelangganId = in.readString();
        nama = in.readString();
        tanggalWo = in.readString();
        tanggalPutus = in.readString();
        tanggalPelunasan = in.readString();
        alamat = in.readString();
        tarif = in.readString();
        daya = in.readString();
        noTul601 = in.readString();
        noTul = in.readString();
        tagihan601 = in.readString();
        unitUpi = in.readString();
        unitUp = in.readString();
        unitAp = in.readString();
        noGardu = in.readString();
        noTiang = in.readString();
        kddk = in.readString();
        rbm = in.readString();
        statusPiutang = in.readString();
        rpTotal = in.readString();
        jumlahRpTag = in.readString();
        jumlahRbpk = in.readString();
        jumlahLembar = in.readString();
        total = in.readString();
        koordinatX = in.readString();
        koordinatY = in.readString();
        kodeVendor = in.readString();
        expired = in.readString();
        kodePetugas = in.readString();
        flag = in.readString();
        jumlahData = in.readString();
        isSelesai = in.readByte() != 0;
        isUploaded = in.readByte() != 0;
        statusSinkronisasi = in.readString();
        isWoUlang = in.readByte() != 0;
    }

    public static final Creator<WorkOrder> CREATOR = new Creator<WorkOrder>() {
        @Override
        public WorkOrder createFromParcel(Parcel in) {
            return new WorkOrder(in);
        }

        @Override
        public WorkOrder[] newArray(int size) {
            return new WorkOrder[size];
        }
    };

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

    public String getTanggalPutus() {
        return tanggalPutus;
    }

    public void setTanggalPutus(String tanggalPutus) {
        this.tanggalPutus = tanggalPutus;
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

    public boolean isSelesai() {
        return isSelesai;
    }

    public void setSelesai(boolean selesai) {
        isSelesai = selesai;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getStatusSinkronisasi() {
        return statusSinkronisasi;
    }

    public void setStatusSinkronisasi(String statusSinkronisasi) {
        this.statusSinkronisasi = statusSinkronisasi;
    }

    public boolean isExpired() {
        //DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            Date expired = df.parse(getExpired());
            Date today   = new Date();
            //date.before(today);
            Log.d("WorkOrder", "isExpired: -------------------------------------------------------");
            Log.d("WorkOrder", "isExpired: [Today]" + today.toString() + " | [Exp]" + expired.toString());
            Log.d("WorkOrder", "isExpired: " + today.after(expired));
            Log.d("WorkOrder", "isExpired: -------------------------------------------------------");

            if (today.equals(expired)) return false;
            else if (today.after(expired)) return true;
            else return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isWoUlang() {
        return isWoUlang;
    }

    public void setWoUlang(boolean woUlang) {
        isWoUlang = woUlang;
    }

    public void formatPretty() {
        if (this.nama != null) this.nama = StringUtils.normalize(nama);
        else {
            System.out.println(this.noWo + " nama kosong");
        }
        if (this.tanggalWo != null) this.tanggalWo = StringUtils.normalize(tanggalWo);
        else {
            System.out.println(this.noWo + " tanggal kosong");
        }
        if (this.alamat != null) this.alamat = StringUtils.normalize(alamat);
        else {
            System.out.println(this.noWo + " alamat kosong");
        }
        if (this.noTul != null) this.noTul = StringUtils.normalize(noTul);
        else {
            System.out.println(this.noTul + " no tul kosong");
        }
        if (this.noTiang != null) this.noTiang = StringUtils.normalize(noTiang);
        else {
            System.out.println(this.noTiang + " no tiang kosong");
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(noWo);
        parcel.writeString(pelangganId);
        parcel.writeString(nama);
        parcel.writeString(tanggalWo);
        parcel.writeString(tanggalPutus);
        parcel.writeString(tanggalPelunasan);
        parcel.writeString(alamat);
        parcel.writeString(tarif);
        parcel.writeString(daya);
        parcel.writeString(noTul601);
        parcel.writeString(noTul);
        parcel.writeString(tagihan601);
        parcel.writeString(unitUpi);
        parcel.writeString(unitUp);
        parcel.writeString(unitAp);
        parcel.writeString(noGardu);
        parcel.writeString(noTiang);
        parcel.writeString(kddk);
        parcel.writeString(rbm);
        parcel.writeString(statusPiutang);
        parcel.writeString(rpTotal);
        parcel.writeString(jumlahRpTag);
        parcel.writeString(jumlahRbpk);
        parcel.writeString(jumlahLembar);
        parcel.writeString(total);
        parcel.writeString(koordinatX);
        parcel.writeString(koordinatY);
        parcel.writeString(kodeVendor);
        parcel.writeString(expired);
        parcel.writeString(kodePetugas);
        parcel.writeString(flag);
        parcel.writeString(jumlahData);
        parcel.writeByte((byte) (isSelesai ? 1 : 0));
        parcel.writeByte((byte) (isUploaded ? 1 : 0));
        parcel.writeString(statusSinkronisasi);
        parcel.writeByte((byte) (isWoUlang ? 1 : 0));
    }
}
