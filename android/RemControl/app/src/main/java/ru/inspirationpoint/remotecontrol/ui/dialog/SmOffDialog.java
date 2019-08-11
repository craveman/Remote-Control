package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.jetbrains.annotations.NotNull;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;

public class SmOffDialog extends DialogFragment {

    private AlertDialog smDialog;

    public static SmOffDialog newInstance() {
        return new SmOffDialog();
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.support.v7.app.AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new android.support.v7.app.AlertDialog.Builder(getActivity());

        smDialog = builder.setTitle(getResources().getString(R.string.warning))
                .setMessage(getResources().getString(R.string.sm_off))
                .show();
        smDialog.setCanceledOnTouchOutside(false);
        return smDialog;
    }

    public void close() {
        smDialog.dismiss();
    }
}
