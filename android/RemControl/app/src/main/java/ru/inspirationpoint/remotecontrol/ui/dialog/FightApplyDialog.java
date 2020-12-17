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
import android.widget.TextView;

import ru.inspirationpoint.remotecontrol.R;

public class FightApplyDialog extends DialogFragment {

    private ApplyListener listener;

    public static void show(FragmentActivity fragmentActivity) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev = manager.findFragmentByTag("ApplyDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        FightApplyDialog myDialogFragment = new FightApplyDialog();
        myDialogFragment.show(ft, "ApplyDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = inflater.inflate(R.layout.dlg_fight_apply, null);
        TextView ok = contentView.findViewById(R.id.fight_apply_ok);
        ok.setOnClickListener(v -> {
            listener.apply();
            dismiss();
        });
        contentView.findViewById(R.id.fight_apply_prev).setVisibility(View.GONE);
//                .setOnClickListener(v -> {
//            listener.askPrev();
//            dismiss();
//        });
        contentView.findViewById(R.id.fight_apply_next).setVisibility(View.GONE);
//                .setOnClickListener(v -> {
//            listener.askNext();
//            dismiss();
//        });
        AlertDialog d = builder.setView(contentView).show();
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ApplyListener) {
            listener = (ApplyListener) context;
        }
    }

    public interface ApplyListener {
        void apply();
        void askPrev();
        void askNext();
    }
}
