package id.net.iconpln.apps.ito.utility;

import android.util.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Rizki Maulana on 5/2/2016.
 */
public class Formater {
    public static String numberToMonth(String str) {
        int intmonth = Integer.valueOf(str);
        String date = "";
        switch (intmonth) {
            case 1:
                date = "Januari";
                break;
            case 2:
                date = "Februari";
                break;
            case 3:
                date = "Maret";
                break;
            case 4:
                date = "April";
                break;
            case 5:
                date = "Mei";
                break;
            case 6:
                date = "Juni";
                break;
            case 7:
                date = "Juli";
                break;
            case 8:
                date = "Agustus";
                break;
            case 9:
                date = "September";
                break;
            case 10:
                date = "Oktober";
                break;
            case 11:
                date = "November";
                break;
            case 12:
                date = "Desember";
                break;
        }
        return date;
    }

    public static String numberToMonth(int month) {
        String date = "";
        switch (month) {
            case 1:
                date = "Januari";
                break;
            case 2:
                date = "Februari";
                break;
            case 3:
                date = "Maret";
                break;
            case 4:
                date = "April";
                break;
            case 5:
                date = "Mei";
                break;
            case 6:
                date = "Juni";
                break;
            case 7:
                date = "Juli";
                break;
            case 8:
                date = "Agustus";
                break;
            case 9:
                date = "September";
                break;
            case 10:
                date = "Oktober";
                break;
            case 11:
                date = "November";
                break;
            case 12:
                date = "Desember";
                break;
        }
        return date;
    }

    public static String intToMonth(int month) {
        String data = "01";
        switch (month) {
            case 0:
                data = "01";
                break;
            case 1:
                data = "02";
                break;
            case 2:
                data = "03";
                break;
            case 3:
                data = "04";
                break;
            case 4:
                data = "05";
                break;
            case 5:
                data = "06";
                break;
            case 6:
                data = "07";
                break;
            case 7:
                data = "08";
                break;
            case 8:
                data = "09";
                break;
            case 9:
                data = "10";
                break;
            case 10:
                data = "11";
                break;
            case 11:
                data = "12";
                break;
            case 12:
                data = "01";
                break;

        }
        return data;
    }

    public static String numberFormater(int param) {
        String data = "";
        Locale.setDefault(new Locale("id", "ID"));
        data = NumberFormat.getNumberInstance(Locale.getDefault()).format(param);

        return data;
    }

    public static String unixTimeToString(String str){
        String data = "";
        long unixSeconds = Long.parseLong(str);
        Date date = new Date(unixSeconds*1000L);
        Date dateToday = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        String formatedDateToday = sdf.format(dateToday);
        if (formattedDate.equals(formatedDateToday)){
            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            data = sdfTime.format(date);
        }else {
            data = formattedDate;
        }
        return data;
    }

    public static String unixTimeToCompleteString(String str){
        long unixSeconds = Long.parseLong(str);
        Date date = new Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm  ", Locale.getDefault());
        return sdf.format(date);
    }

    public static String getTimeStamp(){
        long unixTime = System.currentTimeMillis() / 1000L;
        return String.valueOf(unixTime);
    }

    public static String stringToUnixTime(int year, int month, int day){
        Date date = new Date(year - 1900, month, day+1);
        long unixTime = date.getTime() / 1000L;
        Log.i("stringToUnixTime", String.valueOf(unixTime)+" | "+year);
        return String.valueOf(unixTime);
    }

    public static String unixTimeToDateString(String str){
        long unixSeconds = Long.parseLong(str);
        Date date = new Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public static String unixTimeToTimeString(String str){
        long unixSeconds = Long.parseLong(str);
        Date date = new Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        return sdf.format(date);
    }


}
