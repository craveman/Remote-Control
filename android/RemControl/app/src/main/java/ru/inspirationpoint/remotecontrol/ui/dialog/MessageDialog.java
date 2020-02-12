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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;

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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        View contentView = inflater.inflate(R.layout.dlg_message, null);

        TextView messageTitle = contentView.findViewById(R.id.message_title);
        messageTitle.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        messageTitle.setText(title);
        TextView messageText = contentView.findViewById(R.id.message_content);
        messageText.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        messageText.setText(message);
        TextView btnOk = contentView.findViewById(R.id.msg_confirm);
        btnOk.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btnOk.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onMessageDialogDismissed(mMessageId);
            }
            dismiss();
        });

        return builder.setView(contentView).show();
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
