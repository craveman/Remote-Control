package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class MessageDialog extends DialogFragment {

    private final static String MESSAGE_ID_PARAM = "message_id";
    private final static String TITLE_PARAM = "title";
    private final static String MESSAGE_PARAM = "message";

    private Listener mListener;
    private int mMessageId;

    public static MessageDialog show(FragmentActivity fragmentActivity, int messageId, String title, String message) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        MessageDialog myDialogFragment = new MessageDialog();
        Bundle params = new Bundle();
        params.putInt(MESSAGE_ID_PARAM, messageId);
        params.putString(TITLE_PARAM, title);
        params.putString(MESSAGE_PARAM, message);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "MessageType");
        return myDialogFragment;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d("Dialog", "Exception", e);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMessageId = getArguments().getInt(MESSAGE_ID_PARAM);
        String title = getArguments().getString(TITLE_PARAM);
        String message = getArguments().getString(MESSAGE_PARAM);

        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        Dialog d = builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onMessageDialogDismissed(mMessageId);
                        }
                        dismiss();
                    }
                }).show();

        TextView tvMess = d.getWindow().findViewById(android.R.id.message);
        Button btn1 = d.getWindow().findViewById(android.R.id.button1);
        Button btn2 = d.getWindow().findViewById(android.R.id.button2);
        Button btn3 = d.getWindow().findViewById(android.R.id.button3);

        tvMess.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        tvMess.setSingleLine(false);
        tvMess.setLines(4);
        btn1.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btn1.setTextColor(getActivity().getResources().getColor(R.color.textColorSecondary));
        btn2.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btn3.setTypeface(InspirationDayApplication.getCustomFontTypeface());

//        builder.setTitle(title)
//                .setMessage(message)
//                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (mListener != null) {
//                            mListener.onMessageDialogDismissed(mMessageId);
//                        }
//                    }
//                });
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
        void onMessageDialogDismissed(int messageId);
    }
}
