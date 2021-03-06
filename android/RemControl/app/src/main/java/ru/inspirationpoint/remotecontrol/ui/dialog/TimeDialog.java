package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData;

public class TimeDialog extends DialogFragment implements NumberPicker.OnValueChangeListener{

    private final static long MINUTE_MS = 60 * 1000;
    private final static long SECOND_MS = 1000;
    private final static long MAX_DURATION_MS = 59 * 60 * 1000;
    private final static long MIN_DURATION_MS = 1000;
    private final static String CURRENT_DURATION_PARAM = "current_duration";
    private Listener mListener;
    private int minsDec = 0;
    private int mins = 3;
    private int secsDec = 0;
    private int secs = 0;
    private long mCurrentDurationMS;

    public static void show(FragmentActivity fragmentActivity, int mins, int secs, int santisecs) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        TimeDialog myDialogFragment = new TimeDialog();
        Bundle params = new Bundle();
        params.putInt("mins", mins);
        params.putInt("secs", secs);
        params.putInt("santisecs", santisecs);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "TimeDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCurrentDurationMS = getArguments().getLong(CURRENT_DURATION_PARAM);
        Log.d("LONG TIME", String.valueOf(mCurrentDurationMS));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        View contentView = inflater.inflate(R.layout.time_dlg, null);

        TextView titleTextView = contentView.findViewById(R.id.title);
        titleTextView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        titleTextView.setText(R.string.time_period);

        ImageButton closeButton = contentView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);

        mins = getArguments().getInt("mins");

        NumberPicker npMinDec = contentView.findViewById(R.id.picker_mins_dec);
        npMinDec.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        npMinDec.setOnValueChangedListener(this);
        npMinDec.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        npMinDec.setValue(mins/10);

        NumberPicker npMin = contentView.findViewById(R.id.picker_mins_units);
        npMin.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        npMin.setOnValueChangedListener(this);
        npMin.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        npMin.setValue(mins%10);

        NumberPicker npSecDec = contentView.findViewById(R.id.picker_secs_dec);
        npSecDec.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        npSecDec.setOnValueChangedListener(this);
        npSecDec.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        secsDec = getArguments().getInt("secs")/10;
        npSecDec.setValue(secsDec);

        NumberPicker npSecUnit = contentView.findViewById(R.id.picker_secs_units);
        npSecUnit.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        npSecUnit.setOnValueChangedListener(this);
        npSecUnit.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        secs = getArguments().getInt("secs")%10;
        npSecUnit.setValue(secs);

        Dialog d = builder.setView(contentView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mCurrentDurationMS = (minsDec*10+mins)*MINUTE_MS +
                                    (secsDec*10 + secs)*SECOND_MS;
                            if (mCurrentDurationMS > MAX_DURATION_MS) {
                                mCurrentDurationMS = MAX_DURATION_MS;
                            } else if (mCurrentDurationMS < MIN_DURATION_MS) {
                                mCurrentDurationMS = MIN_DURATION_MS;
                            }
                            mListener.onDurationSet(FightActionData.ActionPeriod.other, mCurrentDurationMS);
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

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        switch (numberPicker.getId()) {
            case R.id.picker_mins_dec:
                minsDec = i1;
                break;
            case R.id.picker_mins_units:
                mins = i1;
                break;
            case R.id.picker_secs_dec:
                secsDec = i1;
                break;
            case R.id.picker_secs_units:
                secs = i1;
                break;
        }
    }

    public interface Listener {
        void onDurationSet(FightActionData.ActionPeriod timePeriod, long otherDuration);
    }
}
