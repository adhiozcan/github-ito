package id.net.iconpln.apps.ito.model;

/**
 * Created by Ozcan on 19/07/2017.
 */

public class WorkSummary {
    private int pelaksanaan;
    private int selesai;

    public WorkSummary(int pelaksanaan, int selesai) {
        this.pelaksanaan = pelaksanaan;
        this.selesai = selesai;
    }

    public int getPelaksanaan() {
        return pelaksanaan;
    }

    public int getSelesai() {
        return selesai;
    }
}
