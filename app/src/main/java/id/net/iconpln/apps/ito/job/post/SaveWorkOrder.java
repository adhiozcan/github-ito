package id.net.iconpln.apps.ito.job.post;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.Subscribe;

import id.net.iconpln.apps.ito.job.BaseJob;
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.socket.envelope.MessageEvent;

/**
 * Created by alsahfer on 3/27/17.
 */

public class SaveWorkOrder extends BaseJob {

    private static final String GROUP = "new_post";

    private final String mText;

    private final String mClientId;

    private final long mUserId;

    transient SocketTransaction socketTransaction;

    transient MessageEvent mEventBus;

    transient WorkOrder mWorkOrder;

    transient UserProfile mUserModel;

    public SaveWorkOrder(String text, String clientId, long userId, MessageEvent messageEvent) {
        super(new Params(BACKGROUND).groupBy(GROUP).requireNetwork().persist());
        mText = text;
        mClientId = clientId;
        mUserId = userId;
        mEventBus = messageEvent;
    }

    @Override
    public void onAdded() {
        // make sure whatever time we put here is greater / eq to last known time in database.
        // this will work around issues related to client's time.
        // this time is temporary anyways as it will be overriden when it is synched to server
    }

    @Override
    public void onRun() throws Throwable {
        onLoginResponse(mEventBus);

    }

    @Override
    protected int getRetryLimit() {
        return 0;
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount,
                                                     int maxRunCount) {
        if (shouldRetry(throwable)) {
            // For the purposes of the demo, just back off 250 ms.
            RetryConstraint constraint = RetryConstraint
                    .createExponentialBackoff(runCount, 250);
            constraint.setApplyNewDelayToGroup(true);
            return constraint;
        }
        return RetryConstraint.CANCEL;
    }

    @Override
    protected void onCancel() {
    }

    @Subscribe
    private void onLoginResponse(MessageEvent messageEvent) {
        messageEvent.toString();
    }
}
