package id.net.iconpln.apps.ito.helper;

import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * Created by Ozcan on 03/04/2017.
 */

public class Anim {
    public static final int SLIDE_DOWN = 0;
    public static final int SLIDE_UP   = 1;

    public static ViewPropertyAnimator slideUp(final View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(800);
        return view.animate().setDuration(1000).translationY(0).alpha(1);
    }

    public static ViewPropertyAnimator slideDown(final View view) {
        return view.animate().setDuration(1000).translationY(800).alpha(1);
    }
}
