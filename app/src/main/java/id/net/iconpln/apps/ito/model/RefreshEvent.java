package id.net.iconpln.apps.ito.model;

/**
 * Created by Ozcan on 04/05/2017.
 */

public class RefreshEvent {
    private boolean isRefresh;

    public RefreshEvent(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }
}
