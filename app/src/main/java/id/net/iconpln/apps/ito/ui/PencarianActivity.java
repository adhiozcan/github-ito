package id.net.iconpln.apps.ito.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.adapter.PencarianAdapter;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.utility.CommonUtils;
import io.realm.Realm;

/**
 * Created by Ozcan on 29/05/2017.
 */

public class PencarianActivity extends AppCompatActivity {
    private PencarianAdapter mAdapter;
    private RecyclerView     mRecyclerView;
    private List<WorkOrder>  mWorkOrderList;

    private EditText edPencarian;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pencarian);

        mWorkOrderList = new ArrayList<>(getLocalData());
        mAdapter = new PencarianAdapter(this, mWorkOrderList);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(CommonUtils.getVerticalLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        edPencarian = (EditText) findViewById(R.id.text_pencarian_input);
        edPencarian.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence constraint, int i, int i1, int i2) {
                mAdapter.getFilter().filter(constraint);
            }

            @Override
            public void afterTextChanged(Editable constraint) {
            }
        });
    }

    private List<WorkOrder> getLocalData() {
        Realm realm = Realm.getDefaultInstance();
        return realm.copyFromRealm(realm.where(WorkOrder.class).findAll());
    }
}
