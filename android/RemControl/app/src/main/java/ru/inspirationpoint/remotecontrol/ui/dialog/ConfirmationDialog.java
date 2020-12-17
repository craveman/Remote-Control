package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;

public class ConfirmationDialog extends DialogFragment {

    private final static String MESSAGE_ID_PARAM = "message_id";
    private final static String TITLE_PARAM = "title";
    private final static String MESSAGE_PARAM = "message";

    private Listener mListener;
    private int mMessageId;

    public static ConfirmationDialog show(FragmentActivity fragmentActivity, int messageId, String title, String message) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        ConfirmationDialog myDialogFragment = new ConfirmationDialog();
        Bundle params = new Bundle();
        params.putInt(MESSAGE_ID_PARAM, messageId);
        params.putString(TITLE_PARAM, title);
        params.putString(MESSAGE_PARAM, message);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "MessageType");
        return myDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMessageId = getArguments().getInt(MESSAGE_ID_PARAM);
        String title = getArguments().getString(TITLE_PARAM);
        final String message = getArguments().getString(MESSAGE_PARAM);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate(R.layout.confirmation_dlg_btns, null);
        TextView tv_yes = contentView.findViewById(R.id.btn_yes);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onConfirmed(mMessageId);
                }
                dismiss();
            }
        });

        TextView tv_no = contentView.findViewById(R.id.btn_no);
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ID IN DLG", mMessageId + "");
//                if (mMessageId == FightActionActivity.PHRASE_SELECT) {
//                    mListener.onConfirmed(11);
//                }
                if (mListener != null) {
                    mListener.onConfirmDeclined(mMessageId);
                }
                dismiss();
            }
        });

        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());
        TextView myMsg = new TextView(getContext());
        myMsg.setText(title);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        myMsg.setTextSize(24);
        myMsg.setTextColor(Color.WHITE);
        myMsg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Dialog d = builder.setCustomTitle(myMsg)
                .setMessage(mMessageId == 2222 ? (Html.fromHtml(getResources().getString(R.string.unblock) + "<br><br>" +
                        "<font size=\"10\" color=\"#0082FC\">" + message + "</font> <br><br> "))
                        : (mMessageId == 1234 ? Html.fromHtml(getResources().getString(R.string.delete) + "<br><br>" +
                        "<font size=\"10\" color=\"#0082FC\">" + message + "</font> <br><br> "
                        + getResources().getString(R.string.delete_user_confirm)) : "\n" + message))
                .setView(contentView)
                .show();
        TextView tvMess = d.getWindow().findViewById(android.R.id.message);
        Button btn1 = d.getWindow().findViewById(android.R.id.button1);
        Button btn2 = d.getWindow().findViewById(android.R.id.button2);
        Button btn3 = d.getWindow().findViewById(android.R.id.button3);

        tvMess.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        tvMess.setSingleLine(false);
        tvMess.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvMess.setMaxLines(5);
        tvMess.setTextSize(22);
        btn1.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btn1.setTextColor(getActivity().getResources().getColor(R.color.textColorRed));
        btn1.setTextSize(26);
        btn2.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btn2.setTextColor(getActivity().getResources().getColor(R.color.textColorSecondary));
        btn2.setGravity(Gravity.START);
        btn2.setTextSize(26);
        btn2.requestLayout();
        btn3.setTypeface(InspirationDayApplication.getCustomFontTypeface());

//        builder.setTitle(title)
//                .setMessage(message)
//                .setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (mListener != null) {
//                            mListener.onConfirmed(mMessageId);
//                        }
//                    }
//                })
//                .setPositiveButton(getString(R.string.no), null);
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


    public interface Listener {
        void onConfirmed(int messageId);
        void onConfirmDeclined(int messageId);
    }
}
