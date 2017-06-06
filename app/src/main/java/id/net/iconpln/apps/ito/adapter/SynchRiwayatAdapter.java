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
    }

    @Override
    public int getItemCount() {
        return riwayatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTanggal;
        private TextView txtWaktu;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTanggal = (TextView) itemView.findViewById(R.id.tanggal_riwayat);
            txtWaktu = (TextView) itemView.findViewById(R.id.jam_riwayat);
        }
    }
}
