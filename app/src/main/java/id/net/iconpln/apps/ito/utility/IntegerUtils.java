package id.net.iconpln.apps.ito.utility;

/**
 * Created by Ozcan on 10/07/2017.
 */

public class IntegerUtils {

    public static int checkNull(String object) {
        if (object == null) return 0;
        return Integer.parseInt(object);
    }
}
