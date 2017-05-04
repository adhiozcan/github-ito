package id.net.iconpln.apps.ito.helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.utility.Formater;


/**
 * Created by rizmaulana on 23/01/17.
 */

public class RMDatePickerDialog implements View.OnClickListener {

    public static final String YEAR                = "YEAR";
    public static final String MONTH_AND_YEAR      = "MONTANDYEAR";

    private int gMonth, gYear;
    private Dialog dialog;
    private OnDatePickerSet onDatePickerSet;
    private String gTipe;

    //ComponentView
    //View Component
    private TextView mTxtPeriode, mTxtBulan, mTxtTahun, mTxtBatal, mTxtOk;
    private ImageView mImgNextMonth, mImgPrevMonth, mImgNextYear, mImgPrevYear;
    private LinearLayout mLayoutMonth, mLayoutYear;

    public RMDatePickerDialog(Context context, String tipe, final int month, final int year, final OnDatePickerSet onDatePickerSet){

        gMonth = month;
        gYear = year;
        gTipe = tipe;
        this.onDatePickerSet = onDatePickerSet;


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.rm_date_picker_layout, null);


        mTxtPeriode = (TextView) view.findViewById(R.id.text_periode);
        mTxtBulan = (TextView) view.findViewById(R.id.text_bulan);
        mTxtTahun = (TextView) view.findViewById(R.id.text_tahun);
        mTxtBatal = (TextView) view.findViewById(R.id.text_batal);
        mTxtOk = (TextView) view.findViewById(R.id.text_ok);
        mImgNextMonth = (ImageView) view.findViewById(R.id.img_next_month);
        mImgPrevMonth = (ImageView) view.findViewById(R.id.img_prev_mont);
        mImgNextYear = (ImageView) view.findViewById(R.id.img_next_year);
        mImgPrevYear = (ImageView) view.findViewById(R.id.img_prev_year);
        mLayoutYear = (LinearLayout) view.findViewById(R.id.layout_year);
        mLayoutMonth = (LinearLayout) view.findViewById(R.id.layout_month);

        setDate();

        mTxtBulan.setText(Formater.numberToMonth(gMonth));
        mTxtTahun.setText(String.valueOf(gYear));

        mTxtBatal.setOnClickListener(this);
        mTxtOk.setOnClickListener(this);
        mImgNextMonth.setOnClickListener(this);
        mImgPrevMonth.setOnClickListener(this);
        mImgPrevYear.setOnClickListener(this);
        mImgNextYear.setOnClickListener(this);


        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.text_ok:
                onDatePickerSet.onSet(gMonth, gYear);
                dismiss();
                break;

            case R.id.text_batal:
                dismiss();
                break;

            case R.id.img_next_month:
                if (gMonth == 12)
                    gMonth = 1;
                else
                    gMonth++;
                mTxtBulan.setText(Formater.numberToMonth(gMonth));
                setDate();
                break;

            case R.id.img_prev_mont:
                if (gMonth == 1)
                    gMonth = 12;
                else
                    gMonth--;
                mTxtBulan.setText(Formater.numberToMonth(gMonth));
                setDate();
                break;

            case R.id.img_next_year:
                gYear++;
                mTxtTahun.setText(String.valueOf(gYear));
                setDate();
                break;

            case R.id.img_prev_year:
                gYear--;
                mTxtTahun.setText(String.valueOf(gYear));
                setDate();
                break;

        }
    }

    private void setDate(){
        //set
        switch (gTipe){
            case YEAR:
                mLayoutMonth.setVisibility(View.GONE);
                mTxtPeriode.setText(String.valueOf(gYear));
                break;
            case MONTH_AND_YEAR:
                mTxtPeriode.setText(Formater.numberToMonth(gMonth)+" "+gYear);
                break;
        }
    }

    public interface OnDatePickerSet{
        public void onSet(int month, int year);
    }


}


