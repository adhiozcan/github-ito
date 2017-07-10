package id.net.iconpln.apps.ito.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.ChartVPAdapter;
import id.net.iconpln.apps.ito.model.WoSummary;
import id.net.iconpln.apps.ito.socket.ParamDef;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.storage.LocalDb;
import id.net.iconpln.apps.ito.storage.StorageTransaction;
import id.net.iconpln.apps.ito.utility.SynchUtils;
import io.realm.Realm;

import static id.net.iconpln.apps.ito.utility.IntegerUtils.checkNull;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private int[] mPieChartData;
    private int[] mBarChartData;

    private ChartVPAdapter mVpAdapter;
    private ViewPager      mViewPager;
    private List<Fragment> mChartFragments;
    private TabLayout      mTabLayout;

    private TextView txtBelumPutus,
            txtBelumPutusLunas,
            txtPutus,
            txtPutusLunas,
            txtSambung,
            txtTotal,
            txtBelumBongkar,
            txtBongkar,
            btnCleanAll;

    private View      statisticPanel;
    private ImageView btnArrowStatistic;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initViewComponents(rootView);
        initViewPager();

        mPieChartData = new int[7];
        mBarChartData = new int[7];

        checkAvailableData();

        btnArrowStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toogleStatisticPanel();
            }
        });

        btnCleanAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bersihkanSemuaData();
            }
        });

        return rootView;
    }

    private void initViewComponents(View rootView) {
        mViewPager = (ViewPager) rootView.findViewById(R.id.vp_chart);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);

        btnCleanAll = (TextView) rootView.findViewById(R.id.btn_clean_all);
        txtTotal = (TextView) rootView.findViewById(R.id.value_total);

        txtBelumPutus = (TextView) rootView.findViewById(R.id.value_belum_putus);
        txtBelumPutusLunas = (TextView) rootView.findViewById(R.id.value_belum_putus_sudah_lunas);
        txtPutus = (TextView) rootView.findViewById(R.id.value_sudah_putus);
        txtPutusLunas = (TextView) rootView.findViewById(R.id.value_sudah_putus_sudah_lunas);
        txtSambung = (TextView) rootView.findViewById(R.id.value_sambung);
        txtBelumBongkar = (TextView) rootView.findViewById(R.id.value_belum_bongkar);
        txtBongkar = (TextView) rootView.findViewById(R.id.value_bongkar);

        statisticPanel = rootView.findViewById(R.id.panel_statistic);
        btnArrowStatistic = (ImageView) rootView.findViewById(R.id.action_statistic_arrow);
    }

    private void initViewPager() {
        mChartFragments = new ArrayList<>();
        mVpAdapter = new ChartVPAdapter(getChildFragmentManager(), mChartFragments);
        mViewPager.setAdapter(mVpAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void checkAvailableData() {
        Log.d(TAG, "[Check Data] :-----------------------------------------------------------");
        if (getDataFromStorage() != null) {
            WoSummary woSummary = getDataFromStorage();
            setChartData(woSummary);
            setLegend(woSummary);

            Log.d(TAG, "[Get Offline] : WoSummary" + woSummary.toString());
        } else {
            getDataFromNetwork();

            Log.d(TAG, "[Get Server] : WoSummary");
        }
    }

    private void getDataFromNetwork() {
        SocketTransaction.getInstance().sendMessage(ParamDef.GET_WO_CHART);
    }

    private WoSummary getDataFromStorage() {
        StorageTransaction<WoSummary> storageTransaction = new StorageTransaction<>();
        return storageTransaction.find(WoSummary.class);
    }

    private void setChartData(WoSummary woSummary) {
        mPieChartData[0] = checkNull(woSummary.getBelumPutus());
        mPieChartData[1] = checkNull(woSummary.getBelumPutusSudahLunas());
        mPieChartData[2] = checkNull(woSummary.getSudahputus());
        mPieChartData[3] = checkNull(woSummary.getSudahPutusSudahLunas());
        mPieChartData[4] = checkNull(woSummary.getSambung());
        mPieChartData[5] = checkNull(woSummary.getBongkar());
        mPieChartData[6] = checkNull(woSummary.getBelumBongkar());

        mBarChartData[0] = checkNull(woSummary.getBelumPutus());
        mBarChartData[1] = checkNull(woSummary.getBelumPutusSudahLunas());
        mBarChartData[2] = checkNull(woSummary.getSudahputus());
        mBarChartData[3] = checkNull(woSummary.getSudahPutusSudahLunas());
        mBarChartData[4] = checkNull(woSummary.getSambung());
        mBarChartData[5] = checkNull(woSummary.getBongkar());
        mBarChartData[6] = checkNull(woSummary.getBelumBongkar());

        mChartFragments.add(BarChartFragment.newInstance(mBarChartData));
        mChartFragments.add(PieChartFragment.newInstance(mPieChartData));

        mViewPager.getAdapter().notifyDataSetChanged();
    }

    private void setLegend(WoSummary woSummary) {
        txtBelumPutus.setText(woSummary.getBelumPutus());
        txtBelumPutusLunas.setText(woSummary.getBelumPutusSudahLunas());
        txtPutus.setText(woSummary.getSudahputus());
        txtPutusLunas.setText(woSummary.getSudahPutusSudahLunas());
        txtSambung.setText(woSummary.getSambung());
        txtBelumBongkar.setText(woSummary.getBelumBongkar());
        txtBongkar.setText(woSummary.getBongkar());
        txtTotal.setText(woSummary.countTotal());
    }

    private void toogleStatisticPanel() {
        boolean isShown = Integer.parseInt(statisticPanel.getTag().toString()) == 1 ? true : false;
        if (isShown) {
            btnArrowStatistic.setImageResource(R.drawable.ic_arrow_down);
            statisticPanel.setVisibility(View.GONE);
            statisticPanel.setTag(0);
        } else {
            btnArrowStatistic.setImageResource(R.drawable.ic_arrow_up);
            statisticPanel.setVisibility(View.VISIBLE);
            statisticPanel.setTag(1);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBusProvider.getInstance().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventReceived(WoSummary woSummary) {
        setChartData(woSummary);
        setLegend(woSummary);

        /**
         * Saving to local
         */
        StorageTransaction<WoSummary> storageTransaction = new StorageTransaction<>();
        storageTransaction.save(WoSummary.class, woSummary);
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
            }

            @Override
            public void onNoButtonClicked() {
            }
        };
        ItoDialog.simpleAlert(getActivity(), message, action);
    }
}
