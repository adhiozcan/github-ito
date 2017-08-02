package id.net.iconpln.apps.ito.socket;

import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.Tusbung;

/**
 * Created by Ozcan on 24/03/2017.
 */

public class Param {
    public static String login() {
        return "{" +
                "type:event," +
                "event:do_something," +
                "params: {" +
                "method: login," +
                "imei: 862844031150625," +
                "username:" + AppConfig.USERNAME + "," +
                "password:" + AppConfig.PASSWORD +
                "}" +
                "}";
    }

    public static String monitoring() {
        return "{" +
                "\"type\":\"event\"," +
                "\"event\":\"do_something\"," +
                "\"params\": {" +
                "\"method\":\"getwomonitoring\"," +
                "\"kodepetugas\":\"" + AppConfig.KODE_PETUGAS + "\"," +
                "\"idunitup\":\"" + AppConfig.ID_UNIT_UP + "\"," +
                "\"bulan\":\"" + AppConfig.MONITOR_BULAN + "\"," +
                "\"tahun\":\"" + AppConfig.MONITOR_TAHUN + "\"" +
                "}" +
                "}";
    }

    public static String getWoAll() {
        return "{\n" +
                "    \"type\": \"event\",\n" +
                "    \"event\": \"do_something\",\n" +
                "    \"params\": {\n" +
                "        \"method\": \"getwoall\",\n" +
                "        \"kodepetugas\": \"" + AppConfig.KODE_PETUGAS + "\",\n" +
                "        \"idunitup\": \"" + AppConfig.ID_UNIT_UP + "\"\n" +
                "    }\n" +
                "}";
    }

    public static String getWoSync() {
        return "{\n" +
                "\t\"type\": \"event\",\n" +
                "\t\"event\": \"do_something\",\n" +
                "\t\"params\": {\n" +
                "\t\t\"method\": \"getwosync\",\n" +
                "\t\t\"idunitup\": \"" + AppConfig.ID_UNIT_UP + "\",\n" +
                "\t\t\"kodepetugas\": \"" + AppConfig.KODE_PETUGAS + "\",\n" +
                "\t\t\"pagestart\": \"" + AppConfig.WO_PAGE_START + "\",\n" +
                "\t\t\"pageend\": \"" + AppConfig.WO_PAGE_END + "\"\n" +
                "\t}\n" +
                "}";
    }

    public static String getWoUlang() {
        return "{\n" +
                "\t\"type\": \"event\",\n" +
                "\t\"event\": \"do_something\",\n" +
                "\t\"params\": {\n" +
                "\t\t\"method\": \"getwoulang\",\n" +
                "\t\t\"idunitup\": \"" + AppConfig.ID_UNIT_UP + "\",\n" +
                "\t\t\"kodepetugas\": \"" + AppConfig.KODE_PETUGAS + "\"\n" +
                "\t}\n" +
                "}";
    }

    public static String markAsLocal() {
        return "{\n" +
                "\t\"type\": \"event\",\n" +
                "\t\"event\": \"do_something\",\n" +
                "\t\"params\": {\n" +
                "\t\t\"method\": \"setwosync\",\n" +
                "\t\t\"kodepetugas\": \"" + AppConfig.KODE_PETUGAS + "\",\n" +
                "\t\t\"idunitup\": \"" + AppConfig.ID_UNIT_UP + "\",\n" +
                "\t\t\"nomorwo\": \"" + AppConfig.NO_WO_LOCAL + "\"\n" +
                "\t}\n" +
                "}";
    }

    public static String getWoChart() {
        return "{" +
                "type: event," +
                "event:do_something," +
                "params: {" +
                "method: getwototal," +
                "kodepetugas:" + AppConfig.KODE_PETUGAS +
                "}" +
                "}";
    }

    public static String getMasterTusbung() {
        return "{" +
                "type: event," +
                "event: do_something," +
                "params: {" +
                "method: getmasterflagtusbung" +
                "}" +
                "}";
    }

