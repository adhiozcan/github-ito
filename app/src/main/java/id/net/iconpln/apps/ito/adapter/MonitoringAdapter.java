package id.net.iconpln.apps.ito.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.model.WoMonitoring;

/**
 * Created by Ozcan on 03/05/2017.
 */

public class MonitoringAdapter extends RecyclerView.Adapter<MonitoringAdapter.ViewHolder> {
    private Context            context;
    private List<WoMonitoring> woMonitoringList;

    public MonitoringAdapter(Context context, List<WoMonitoring> woMonitoringList) {
        this.context = context;
        this.woMonitoringList = woMonitoringList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext   = parent.getContext();
        int     itemLayout = R.layout.adapter_item_monitoring;
        View    view       = LayoutInflater.from(mContext).inflate(itemLayout, parent, false);
        return new MonitoringAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WoMonitoring wo = woMonitoringList.get(position);
        holder.txtNoTul.setText(wo.getNoTul601());
        holder.txtNamaPelanggan.setText(wo.getNama());
        holder.txtAlamatPelanggan.setText(wo.getAlamat());
        String status = (wo.getTanggalPelunasan() == null) ? "Belum Lunas" : "Lunas";
        holder.txtStatusLunas.setText(status);
    }

    @Override
    public int getItemCount() {
        return woMonitoringList.size();
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
}
