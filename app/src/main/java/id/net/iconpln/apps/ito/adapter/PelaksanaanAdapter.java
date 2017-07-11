package id.net.iconpln.apps.ito.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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
    private String          tagTab;
    private List<WorkOrder> woList;

    public PelaksanaanAdapter(Context context, String tagTab, List<WorkOrder> woList) {
        this.context = context;
        this.tagTab = tagTab;
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
        holder.txtNoTul.setText("VI-01 | " + wo.getNoTul601());
        holder.txtPelangganId.setText(wo.getPelangganId());
        holder.txtPelangganNama.setText(wo.getNama());
        holder.txtPelangganAlamat.setText(wo.getAlamat());

        if (tagTab.equals("selesai")) {
            holder.txtStatusUpload.setText(wo.getStatusSinkronisasi());
            if (wo.isUploaded()) {
                holder.txtStatusUpload.setTextColor(ContextCompat.getColor(context, R.color.material_green));
                holder.txtStatusUpload.setVisibility(View.VISIBLE);
            } else {
                holder.txtStatusUpload.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
                holder.txtStatusUpload.setVisibility(View.VISIBLE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Pelaksanaan Adapter", "onClick: --------------------------------------------");
                EventBusProvider.getInstance().postSticky(wo);
                Intent statusPiutang = new Intent(context, StatusPiutangActivity.class);
                statusPiutang.putExtra("tag_tab", tagTab);
                context.startActivity(statusPiutang);
            }
        });
    }

    @Override
    public int getItemCount() {
        return woList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNoTul;
        private TextView txtPelangganId;
        private TextView txtStatusUpload;
        private TextView txtPelangganNama;
        private TextView txtPelangganAlamat;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNoTul = (TextView) itemView.findViewById(R.id.nomor_tul);
            txtPelangganId = (TextView) itemView.findViewById(R.id.pelanggan_id);
            txtStatusUpload = (TextView) itemView.findViewById(R.id.status_upload);
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
