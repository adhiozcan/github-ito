package id.net.iconpln.apps.ito.job;

import android.app.Application;
import android.support.annotation.IntDef;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by alsahfer on 3/27/17.
 */

abstract public class BaseJob extends Job {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UI_HIGH, BACKGROUND})
    public @interface Priority {

    }

    public static final int UI_HIGH    = 10;
    public static final int BACKGROUND = 1;

    protected BaseJob(Params params) {
        super(params);
    }

    protected boolean shouldRetry(Throwable throwable) {
        if (throwable instanceof NetworkException) {
            NetworkException exception = (NetworkException) throwable;
            return exception.shouldRetry();
        }
        return true;
    }

    public static void init(Application application) {
    }
}
