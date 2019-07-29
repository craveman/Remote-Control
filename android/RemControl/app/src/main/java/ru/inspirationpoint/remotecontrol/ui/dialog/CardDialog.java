package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.CardStatus;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class CardDialog extends DialogFragment {

    private final static String LEFT_FIGHTER_NAME_PARAM = "left_fighter";
    private final static String RIGHT_FIGHTER_NAME_PARAM = "right_fighter";
    private final static String LEFT_CARD_PARAM = "is_left_selected";
    private final static String RIGHT_CARD_PARAM = "is_right_selected";
    private Listener mListener;
    private CardStatus mLeftCardStatus;
    private CardStatus mRightCardStatus;

    public static void show(FragmentActivity fragmentActivity, String leftFighterName, String rightFighterName, CardStatus leftCard, CardStatus rightCard) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        CardDialog myDialogFragment = new CardDialog();
        Bundle params = new Bundle();
        params.putString(LEFT_FIGHTER_NAME_PARAM, leftFighterName);
        params.putString(RIGHT_FIGHTER_NAME_PARAM, rightFighterName);
        params.putInt(LEFT_CARD_PARAM, leftCard.ordinal());
        params.putInt(RIGHT_CARD_PARAM, rightCard.ordinal());
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "CardDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String leftFighterName = getArguments().getString(LEFT_FIGHTER_NAME_PARAM);
        String rightFighterName = getArguments().getString(RIGHT_FIGHTER_NAME_PARAM);
        mLeftCardStatus = CardStatus.values()[getArguments().getInt(LEFT_CARD_PARAM)];
        mRightCardStatus = CardStatus.values()[getArguments().getInt(RIGHT_CARD_PARAM)];

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        View contentView = inflater.inflate(R.layout.card_dlg, null);

        TextView titleTextView = contentView.findViewById(R.id.title);
        titleTextView.setText(R.string.card_title);

        ImageButton closeButton = contentView.findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView text = contentView.findViewById(R.id.left_fighter_name);
        text.setText(leftFighterName);
        text = contentView.findViewById(R.id.right_fighter_name);
        text.setText(rightFighterName);

        View leftCard = contentView.findViewById(R.id.left_fighter_card);
        leftCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCardSet(nextStatus(mLeftCardStatus), mRightCardStatus);
                }
                dismiss();
            }
        });

        switch (mLeftCardStatus) {
            case CardStatus_None:
                leftCard.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.yellow_card_button_shape));
                break;

            case CardStatus_Yellow:
            case CardStatus_Red:
                leftCard.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.red_card_button_shape));
                break;
        }

        View rightCard = contentView.findViewById(R.id.right_fighter_card);
        rightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCardSet(mLeftCardStatus, nextStatus(mRightCardStatus));
                }
                dismiss();
            }
        });

        switch (mRightCardStatus) {
            case CardStatus_None:
                rightCard.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.yellow_card_button_shape));
                break;

            case CardStatus_Yellow:
            case CardStatus_Red:
                rightCard.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.red_card_button_shape));
                break;
        }

        builder.setView(contentView);

        return builder.create();
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

    private CardStatus nextStatus(CardStatus cardStatus) {
        switch (cardStatus) {
            case CardStatus_None:
                return CardStatus.CardStatus_Yellow;

            case CardStatus_Yellow:
                return CardStatus.CardStatus_Red;
        }
        return CardStatus.CardStatus_Yellow;
    }

    public interface Listener {
        void onCardSet(CardStatus leftFighter, CardStatus rightFighter);
    }
}
