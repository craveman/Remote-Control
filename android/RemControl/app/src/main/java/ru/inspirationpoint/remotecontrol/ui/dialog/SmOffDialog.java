package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

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
        androidx.appcompat.app.AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new androidx.appcompat.app.AlertDialog.Builder(getActivity());

        smDialog = builder.setTitle(getResources().getString(R.string.warning))
                .setMessage(getResources().getString(R.string.sm_off))
                .show();
        smDialog.setCanceledOnTouchOutside(false);
        return smDialog;
    }

    public void close() {
        if (smDialog != null) {
                smDialog.dismiss();
        }
    }
}
