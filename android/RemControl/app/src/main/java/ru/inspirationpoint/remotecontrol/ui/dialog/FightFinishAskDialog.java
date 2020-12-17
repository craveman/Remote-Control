package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import ru.inspirationpoint.remotecontrol.R;

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
