package ru.inspirationpoint.inspirationrc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class ChangeScoresDialog extends DialogFragment implements NumberPicker.OnValueChangeListener{

    private final static String LEFT_SCORE_PARAM = "left_score";
    private final static String RIGHT_SCORE_PARAM = "right_score";
    private final static String LEFT_FIGHTER_NAME_PARAM = "left_fighter";
    private final static String RIGHT_FIGHTER_NAME_PARAM = "right_fighter";
    private Listener mListener;
    private int mLeftScore;
    private int mRightScore;

    public static void show(FragmentActivity fragmentActivity, String leftFighterName, String rightFighterName, int leftScore, int rightScore) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        ChangeScoresDialog myDialogFragment = new ChangeScoresDialog();
        Bundle params = new Bundle();
        params.putString(LEFT_FIGHTER_NAME_PARAM, leftFighterName);
        params.putString(RIGHT_FIGHTER_NAME_PARAM, rightFighterName);
        params.putInt(LEFT_SCORE_PARAM, leftScore);
        params.putInt(RIGHT_SCORE_PARAM, rightScore);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "ChangeScoresDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String leftFighterName = getArguments().getString(LEFT_FIGHTER_NAME_PARAM);
        String rightFighterName = getArguments().getString(RIGHT_FIGHTER_NAME_PARAM);
        mLeftScore = getArguments().getInt(LEFT_SCORE_PARAM);
        mRightScore = getArguments().getInt(RIGHT_SCORE_PARAM);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        View contentView = inflater.inflate(R.layout.change_scores_dlg, null);

        TextView titleTextView = (TextView) contentView.findViewById(R.id.title);
        titleTextView.setTextColor(!SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        titleTextView.setText(R.string.change_score);

        ImageButton closeButton = (ImageButton) contentView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);


        TextView textView = (TextView) contentView.findViewById(R.id.left_fighter_name);
        textView.setText(leftFighterName);
        textView = (TextView) contentView.findViewById(R.id.right_fighter_name);
        textView.setText(rightFighterName);

        NumberPicker np_left = (NumberPicker) contentView.findViewById(R.id.np_left_score);
        NumberPicker np_right = (NumberPicker) contentView.findViewById(R.id.np_right_score);

        np_left.setTextColor(!SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        np_left.setMinValue(0);
        np_left.setMaxValue(99);
        np_left.setValue(mLeftScore);
        np_left.setWrapSelectorWheel(true);
        np_left.setOnValueChangedListener(this);
        np_left.setTypeface(InspirationDayApplication.getCustomFontTypeface());

        np_right.setTextColor(!SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        np_right.setMinValue(0);
        np_right.setMaxValue(99);
        np_right.setValue(mRightScore);
        np_right.setWrapSelectorWheel(true);
        np_right.setOnValueChangedListener(this);
        np_right.setTypeface(InspirationDayApplication.getCustomFontTypeface());

        Dialog d = builder.setView(contentView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onScoreChanged(mLeftScore, mRightScore);
                        }
                    }
                }).show();

        Button btn1 = (Button) d.getWindow().findViewById(android.R.id.button1);

        btn1.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btn1.setTextColor(getActivity().getResources().getColor(R.color.textColorSecondary));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn1.getLayoutParams();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        btn1.setLayoutParams(params);

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

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        if (numberPicker.getId() == R.id.np_left_score) {
            mLeftScore = i1;
        } else if (numberPicker.getId() == R.id.np_right_score) {
            mRightScore = i1;
        }
    }

    public interface Listener {
        void onScoreChanged(int leftScoreDelta, int rightScoreDelta);
    }
}
