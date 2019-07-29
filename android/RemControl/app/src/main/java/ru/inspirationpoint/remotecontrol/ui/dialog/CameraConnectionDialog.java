package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.IS_BY_FLAGS_STOP;

public class CameraConnectionDialog extends DialogFragment {

    private AlertDialog cameraDialog;
    boolean isRepeaterAvailable;
    private String name;
    private View btnTargetChange;
    private boolean isTargetSM = true;
    private CameraConnectionListener listener;
    private boolean isSaveNeeded = true;
    private TextView tvTarget;
    private TextView title;

    public static CameraConnectionDialog newInstance(String name, boolean isRepeaterAvailable) {
        CameraConnectionDialog dialog = new CameraConnectionDialog();
        Bundle params = new Bundle();
        params.putString("name", name);
        params.putBoolean("isRep", isRepeaterAvailable);
        dialog.setArguments(params);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());
        View contentView = inflater.inflate(R.layout.camera_connection_dlg, null);
        name = getArguments().getString("name");
        isRepeaterAvailable = getArguments().getBoolean("isRep");
        title = contentView.findViewById(R.id.tv_camera);
        tvTarget = contentView.findViewById(R.id.tv_target);
        SwitchCompat saveSwitch = contentView.findViewById(R.id.store_switch);
        btnTargetChange = contentView.findViewById(R.id.btn_target_change);
        saveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isSaveNeeded = isChecked);
        saveSwitch.setChecked(isSaveNeeded);
        SwitchCompat eventSwitch = contentView.findViewById(R.id.event_switch);
        eventSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingsManager.setValue(IS_BY_FLAGS_STOP, isChecked);
            ((TextView)contentView.findViewById(R.id.tv_by_flags))
                    .setTextColor(isChecked?getActivity().getResources().getColor(R.color.textColorSecondary) :
                            (SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                            getActivity().getResources().getColor(R.color.textColorPrimary) :
                                    getActivity().getResources().getColor(R.color.colorAccent)));
            ((TextView)contentView.findViewById(R.id.tv_by_time))
                    .setTextColor(!isChecked?getActivity().getResources().getColor(R.color.textColorSecondary) :
                            (SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                                    getActivity().getResources().getColor(R.color.textColorPrimary) :
                                    getActivity().getResources().getColor(R.color.colorAccent)));
        });
        eventSwitch.setChecked(SettingsManager.getValue(IS_BY_FLAGS_STOP, false));
        ((TextView)contentView.findViewById(R.id.tv_by_flags))
                .setTextColor(SettingsManager.getValue(IS_BY_FLAGS_STOP, false)?
                        getActivity().getResources().getColor(R.color.textColorSecondary) :
                        (SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                                getActivity().getResources().getColor(R.color.textColorPrimary) :
                                getActivity().getResources().getColor(R.color.colorAccent)));
        ((TextView)contentView.findViewById(R.id.tv_by_time))
                .setTextColor(!SettingsManager.getValue(IS_BY_FLAGS_STOP, false)?
                        getActivity().getResources().getColor(R.color.textColorSecondary) :
                        (SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                                getActivity().getResources().getColor(R.color.textColorPrimary) :
                                getActivity().getResources().getColor(R.color.colorAccent)));
        title.setText(getResources().getString(R.string.selected_cam, name));
        View btnDone = contentView.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(v -> {
            listener.onReady(isTargetSM, isSaveNeeded);
            dismiss();
        });
        if (isRepeaterAvailable) {
            btnTargetChange.setClickable(true);
            btnTargetChange.setOnClickListener(v -> {
                isTargetSM = !isTargetSM;
                tvTarget.setText(isTargetSM ? "SM-02" : getString(R.string.repeater));
            });
        } else  btnTargetChange.setClickable(false);
        tvTarget.setText("SM-02");
        cameraDialog = builder.setView(contentView).show();
        cameraDialog.setCanceledOnTouchOutside(false);
        return cameraDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CameraConnectionListener) {
            listener = (CameraConnectionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }

    public interface CameraConnectionListener{
        void onReady(boolean isTargetSM, boolean isSaveNeeded);
    }
}
