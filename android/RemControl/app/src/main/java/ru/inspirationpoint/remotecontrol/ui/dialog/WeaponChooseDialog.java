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
import android.widget.ImageButton;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class WeaponChooseDialog extends DialogFragment {

    private Listener mListener;

    public static WeaponChooseDialog show(FragmentActivity fragmentActivity) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        WeaponChooseDialog dialog = new WeaponChooseDialog();
        dialog.show(manager, "WeaponType");
        return dialog;
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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        View titleView = inflater.inflate(R.layout.dlg_title_layout, null);

        TextView titleTextView = titleView.findViewById(R.id.title);
        titleTextView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        titleTextView.setText(R.string.weapon_type);

        ImageButton closeButton = titleView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);

        View contentView = inflater.inflate(R.layout.weapon_dlg, null);
        TextView epee = contentView.findViewById(R.id.weapon_epee);
        TextView rapier = contentView.findViewById(R.id.weapon_rapier);
        TextView saber = contentView.findViewById(R.id.weapon_saber);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.weapon_epee:
                        mListener.onWeaponChoosed(getResources().getString(R.string.type_epee));
                        dismiss();
                        break;
                    case R.id.weapon_saber:
                        mListener.onWeaponChoosed(getResources().getString(R.string.type_saber));
                        dismiss();
                        break;
                    case R.id.weapon_rapier:
                        mListener.onWeaponChoosed(getResources().getString(R.string.type_rapier));
                        dismiss();
                        break;
                }
            }
        };
        epee.setOnClickListener(listener);
        rapier.setOnClickListener(listener);
        saber.setOnClickListener(listener);

        Dialog d = builder.setCustomTitle(titleView)
                .setView(contentView).show();
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
        void onWeaponChoosed(String weaponType);
    }
}