    /**
     * public static String doTusbung(String foto, String jumlah, String part, String tanggalPutus,
     * String standLwbp, String standWbp, String standKvarh,
     * String kodePetugas, String gagalPutus, String nomorWo,
     * String namaPetugas, String noTul, String idUnitUp, String status)
     *
     * @return
     */
    public static String doTusbung() {
        Tusbung tusbung = AppConfig.TUSBUNG;
        return "{\n" +
                "\"type\": \"event\",\n" +
                "\"event\": \"do_something\",\n" +
                "\"params\": {\n" +
                "\"method\": \"tempuploadwo\",\n" +
                "\"foto\":\"" + tusbung.getBase64Foto() + "\",\n" +
                "\"jumlah\":\"" + tusbung.getJumlahFoto() + "\", \"ke\":\"" + tusbung.getPart() + "\",\n" +
                "\"tglputus\": \"" + tusbung.getTanggalPemutusan() + "\",\n" +
                "\"standlwbpputus\": \"" + tusbung.getStandLWBP() + "\",\n" +
                "\"standwbpputus\": \"" + tusbung.getStandWBP() + "\",\n" +
                "\"standkvarhputus\": \"" + tusbung.getStandKVARH() + "\",\n" +
                "\"namaputus\": \"" + tusbung.getNamaPetugas() + "\",\n" +
                "\"gagalputus\": \"" + tusbung.getGagalPutus() + "\",\n" +
                "\"nomorwo\": \"" + tusbung.getNoWo() + "\",\n" +
                "\"kodepetugas\": \"" + tusbung.getKodePetugas() + "\",\n" +
                "\"latitude\": \"" + tusbung.getLatitude() + "\",\n" +
                "\"longitude\": \"" + tusbung.getLongitude() + "\",\n" +
                "\"notul\": \"" + tusbung.getNoTul() + "\",\n" +
                "\"idunitup\": \"" + tusbung.getUnitUpId() + "\",\n" +
                "\"email\": \"" + tusbung.getEmail() + "\",\n" +
                "\"hp\": \"" + tusbung.getHp() + "\",\n" +
                "\"status\": \"" + tusbung.getStatus() + "\"\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n";
    }

    public static String doTusbungUlang() {
        Tusbung tusbung = AppConfig.TUSBUNG;
        return "{\n" +
                "\"type\": \"event\",\n" +
                "\"event\": \"do_something\",\n" +
                "\"params\": {\n" +
                "\"method\": \"tempuploadwoulang\",\n" +
                "\"foto\":\"" + tusbung.getBase64Foto() + "\",\n" +
                "\"jumlah\":\"" + tusbung.getJumlahFoto() + "\", \"ke\":\"" + tusbung.getPart() + "\",\n" +
                "\"tglputus\": \"" + tusbung.getTanggalPemutusan() + "\",\n" +
                "\"standlwbpputus\": \"" + tusbung.getStandLWBP() + "\",\n" +
                "\"standwbpputus\": \"" + tusbung.getStandWBP() + "\",\n" +
                "\"standkvarhputus\": \"" + tusbung.getStandKVARH() + "\",\n" +
                "\"namaputus\": \"" + tusbung.getNamaPetugas() + "\",\n" +
                "\"gagalputus\": \"" + tusbung.getGagalPutus() + "\",\n" +
                "\"nomorwo\": \"" + tusbung.getNoWo() + "\",\n" +
                "\"kodepetugas\": \"" + tusbung.getKodePetugas() + "\",\n" +
                "\"latitude\": \"" + tusbung.getLatitude() + "\",\n" +
                "\"longitude\": \"" + tusbung.getLongitude() + "\",\n" +
                "\"notul\": \"" + tusbung.getNoTul() + "\",\n" +
                "\"idunitup\": \"" + tusbung.getUnitUpId() + "\",\n" +
                "\"email\": \"" + tusbung.getEmail() + "\",\n" +
                "\"hp\": \"" + tusbung.getHp() + "\",\n" +
                "\"status\": \"" + tusbung.getStatus() + "\"\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n";
    }
}
