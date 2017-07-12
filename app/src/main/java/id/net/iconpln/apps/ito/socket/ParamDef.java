package id.net.iconpln.apps.ito.socket;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Ozcan on 24/04/2017.
 */

public class ParamDef {
    public static final int LOGIN              = 0;
    public static final int GET_WO             = 1;
    public static final int GET_WO_CHART       = 2;
    public static final int GET_WO_MONITOR     = 3;
    public static final int GET_WO_ULANG       = 4;
    public static final int GET_MASTER_TUSBUNG = 5;
    public static final int SET_WO             = 6;
    public static final int DO_TUSBUNG         = 7;
    public static final int DO_TUSBUNG_ULANG   = 8;

    public ParamDef(@Param int service) {
        System.out.println(">>Param : " + service);
    }

    @IntDef({
            LOGIN, GET_WO, GET_WO_CHART, GET_WO_MONITOR,
            GET_WO_ULANG, GET_MASTER_TUSBUNG, SET_WO,
            DO_TUSBUNG, DO_TUSBUNG_ULANG
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Param {
    }
}
