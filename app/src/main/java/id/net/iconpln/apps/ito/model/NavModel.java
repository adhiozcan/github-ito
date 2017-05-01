package id.net.iconpln.apps.ito.model;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class NavModel {
    private int    icon;
    private String name;

    public NavModel(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
