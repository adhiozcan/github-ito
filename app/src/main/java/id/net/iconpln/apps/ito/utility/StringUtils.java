package id.net.iconpln.apps.ito.utility;

import android.util.Log;

import org.apache.commons.lang3.text.WordUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Ozcan on 01/04/2017.
 */

public class StringUtils {
    private static final String TAG = StringUtils.class.getSimpleName();

    public static String normalize(String string) {
        if (string == null) return string;
        String result;
        try {
            result = URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result = string;
            Log.e(TAG, "normalize: ", e);
        }
        result = result.replaceAll("\\s+", " ").trim();
        result = result.toLowerCase();
        //result = upperCaseAllFirst(result);
        result = WordUtils.capitalize(result);
        return result;
    }

    private static String upperCaseAllFirst(String value) {
        char[] array = value.toCharArray();
        // Uppercase first letter.
        array[0] = Character.toUpperCase(array[0]);

        // Uppercase all letters that follow a whitespace character.
        for (int i = 1; i < array.length; i++) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            }
        }
        // Result.
        return new String(array);
    }

}
