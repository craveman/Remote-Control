package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData;

public class PriorityDialog extends DialogFragment {

    private final static String LEFT_FIGHTER_NAME_PARAM = "left_fighter";
    private final static String RIGHT_FIGHTER_NAME_PARAM = "right_fighter";
    private final static String IS_LEFT_SELECTED_PARAM = "is_left_selected";
    private Listener mListener;
    private FightActionData.Fighter mPriorityFighter;

    public static void show(FragmentActivity fragmentActivity, String leftFighterName, String rightFighterName,
                            FightActionData.Fighter priorityFighter) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        PriorityDialog myDialogFragment = new PriorityDialog();
        Bundle params = new Bundle();
        params.putString(LEFT_FIGHTER_NAME_PARAM, leftFighterName);
        params.putString(RIGHT_FIGHTER_NAME_PARAM, rightFighterName);
        params.putInt(IS_LEFT_SELECTED_PARAM, priorityFighter.ordinal());
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "PriorityDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        View contentView = inflater.inflate(R.layout.dlg_title_layout, null);

        TextView titleTextView = contentView.findViewById(R.id.title);
        titleTextView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        titleTextView.setText(R.string.priority_title);

        ImageButton closeButton = contentView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });

        String leftFighterName = getArguments().getString(LEFT_FIGHTER_NAME_PARAM);
        String rightFighterName = getArguments().getString(RIGHT_FIGHTER_NAME_PARAM);
        mPriorityFighter = FightActionData.Fighter.values()[getArguments().getInt(IS_LEFT_SELECTED_PARAM)];
        if (mPriorityFighter == FightActionData.Fighter.None) {
            mPriorityFighter = new Random().nextBoolean() ? FightActionData.Fighter.Left : FightActionData.Fighter.Right;
        }

//        builder.setCustomTitle(contentView)
//                .setItems(new String[]{leftFighterName, rightFighterName}, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mPriorityFighter = which == 0 ? FightActionData.Fighter.Left : FightActionData.Fighter.Right;
//                        if (mListener != null) {
//                            mListener.onPrioritySet(mPriorityFighter);
//                        }
//                        dismiss();
//                    }
//                });

        Dialog d = builder.setCustomTitle(contentView)
                .setItems(new String[]{leftFighterName, rightFighterName}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPriorityFighter = which == 0 ? FightActionData.Fighter.Left : FightActionData.Fighter.Right;
                        if (mListener != null) {
                            mListener.onPrioritySet(mPriorityFighter);
                        }
                        dismiss();
                    }
                }).show();

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
        void onPrioritySet(FightActionData.Fighter fighter);
    }
}
