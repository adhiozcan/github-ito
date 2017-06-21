package id.net.iconpln.apps.ito.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.model.Tusbung;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class SyncPendingAdapter extends RecyclerView.Adapter<SyncPendingAdapter.ViewHolder> {
    private List<Tusbung> tusbungList;

    public SyncPendingAdapter(List<Tusbung> tusbungList) {
        this.tusbungList = tusbungList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext   = parent.getContext();
        int     itemLayout = R.layout.adapter_item_synch_pending;
        View    view       = LayoutInflater.from(mContext).inflate(itemLayout, parent, false);
        return new SyncPendingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tusbung tusbung = tusbungList.get(position);
        holder.txtNotul.setText("VI-1 | " + tusbung.getNoTul());
        holder.txtPelangganId.setText(tusbung.getPelangganId());
        holder.txtPelangganNama.setText(tusbung.getNamaPelanggan());
        holder.txtPelangganId.setText(tusbung.getKodePetugas());
        holder.txtPelangganAlamat.setText(tusbung.getAlamat());

        String status = tusbung.getStatusSinkron();
        if (status != null) {
            if (status.equals(Constants.SINKRONISASI_SUKSES)) {
                holder.progressBar.setVisibility(View.GONE);
                holder.txtStatusSynch.setText("Sukses");
            } else if (status.equals(Constants.SINKRONISASI_PROSES)) {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.txtStatusSynch.setText("Proses");
            } else if (status.equals(Constants.SINKRONISASI_PENDING)) {
                holder.progressBar.setVisibility(View.GONE);
                holder.txtStatusSynch.setText("Pending");
            } else {
                holder.progressBar.setVisibility(View.GONE);
                holder.txtStatusSynch.setText("Gagal");
                holder.txtKeterangan.setVisibility(View.VISIBLE);
                holder.txtKeterangan.setText(tusbung.getKeteranganSinkron());
            }
        }
    }

    @Override
    public int getItemCount() {
        return tusbungList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private TextView    txtNotul;
        private TextView    txtPelangganId;
        private TextView    txtPelangganNama;
        private TextView    txtPelangganAlamat;
        private TextView    txtStatusSynch;
        private TextView    txtKeterangan;

        public ViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loading_sync_up);
            txtNotul = (TextView) itemView.findViewById(R.id.nomor_tul);
            txtPelangganNama = (TextView) itemView.findViewById(R.id.pelanggan_nama);
            txtPelangganId = (TextView) itemView.findViewById(R.id.pelanggan_id);
            txtPelangganNama = (TextView) itemView.findViewById(R.id.pelanggan_nama);
            txtPelangganAlamat = (TextView) itemView.findViewById(R.id.pelanggan_alamat);
            txtStatusSynch = (TextView) itemView.findViewById(R.id.status_sinkronisasi_upload);
            txtKeterangan = (TextView) itemView.findViewById(R.id.keterangan_sinkronisasi);
        }
    }
}
