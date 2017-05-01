package id.net.iconpln.apps.ito;

/**
 * Created by Ozcan on 13/04/2017.
 */

public interface EventBusSubscriber<obj extends Object> {
    void onResponse();
}
