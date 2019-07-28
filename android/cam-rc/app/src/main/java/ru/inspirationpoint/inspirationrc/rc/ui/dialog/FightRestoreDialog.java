package ru.inspirationpoint.inspirationrc.rc.ui.dialog;

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
import android.widget.TextView;

import java.util.Locale;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FullFightInfo;

public class FightRestoreDialog extends DialogFragment {

    private final static String FIGHT_TO_RESTORE = "FTR";

    private FullFightInfo info;
    private RestoreListener listener;

    public static FightRestoreDialog show(FragmentActivity activity, FullFightInfo info) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FightRestoreDialog dialog = new FightRestoreDialog();
        Bundle params = new Bundle();
        params.putSerializable(FIGHT_TO_RESTORE, info);
        dialog.setArguments(params);
        dialog.show(manager, "MessageType");
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d("Restore Dialog", "Exception", e);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        info = (FullFightInfo) getArguments().getSerializable(FIGHT_TO_RESTORE);
        Log.wtf("TO RESTORE", info.getFightData().getOwner() + "|" +
                info.getFightData().getmCurrentPeriod() + "|" +
                info.getFightData().getLeftFighter().getScore() + "|" +
                info.getFightData().getRightFighter().getScore());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = inflater.inflate(R.layout.dlg_fight_restore, null);
        ((TextView) contentView.findViewById(R.id.restore_data_names))
                .setText(String.format("%s - %s", info.getFightData().getLeftFighter().getName(),
                        info.getFightData().getRightFighter().getName()));
        ((TextView) contentView.findViewById(R.id.restore_data_score))
                .setText(String.format(Locale.getDefault(), "%d - %d", info.getFightData().getLeftFighter().getScore(),
                        info.getFightData().getRightFighter().getScore()));
        contentView.findViewById(R.id.unfinished_accept).setOnClickListener(v -> {
            listener.onAccept(info);
            dismiss();
        });
        contentView.findViewById(R.id.unfinished_cancel).setOnClickListener(v -> {
            listener.onDecline();
            dismiss();
        });
        return builder.setView(contentView).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof RestoreListener) {
            listener = (RestoreListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }

    public interface RestoreListener {
        void onAccept(FullFightInfo restoredInfo);

        void onDecline();
    }
}
