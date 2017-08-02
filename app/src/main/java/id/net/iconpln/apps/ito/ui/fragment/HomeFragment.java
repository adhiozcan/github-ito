package id.net.iconpln.apps.ito.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.ChartVPAdapter;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.helper.WorkOrderComparator;
import id.net.iconpln.apps.ito.job.WoUploadService;
import id.net.iconpln.apps.ito.model.Statistic;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.WorkSummary;
import id.net.iconpln.apps.ito.model.eventbus.WoUploadServiceEvent;
import id.net.iconpln.apps.ito.model.eventbus.WorkOrderEvent;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.storage.StorageTransaction;
import id.net.iconpln.apps.ito.utility.SmileyLoading;
import id.net.iconpln.apps.ito.utility.SynchUtils;
import io.realm.Realm;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class HomeFragment extends Fragment {
    private static String TAG = HomeFragment.class.getSimpleName();

    private int[] mPieChartData;

    private ChartVPAdapter mVpAdapter;
    private ViewPager      mViewPager;
    private List<Fragment> mChartFragments;

    private ViewGroup vgDataWorkOrder;
    private TextView  txtTotal;
    private TextView  txtPosisiData;
    private TextView  btnHapusData;

    private List<WorkOrder> mWoBackupList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        EventBusProvider.getInstance().register(this);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initViewComponents(rootView);
        initViewPager();

        mPieChartData = new int[7];

        updatePosisiData();
        presentStatistic();
        btnHapusData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bersihkanSemuaData();
            }
        });

        return rootView;
    }

    private void updatePosisiData() {
        if (SynchUtils.getLastSynchLog() == null) {
            txtPosisiData.setText("Posisi data terakhir adalah pada -");
        } else {
            txtPosisiData.setText("Posisi data terakhir adalah pada "
                    + SynchUtils.getLastSynchLog().getTanggal() + "  "
                    + SynchUtils.getLastSynchLog().getWaktu());
        }
    }

    private void initViewComponents(View rootView) {
        mViewPager = (ViewPager) rootView.findViewById(R.id.vp_chart);
        vgDataWorkOrder = (ViewGroup) rootView.findViewById(R.id.data_work_order);
        btnHapusData = (TextView) rootView.findViewById(R.id.btn_clean_all);
        txtTotal = (TextView) rootView.findViewById(R.id.value_total);
        txtPosisiData = (TextView) rootView.findViewById(R.id.txt_posisi_data);
    }

    private void initViewPager() {
        mChartFragments = new ArrayList<>();
        mVpAdapter = new ChartVPAdapter(getChildFragmentManager(), mChartFragments);
        mViewPager.setAdapter(mVpAdapter);
    }

    private void presentStatistic() {
        List<WorkOrder> woPelaksanaan = new ArrayList<>();
        woPelaksanaan.addAll(LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance()
                        .where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("isWoUlang", false)
                        .findAll()));

        List<WorkOrder> woUlang = new ArrayList<>();
        woUlang.addAll(LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance()
                        .where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("isWoUlang", true)
                        .findAll()));

        List<WorkOrder> woTotal = new ArrayList<>();
        woTotal.addAll(LocalDb.getInstance()
                .where(WorkOrder.class)
                .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                .findAll());

        List<WorkOrder> woSelesai = new ArrayList<>();
        woSelesai.addAll(LocalDb.getInstance()
                .where(WorkOrder.class)
                .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                .equalTo("isSelesai", true)
                .findAll());

        Statistic statistic = new Statistic();
        statistic.setSumWo(woPelaksanaan.size());
        statistic.setSumWoUlang(woUlang.size());
        statistic.setSumWoSelesai(woSelesai.size());

        WorkSummary workSummary = new WorkSummary((woTotal.size() - woSelesai.size()), woSelesai.size());
        setChartData(workSummary);
        updateStatisticView(statistic);
    }

    private void updateStatisticView(Statistic statistic) {
        txtTotal.setText("" + (statistic.getSumWo() + statistic.getSumWoUlang()));
    }

    private void setChartData(WorkSummary workSummary) {
        mPieChartData[0] = workSummary.getPelaksanaan();
        mPieChartData[1] = workSummary.getSelesai();

        /*
        mBarChartData[0] = checkNull(woSummary.getBelumPutus());
        mBarChartData[1] = checkNull(woSummary.getBelumPutusSudahLunas());
        mBarChartData[2] = checkNull(woSummary.getSudahputus());
        mBarChartData[3] = checkNull(woSummary.getSudahPutusSudahLunas());
        mBarChartData[4] = checkNull(woSummary.getSambung());
        mBarChartData[5] = checkNull(woSummary.getBongkar());
        mBarChartData[6] = checkNull(woSummary.getBelumBongkar());
        */

        //mChartFragments.add(BarChartFragment.newInstance(mBarChartData));
        mChartFragments.add(PieChartFragment.newInstance(mPieChartData));

        mViewPager.getAdapter().notifyDataSetChanged();
    }

    private void bersihkanSemuaData() {
        String message = "Apakah anda yakin akan menghapus semua data ?";
        SynchUtils.writeSynchLog(SynchUtils.LOG_DEL_ALL);
        ItoDialog.Action action = new ItoDialog.Action() {
            @Override
            public void onYesButtonClicked() {
                Hawk.deleteAll();
                LocalDb.makeSafeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        LocalDb.getInstance().deleteAll();
                    }
                });

                presentStatistic();
            }

            @Override
            public void onNoButtonClicked() {
            }
        };
        ItoDialog.simpleAlert(getActivity(), message, action);
    }

    private void backupWoSelesai() {
        mWoBackupList = new ArrayList<>();
        List<WorkOrder> localWoSelesai = LocalDb.getInstance().copyFromRealm(
                LocalDb.getInstance().where(WorkOrder.class)
                        .equalTo("kodePetugas", AppConfig.KODE_PETUGAS)
                        .equalTo("isSelesai", true)
                        .findAll());
        mWoBackupList.addAll(localWoSelesai);
        System.out.println("mWoBackupList : " + localWoSelesai.size());

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(WorkOrder.class).findAll().deleteAllFromRealm();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusProvider.getInstance().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            backupWoSelesai();
            SmileyLoading.show(getActivity(), "Mohon tunggu sebentar");
            SocketTransaction.getInstance().sendMessage(ParamDef.GET_WO);
            SocketTransaction.getInstance().sendMessage(ParamDef.GET_WO_ULANG);
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataWoReceived(final WorkOrderEvent woEvent) {
        Log.d(TAG, "onDataWoReceived : " + AppConfig.KODE_PETUGAS);
        Log.d(TAG, "------------------------------------------");
        Log.d(TAG, "Wo ulang : " + woEvent.isUlang + ", Total Data : " + woEvent.workOrders.length);
        Log.d(TAG, "Total Backup data : " + mWoBackupList.size());

        final List<WorkOrder> woFromServer = Arrays.asList(woEvent.workOrders);
        final List<WorkOrder> woClean      = new ArrayList<>();
        woClean.addAll(mWoBackupList);

        System.out.println("[Starting] Proses pembandingan");
        WorkOrderComparator comparator = new WorkOrderComparator();
        if (woEvent.isUlang) {
            for (WorkOrder serverWo : woFromServer) {
                boolean found = false;
                for (WorkOrder localWo : mWoBackupList) {
                    if (comparator.compare(localWo, serverWo) == 1) {
                        System.out.println("Data sama " + serverWo.getNoWo());
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("Menambahkan wo baru : " + serverWo.getNoWo());
                    woClean.add(serverWo);
                }
            }
            System.out.println("Data terbackup : " + mWoBackupList.size());
            System.out.println("Data wo ulang yang akan ditambahkan " + woClean.size());
            System.out.println("-----------------------------------------");

        } else {
            for (WorkOrder serverWo : woFromServer) {
                boolean found = false;
                System.out.println(serverWo.getKodePetugas() + " | " + AppConfig.KODE_PETUGAS);
                for (WorkOrder localWo : mWoBackupList) {
                    if (comparator.compare(localWo, serverWo) == 1) {
                        System.out.println("Data sama " + serverWo.getNoWo());
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("Menambahkan wo baru : " + serverWo.getNoWo());
                    woClean.add(serverWo);
                }
            }

            System.out.println("Data terbackup : " + mWoBackupList.size());
            System.out.println("Data wo yang akan ditambahkan " + woClean.size());
            System.out.println("-----------------------------------------");
        }

        LocalDb.makeSafeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < woClean.size(); i++) {
                    realm.insertOrUpdate(woClean);
                }
            }
        });

        int totalWoSelesai = 0;
        for (WorkOrder woSelesai : mWoBackupList) {
            if (woSelesai.isSelesai()) {
                totalWoSelesai = totalWoSelesai + 1;
            }
        }
        StorageTransaction<Statistic> transaction = new StorageTransaction<>();
        Statistic                     statistic   = transaction.find(Statistic.class);
        statistic.setSumWoSelesai(totalWoSelesai);
        if (woEvent.isUlang) {
            statistic.setSumWoUlang(woEvent.workOrders.length);
        } else {
            statistic.setSumWo(woEvent.workOrders.length);
        }

        transaction.save(Statistic.class, statistic);
        SynchUtils.writeSynchLog(SynchUtils.LOG_UNDUH, String.valueOf(woEvent.workOrders.length));

        updatePosisiData();
        presentStatistic();
        SmileyLoading.shouldCloseDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUploadBackgroundFinished(WoUploadServiceEvent wusEvent) {
        System.out.println("[Service] Stop uploading in the background");
    }
}