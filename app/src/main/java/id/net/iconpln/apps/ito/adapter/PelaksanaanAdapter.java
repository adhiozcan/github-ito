package id.net.iconpln.apps.ito.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.ui.StatusPiutangActivity;

/**
 * Created by Ozcan on 27/03/2017.
 */

public class PelaksanaanAdapter extends RecyclerView.Adapter<PelaksanaanAdapter.ViewHolder> {
    private Context         context;
    private List<WorkOrder> woList;

    public PelaksanaanAdapter(Context context, List<WorkOrder> woList) {
        this.context = context;
        this.woList = woList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext   = parent.getContext();
        int     itemLayout = R.layout.adapter_item_workorder;
        View    view       = LayoutInflater.from(mContext).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final WorkOrder wo = woList.get(position);
        holder.txtPelangganId.setText(wo.getPelangganId());
        holder.txtPelangganNama.setText(wo.getNama());
        holder.txtPelangganAlamat.setText(wo.getAlamat());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Pelaksanaan Adapter", "onClick: --------------------------------------------");
                EventBusProvider.getInstance().postSticky(wo);
                Intent statusPiutang = new Intent(context, StatusPiutangActivity.class);
                context.startActivity(statusPiutang);
            }
        });
    }

    @Override
    public int getItemCount() {
        return woList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNomorWo;
        private TextView txtPelangganId;
        private TextView txtPelangganNama;
        private TextView txtPelangganAlamat;

        public ViewHolder(View itemView) {
            super(itemView);
            txtPelangganId = (TextView) itemView.findViewById(R.id.pelanggan_id);
            txtPelangganNama = (TextView) itemView.findViewById(R.id.pelanggan_nama);
            txtPelangganAlamat = (TextView) itemView.findViewById(R.id.pelanggan_alamat);
        }
    }

    /**
     * Produce broadcast for work order data
     *
     * @param woPosition
     * @return
     */
    private WorkOrder produceMessageWorkOrder(int woPosition) {
        Log.d("Pelaksanaan Adapter", "onClick: " + woList.get(woPosition));
        return woList.get(woPosition);
    }
}
