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

import ru.inspirationpoint.remotecontrol.R;

public class FightFinishedDialog extends DialogFragment {

    private FightFinishedListener listener;

    public static void show(FragmentActivity fragmentActivity) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev = manager.findFragmentByTag("FightFinishedDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        FightFinishedDialog myDialogFragment = new FightFinishedDialog();
        myDialogFragment.show(manager, "FightFinishedDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = inflater.inflate(R.layout.dlg_fight_finished, null);
        TextView semiExit = contentView.findViewById(R.id.fight_end_exit_semi);
        semiExit.setOnClickListener(v -> {
            listener.semiExit();
            dismiss();
        });
        contentView.findViewById(R.id.fight_end_prev).setVisibility(View.GONE);
//                .setOnClickListener(v -> {
//            listener.prev();
//            dismiss();
//        });
        contentView.findViewById(R.id.fight_end_next).setVisibility(View.GONE);
//                .setOnClickListener(v -> {
//            listener.next();
//            dismiss();
//        });
        return builder.setView(contentView).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FightFinishedListener) {
            listener = (FightFinishedListener) context;
        }
    }

    public interface FightFinishedListener{
        void prev();
        void next();
        void semiExit();
    }
}
