package id.net.iconpln.apps.ito.config;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class NetworkConfig {
    public static String USER_AGENT = "Version 1.2.0";
    public static String SPRING_MC  = "MkP1uTanG@2016!";
    public static String COOKIE     = "";

    public static String MYKEY       = "";
    public static String MYIV        = "";
    public static String MYDEBUG     = "";
    public static String MYDEBUGINFO = "";
    public static String MYKEYSTORE  = "";


    public static String HTTP_LOGIN         = "workorder/login";
    public static String HTTP_GET_WORKORDER = "workorder/cek";
    public static String HTTP_SEND_DATA     = "workorder/kirimwo";
    public static String HTTP_SEND_GPS      = "workorder/tracking";
    public static String HTTP_SEND_IMAGE    = "uploadfoto";
    public static String HTTP_HELP          = "help";

    public static String HTTP_CHECK_STATUS       = "workorder/cekstatus";
    public static String HTTP_CHECK_STATUS_MASAL = "workorder/cekstatus2"; //idpel='xxxxxxxxx','asdasdasdasd','asdasdasd'
    public static String HTTP_REJECT_IDPEL       = "workorder/reject";
    public static String HTTP_KINERJA_VENDOR     = "workorder/kinerja";
    public static String HTTP_KINERJA_VENDOR2    = "workorder/kinerja3";
    public static String HTTP_KINERJA_PETUGAS    = "workorder/kinerja2";
    public static String HTTP_KINERJA_TUNGGAKAN  = "grafik/mon-tunggakan-data";
    public static String HTTP_KINERJA_POLABYR    = "grafik/mon-polabayar-data";

    public static String HTTP_GET_PETUGAS = "responsecombo/petugas";
    public static String HTTP_GET_UPI     = "responsecombo/upi";
    public static String HTTP_GET_AP      = "responsecombo/ap";
    public static String HTTP_GET_UP      = "responsecombo/up";
    public static String HTTP_GET_RBM     = "rcmobile/rbm";
    public static String HTTP_GET_GARDU   = "rcmobile/gardu";
    public static String HTTP_GET_WOALL   = "woweb/datawoall";
    public static String HTTP_SEND_WO     = "woweb/perencanaan";


    public static String  IMAGEFILE_STORAGE = "MKPIUTANG";
    public static boolean IMAGEFILE_ROTATE  = false;


    public static String[] alphabet =
            {
                    "a", "b", "c", "d", "e",
                    "f", "g", "h", "i", "j",
                    "k", "l", "m", "n", "o",
                    "p", "q", "r", "s", "t",
                    "u", "v", "w", "x", "y",
                    "z"
            };

    public static int getAlphaPosition(String alpha) {
        int len = alphabet.length;
        for (int i = 0; i < len; i++) {
            if (alphabet[i] == alpha) {
                return i + 1;
            }
        }
        return 0;
    }
}
