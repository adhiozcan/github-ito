package id.net.iconpln.apps.ito.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.WorkOrder;

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
        holder.txtPelangganId.setText(tusbung.getKodePetugas());
        holder.txtPelangganAlamat.setText(tusbung.getNoTul());
    }

    @Override
    public int getItemCount() {
        return tusbungList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtPelangganId;
        private TextView txtPelangganAlamat;
        private TextView txtStatusSynch;

        public ViewHolder(View itemView) {
            super(itemView);
            txtPelangganId = (TextView) itemView.findViewById(R.id.pelanggan_id);
            txtPelangganAlamat = (TextView) itemView.findViewById(R.id.pelanggan_alamat);
            txtStatusSynch = (TextView) itemView.findViewById(R.id.status_sinkronisasi_upload);
        }
    }
}
