package id.net.iconpln.apps.ito;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class EventBusProvider extends EventBus {
    private static EventBus BUS;

    public static EventBus getInstance() {
        if (BUS == null) BUS = EventBus.getDefault();
        return BUS;
    }

    private EventBusProvider() {
    }
}
