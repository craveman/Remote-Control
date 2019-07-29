package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;

public class FightFinishAskDialog extends DialogFragment {

    private FightFinisAskListener listener;
    private boolean withSEMI;

    public static void show(FragmentActivity fragmentActivity, boolean withSEMI) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev = manager.findFragmentByTag("FightFinishAskDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        FightFinishAskDialog myDialogFragment = new FightFinishAskDialog();
        Bundle params = new Bundle();
        params.putBoolean("SEMI", withSEMI);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(ft, "FightFinishAskDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        withSEMI = getArguments().getBoolean("SEMI", false);
        View contentView = inflater.inflate(R.layout.dlg_fight_ask_finish, null);
        contentView.findViewById(R.id.fight_ask_finish).setOnClickListener(v -> {
            listener.ok();
            dismiss();
        });
        contentView.findViewById(R.id.semi_title).setVisibility(withSEMI ? View.VISIBLE : View.GONE);
        contentView.findViewById(R.id.fight_ask_cancel).setOnClickListener(v -> {
            dismiss();
        });
        return builder.setView(contentView).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FightFinisAskListener) {
            listener = (FightFinisAskListener) context;
        }
    }



    public interface FightFinisAskListener{
        void ok();
    }
}
