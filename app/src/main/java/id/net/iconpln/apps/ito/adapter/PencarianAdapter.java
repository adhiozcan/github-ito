package id.net.iconpln.apps.ito.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.ui.StatusPiutangActivity;

/**
 * Created by Ozcan on 29/05/2017.
 */

public class PencarianAdapter extends RecyclerView.Adapter<PencarianAdapter.ViewHolder> implements Filterable {
    private Context         context;
    private WorkOrderFilter workOrderFilter;

    // original list, filtering do with this list
    private List<WorkOrder> workOrderList;

    // filtered list construct by filtering the workOrderList;
    private List<WorkOrder> filteredList;

    public PencarianAdapter(Context context, List<WorkOrder> workOrderList) {
        this.context = context;
        this.workOrderList = workOrderList;
        this.filteredList = workOrderList;

        getFilter();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext   = parent.getContext();
        int     itemLayout = R.layout.adapter_item_pencarian;
        View    view       = LayoutInflater.from(mContext).inflate(itemLayout, parent, false);
        return new PencarianAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WorkOrder wo = filteredList.get(position);
        holder.txtNoTul.setText(wo.getNoTul601());
        holder.txtNamaPelanggan.setText(wo.getNama());
        holder.txtAlamatPelanggan.setText(wo.getAlamat());
        String status = (wo.getTanggalPelunasan() == null) ? "Belum Lunas" : "Lunas";
        holder.txtStatusLunas.setText(status);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Pencarian Adapter", "onClick: --------------------------------------------");
                EventBusProvider.getInstance().postSticky(wo);
                Intent statusPiutang = new Intent(context, StatusPiutangActivity.class);
                context.startActivity(statusPiutang);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        if (workOrderFilter == null) {
            workOrderFilter = new WorkOrderFilter();
        }
        return workOrderFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNoTul;
        private TextView txtStatusLunas;
        private TextView txtNamaPelanggan;
        private TextView txtAlamatPelanggan;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNoTul = (TextView) itemView.findViewById(R.id.nomor_tul);
            txtStatusLunas = (TextView) itemView.findViewById(R.id.status_lunas);
            txtNamaPelanggan = (TextView) itemView.findViewById(R.id.pelanggan_nama);
            txtAlamatPelanggan = (TextView) itemView.findViewById(R.id.pelanggan_alamat);
        }
    }

    private class WorkOrderFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<WorkOrder> tempList = new ArrayList<>();

                // search content in workorder list
                for (WorkOrder workOrder : workOrderList) {
                    String idPel   = workOrder.getPelangganId().toLowerCase();
                    String namaPel = workOrder.getNama().toLowerCase();
                    if (idPel.contains(constraint.toString().toLowerCase())) {
                        tempList.add(workOrder);
                        Log.d("Found Data", "performFiltering: " + workOrder.toString());
                    } else if (namaPel.contains(constraint.toString().toLowerCase())) {
                        tempList.add(workOrder);
                        Log.d("Found Data", "performFiltering: " + workOrder.toString());
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
                Log.d("Filter", "FilterResult (Temp): " + tempList.size());
            } else {
                filterResults.count = workOrderList.size();
                filterResults.values = workOrderList;
                Log.d("Filter", "No data found");
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            filteredList = new ArrayList<>((List<WorkOrder>) results.values);
            PencarianAdapter.this.notifyDataSetChanged();
        }
    }
}
