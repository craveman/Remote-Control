package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;

public class WiFiPassDialog extends DialogFragment {
    private final static String TITLE_PARAM = "title";
    private final static String MESSAGE_PARAM = "message";

    private WiFiPassListener listener;

    public static WiFiPassDialog show(FragmentActivity fragmentActivity, String title, String message) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        WiFiPassDialog myDialogFragment = new WiFiPassDialog();
        Bundle params = new Bundle();
        params.putString(TITLE_PARAM, title);
        params.putString(MESSAGE_PARAM, message);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "PassType");
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
        String title = getArguments().getString(TITLE_PARAM);
        String message = getArguments().getString(MESSAGE_PARAM);
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());
        View contentView = inflater.inflate(R.layout.dlg_wifi_pass, null);

        TextView messageTitle = contentView.findViewById(R.id.pass_title);
        messageTitle.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        messageTitle.setText(title);
        TextView messageText = contentView.findViewById(R.id.pass_content);
        messageText.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        messageText.setText(message);
        EditText passText = contentView.findViewById(R.id.pass_edit);
        passText.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        TextView btnNo = contentView.findViewById(R.id.btn_no);
        btnNo.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btnNo.setOnClickListener(view -> {
            if (listener != null) {
                listener.onPassDlgDismiss();
            }
            dismiss();
        });
        TextView btnOk = contentView.findViewById(R.id.btn_yes);
        btnOk.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btnOk.setOnClickListener(view -> {
            if (listener != null) {
                listener.onPassConfirmed(passText.getText().toString());
            }
            dismiss();
        });

        return builder.setView(contentView).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof WiFiPassListener) {
            listener = (WiFiPassListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }

    public interface WiFiPassListener {
        void onPassDlgDismiss ();
        void onPassConfirmed (String pass);
    }
}
