package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class SyncDialog extends DialogFragment {

    private final static String MESSAGE_ID_PARAM = "message_id";
    private final static String INPUT_CODES = "input";
    private final static String OLD_CODE = "old";
    private AlertDialog udpDialog;
    private ArrayList<Integer> codes = new ArrayList<>();
    private int thousand = 0;
    private int hundred = 0;
    private int decimal = 0;
    private int unit = 0;
    private SyncListener mListener;

    public static SyncDialog newInstance(int messageId, ArrayList<Integer> codes, int oldCode) {
        SyncDialog myDialogFragment = new SyncDialog();
        Bundle params = new Bundle();
        params.putIntegerArrayList(INPUT_CODES, codes);
        params.putInt(MESSAGE_ID_PARAM, messageId);
        params.putInt(OLD_CODE, oldCode);
        myDialogFragment.setArguments(params);
        return myDialogFragment;
    }

    public void changeState (ArrayList<Integer> codes) {
        this.codes = codes;
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (udpDialog != null) {
                    udpDialog.findViewById(R.id.picker_view).setVisibility(View.VISIBLE);
                    udpDialog.findViewById(R.id.progress_view).setVisibility(View.GONE);
                }
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());
        View contentView = inflater.inflate(R.layout.code_dlg, null);

        int oldCode = getArguments().getInt(OLD_CODE, 0);
        if (oldCode != 0) {
            thousand = oldCode /1000;
            hundred = oldCode /100%10;
            decimal = oldCode /10%100;
            unit = oldCode %1000;
        }
        codes = getArguments().getIntegerArrayList(INPUT_CODES);

        String message = getResources().getString(R.string.code_dlg_message);
        String title = getResources().getString(R.string.code_dlg_title);

        TextView titleTextView = contentView.findViewById(R.id.title);
        titleTextView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        titleTextView.setText(title);

        final TextView messageTextView = contentView.findViewById(R.id.tv_message);
        titleTextView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        messageTextView.setText(message);

        ImageButton closeButton = contentView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);
        closeButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSyncCancel();
            }
            udpDialog.dismiss();
        });

        NumberPicker npThousand = contentView.findViewById(R.id.picker_thousand);
        npThousand.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        npThousand.setOnValueChangedListener((picker, oldVal, newVal) -> thousand = newVal);
        npThousand.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        npThousand.setValue(thousand);

        NumberPicker npHundred = contentView.findViewById(R.id.picker_hundred);
        npHundred.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        npHundred.setOnValueChangedListener((picker, oldVal, newVal) -> hundred = newVal);
        npHundred.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        npHundred.setValue(hundred);

        NumberPicker npDecimal = contentView.findViewById(R.id.picker_decimal);
        npDecimal.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        npDecimal.setOnValueChangedListener((picker, oldVal, newVal) -> decimal = newVal);
        npDecimal.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        npDecimal.setValue(decimal);

        NumberPicker npUnit = contentView.findViewById(R.id.picker_unit);
        npUnit.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        npUnit.setOnValueChangedListener((picker, oldVal, newVal) -> unit = newVal);
        npUnit.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        npUnit.setValue(unit);

        TextView yesBtn = contentView.findViewById(R.id.btn_yes);
        yesBtn.setOnClickListener(view -> {
            Integer code = thousand * 1000 + hundred * 100 + decimal * 10 + unit;
            if (codes.contains(code)) {
                if (mListener != null) {
                    mListener.onSyncCodeSet(code);
                }
            } else {
                messageTextView.setText(getResources().getString(R.string.code_dlg_error));
                messageTextView.setTextColor(getResources().getColor(R.color.redCard));
            }
        });

        TextView noBtn = contentView.findViewById(R.id.btn_no);
        noBtn.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onSyncCancel();
            }
            udpDialog.dismiss();
        });

        TextView cancelBtn = contentView.findViewById(R.id.btn_cancel_sync);
        cancelBtn.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onSyncCancel();
            }
            udpDialog.dismiss();
        });

        udpDialog = builder.setView(contentView).show();
        udpDialog.setCanceledOnTouchOutside(false);
        return udpDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SyncListener) {
            mListener = (SyncListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public interface SyncListener {
        void onSyncCodeSet(int code);
//        void onSyncDecline();
        void onSyncCancel();
    }
}
