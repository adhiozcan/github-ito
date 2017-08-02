package id.net.iconpln.apps.ito.utility;

import com.orhanobut.hawk.Hawk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.net.iconpln.apps.ito.helper.Constants;

/**
 * Created by Ozcan on 05/04/2017.
 */

public class DateUtils {
    public static void saveDateFromLogin(String date) {
        Hawk.put(Constants.SAVE_DATE, date);
    }

    public static String getDateFromLogin() {
        return Hawk.get(Constants.SAVE_DATE, "");
    }

    public static Date parseToDate(String dateString) {
        Date date = null;
        try {
            Locale locale = new Locale("in", "ID");
            date = new SimpleDateFormat("yyyyMMddHHmmss", locale).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String parseToString(Date date) {
        String dateInString = new SimpleDateFormat("dd/MM/yyyy").format(date);
        return dateInString;
    }

    public static String getMonth(String month) {
        String monthConvert = "00";
        switch (month) {
            case "Januari":
                monthConvert = "01";
                break;
            case "Februari":
                monthConvert = "02";
                break;
            case "Maret":
                monthConvert = "03";
                break;
            case "April":
                monthConvert = "04";
                break;
            case "Mei":
                monthConvert = "05";
                break;
            case "Juni":
                monthConvert = "06";
                break;
            case "Juli":
                monthConvert = "07";
                break;
            case "Agustus":
                monthConvert = "08";
                break;
            case "September":
                monthConvert = "09";
                break;
            case "Oktober":
                monthConvert = "10";
                break;
            case "November":
                monthConvert = "11";
                break;
            case "Desember":
                monthConvert = "12";
                break;
        }
        return monthConvert;
    }
}
