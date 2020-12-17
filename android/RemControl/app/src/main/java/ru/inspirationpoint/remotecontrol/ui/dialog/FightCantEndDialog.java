package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.inspirationpoint.remotecontrol.R;

public class FightCantEndDialog extends DialogFragment {

    private boolean withSEMI;

    public static void show(FragmentActivity fragmentActivity, boolean withSEMI) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        FightCantEndDialog myDialogFragment = new FightCantEndDialog();
        Bundle params = new Bundle();
        params.putBoolean("SEMI", withSEMI);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "CantEndDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        withSEMI = getArguments().getBoolean("SEMI", false);
        View contentView = inflater.inflate(R.layout.dlg_cant_end_fight, null);
        TextView ok = contentView.findViewById(R.id.fight_cant_end_ok);
        contentView.findViewById(R.id.semi_title).setVisibility(withSEMI ? View.VISIBLE : View.GONE);
        ok.setOnClickListener(v -> dismiss());
        return builder.setView(contentView).show();
    }
}
