package id.net.iconpln.apps.ito.model.eventbus;

/**
 * Created by Ozcan on 17/07/2017.
 */

public class NetworkAvailabilityEvent {
    public NetworkAvailabilityEvent(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable;
}
