package id.net.iconpln.apps.ito.helper;

import java.util.Comparator;

import id.net.iconpln.apps.ito.model.WorkOrder;

/**
 * Created by Ozcan on 30/07/2017.
 */

public class WorkOrderComparator implements Comparator<WorkOrder> {
    @Override
    public int compare(WorkOrder localWo, WorkOrder serverWo) {
        return serverWo.getNoWo().equals(localWo.getNoWo()) ? 1 : 0;
    }
}
