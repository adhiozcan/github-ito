package id.net.iconpln.apps.ito.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.helper.NotifyAdapterChangeListener;
import id.net.iconpln.apps.ito.model.Tusbung;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.RefreshEvent;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.ui.fragment.ItoDialog;
import io.realm.Realm;

/**
 * Created by Ozcan on 11/04/2017.
 */

public class SyncPendingAdapter extends RecyclerView.Adapter<SyncPendingAdapter.ViewHolder> {
    private Context                     mContext;
    private NotifyAdapterChangeListener mNotifyChangeListener;

    private List<Tusbung> mTusbungList;

    public SyncPendingAdapter(Context context, List<Tusbung> mTusbungList,
                              NotifyAdapterChangeListener adapterChangeListener) {
        this.mContext = context;
        this.mNotifyChangeListener = adapterChangeListener;
        this.mTusbungList = mTusbungList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext   = parent.getContext();
        int     itemLayout = R.layout.adapter_item_synch_pending;
        View    view       = LayoutInflater.from(mContext).inflate(itemLayout, parent, false);
        return new SyncPendingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Tusbung tusbung = mTusbungList.get(position);
        holder.txtNotul.setText("VI-01 | " + tusbung.getNoTul());
        holder.txtPelangganId.setText(tusbung.getPelangganId());
        holder.txtPelangganNama.setText(tusbung.getNamaPelanggan());
        holder.txtPelangganId.setText(tusbung.getKodePetugas());
        holder.txtPelangganAlamat.setText(tusbung.getAlamat());

        String status = tusbung.getStatusSinkron();
        if (status != null) {
            if (status.equals(Constants.SINKRONISASI_SUKSES)) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.txtStatusSynch.setText("Sukses");
            } else if (status.equals(Constants.SINKRONISASI_PROSES)) {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.txtStatusSynch.setText("Proses");
            } else if (status.equals(Constants.SINKRONISASI_PENDING)) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.txtStatusSynch.setText("Pending");
            } else {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.txtStatusSynch.setText("Gagal");
                holder.txtKeterangan.setVisibility(View.VISIBLE);
            }
        }

        if (tusbung.getKeteranganSinkron() != null) {
            System.out.println("Keterangan Sinkron : " + tusbung.getKeteranganSinkron());
            holder.txtKeterangan.setText(tusbung.getKeteranganSinkron());
            holder.txtKeterangan.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ItoDialog.simpleAlert(mContext, "Apakah Anda yakin akan menghapus data ini ?", new ItoDialog.Action() {
                    @Override
                    public void onYesButtonClicked() {
                        LocalDb.makeSafeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.where(Tusbung.class)
                                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                                        .equalTo("noWo", tusbung.getNoWo())
                                        .findFirst()
                                        .setStatusSinkron("");

                                realm.where(WorkOrder.class)
                                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                                        .equalTo("noWo", tusbung.getNoWo())
                                        .findFirst()
                                        .setUploaded(false);

                                realm.where(WorkOrder.class)
                                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                                        .equalTo("noWo", tusbung.getNoWo())
                                        .findFirst()
                                        .setStatusPiutang("");

                                realm.where(WorkOrder.class)
                                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                                        .equalTo("noWo", tusbung.getNoWo())
                                        .findFirst()
                                        .setStatusSinkronisasi("");
                            }
                        });

                        mTusbungList.remove(position);
                        notifyDataSetChanged();
                        mNotifyChangeListener.onNotifyDataSetChanged();

                        EventBusProvider.getInstance().post(new RefreshEvent());
                    }

                    @Override
                    public void onNoButtonClicked() {

                    }
                });
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTusbungList.size();
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
