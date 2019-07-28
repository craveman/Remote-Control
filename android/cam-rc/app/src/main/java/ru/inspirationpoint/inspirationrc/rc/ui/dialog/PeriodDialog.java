package ru.inspirationpoint.inspirationrc.rc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class PeriodDialog extends DialogFragment implements com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener{

    private final static String PERIOD_PARAM = "period";
    private Listener mListener;
    private int mPeriod;

    private TextView mPeriodTextView;

    public static void show(FragmentActivity fragmentActivity, int period) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        PeriodDialog myDialogFragment = new PeriodDialog();
        Bundle params = new Bundle();
        params.putInt(PERIOD_PARAM, period);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "PeriodDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPeriod = getArguments().getInt(PERIOD_PARAM);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        View contentView = inflater.inflate(R.layout.period_dlg, null);

        TextView titleTextView = contentView.findViewById(R.id.title);
        titleTextView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        titleTextView.setText(R.string.select_period);

        com.shawnlin.numberpicker.NumberPicker picker = contentView.findViewById(R.id.np_period);
        picker.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        picker.setMinValue(1);
        picker.setMaxValue(9);
        picker.setValue(mPeriod);
        picker.setWrapSelectorWheel(true);
        picker.setOnValueChangedListener(this);
        picker.setTypeface(InspirationDayApplication.getCustomFontTypeface());


        ImageButton closeButton = contentView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });

//        Button button = (Button) contentView.findViewById(R.id.next_period);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IrSender.instance().sendNextPeriod(getContext());
//
//                mPeriod++;
//                update();
//            }
//        });
//
//        Button button = (Button) contentView.findViewById(R.id.reset_period);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPeriod = 1;
//                update();
//            }
//        });

//        mPeriodTextView = (TextView) contentView.findViewById(R.id.period);
//
//        update();
//
//        builder.setView(contentView)
//                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (mListener != null) {
//                            mListener.onPeriodChanged(mPeriod);
//                        }
//                    }
//                });

        Dialog d = builder.setView(contentView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onPeriodChanged(mPeriod);
                        }
                    }
                }).show();

        Button btn1 = d.getWindow().findViewById(android.R.id.button1);

        btn1.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btn1.setTextColor(getActivity().getResources().getColor(R.color.textColorSecondary));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn1.getLayoutParams();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        btn1.setLayoutParams(params);

        return d;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

//    private void update() {
//        mPeriodTextView.setText(getString(R.string.period, mPeriod));
//    }

    @Override
    public void onValueChange(com.shawnlin.numberpicker.NumberPicker numberPicker, int i, int i1) {
        mPeriod = i1;
    }

    public interface Listener {
        void onPeriodChanged(int period);
    }
}
