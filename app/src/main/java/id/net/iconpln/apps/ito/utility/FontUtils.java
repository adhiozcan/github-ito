package id.net.iconpln.apps.ito.utility;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Ozcan on 03/04/2017.
 */

public class FontUtils {
    public static final int SANS_SERIF_LIGHT   = 1;
    public static final int SANS_SERIF_REGULAR = 2;

    protected static Typeface typefaceChoosed;

    public static Typeface makeUpWith(Context context, int fontType) {
        switch (fontType) {
            case SANS_SERIF_LIGHT:
                typefaceChoosed = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");
                break;
            case SANS_SERIF_REGULAR:
                typefaceChoosed = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
                break;
            default:
                return null;
        }
        return typefaceChoosed;
    }
}
