package id.net.iconpln.apps.ito.utility;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;

import id.net.iconpln.apps.ito.R;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class CommonUtils {
    public static void installToolbar(AppCompatActivity context) {
        Toolbar mToolbar = (Toolbar) context.findViewById(R.id.toolbar);
        context.setSupportActionBar(mToolbar);
        if (context.getSupportActionBar() != null) {
            context.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            context.getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public static RecyclerView.LayoutManager getVerticalLayoutManager(Context context) {
        return new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
    }

}
