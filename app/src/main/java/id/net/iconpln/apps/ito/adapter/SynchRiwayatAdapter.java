package id.net.iconpln.apps.ito.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.model.Riwayat;
import id.net.iconpln.apps.ito.utility.SynchUtils;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class SynchRiwayatAdapter extends RecyclerView.Adapter<SynchRiwayatAdapter.ViewHolder> {
    List<Riwayat> riwayatList;

    public SynchRiwayatAdapter(List<Riwayat> riwayatList) {
        this.riwayatList = riwayatList;
    }

    @Override
    public SynchRiwayatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext   = parent.getContext();
        int     itemLayout = R.layout.adapter_item_synch_riwayat;
        View    view       = LayoutInflater.from(mContext).inflate(itemLayout, parent, false);
        return new SynchRiwayatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SynchRiwayatAdapter.ViewHolder holder, int position) {
        Riwayat riwayat = riwayatList.get(position);
        holder.txtTanggal.setText(riwayat.getTanggal());
        holder.txtWaktu.setText(riwayat.getWaktu());
        switch (riwayat.getActivity()) {
            case SynchUtils.LOG_UNDUH:
                holder.txtActivity.setText("Unduh Work Order");
                break;
            case SynchUtils.LOG_UPLOAD:
                holder.txtActivity.setText("Unggah Work Order Local");
                break;
            case SynchUtils.LOG_DEL_GAGAL:
                holder.txtActivity.setText("Hapus Work Order Gagal Sinkronisasi");
                break;
            case SynchUtils.LOG_DEL_PENDING:
                holder.txtActivity.setText("Hapus Work Order Pending");
                break;
            case SynchUtils.LOG_DEL_ALL:
                holder.txtActivity.setText("Hapus Semua Data");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return riwayatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTanggal;
        private TextView txtWaktu;
        private TextView txtActivity;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTanggal = (TextView) itemView.findViewById(R.id.tanggal_riwayat);
            txtWaktu = (TextView) itemView.findViewById(R.id.jam_riwayat);
            txtActivity = (TextView) itemView.findViewById(R.id.log_activity);
        }
    }
}
