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
import android.widget.TextView;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;

public class PhraseConfirmDialog extends DialogFragment {

    private final static String MESSAGE_ID_PARAM = "message_id";
    private final static String TITLE_PARAM = "title";
    private final static String MESSAGE_PARAM = "message";

    private Listener mListener;
    private int mMessageId;

    public static PhraseConfirmDialog show(FragmentActivity fragmentActivity, int messageId, String title, String message) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        PhraseConfirmDialog myDialogFragment = new PhraseConfirmDialog();
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
        final String message = getArguments().getString(MESSAGE_PARAM);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate(R.layout.phrase_confirm_dlg, null);
        View btnConfirm = contentView.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onPhraseConfirmed(mMessageId);
                }
                dismiss();
            }
        });

        View btnCancel = contentView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ID IN DLG", mMessageId + "");
//                if (mMessageId == FightActionActivity.PHRASE_SELECT) {
//                    mListener.onPhraseConfirmed(11);
//                }
                dismiss();
            }
        });

        TextView phraseTv = contentView.findViewById(R.id.action_tv);
        phraseTv.setText(message);

        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

//        TextView tvMess = (TextView) d.getWindow().findViewById(android.R.id.message);
//        Button btn1 = (Button) d.getWindow().findViewById(android.R.id.button1);
//        Button btn2 = (Button) d.getWindow().findViewById(android.R.id.button2);
//        Button btn3 = (Button) d.getWindow().findViewById(android.R.id.button3);
//
//        tvMess.setTypeface(InspirationDayApplication.getCustomFontTypeface());
//        btn1.setTypeface(InspirationDayApplication.getCustomFontTypeface());
//        btn1.setTextColor(getActivity().getResources().getColor(R.color.textColorRed));
//        btn2.setTypeface(InspirationDayApplication.getCustomFontTypeface());
//        btn2.setTextColor(getActivity().getResources().getColor(R.color.textColorSecondary));
//        btn2.setGravity(Gravity.START);
//        btn2.requestLayout();
//        btn3.setTypeface(InspirationDayApplication.getCustomFontTypeface());

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
        return builder
                .setView(contentView)
                .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.onPhraseConfirmed(11);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public interface Listener {
        void onPhraseConfirmed(int messageId);
    }
}
