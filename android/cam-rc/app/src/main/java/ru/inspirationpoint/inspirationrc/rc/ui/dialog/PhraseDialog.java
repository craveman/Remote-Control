package ru.inspirationpoint.inspirationrc.rc.ui.dialog;

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
import android.widget.ImageButton;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.helpers.LocaleHelper;

import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.phrasesEN;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.phrasesRU;

public class PhraseDialog extends DialogFragment {

    private Listener mListener;
    private FragmentActivity activity;
    private boolean isLeft;

    public static void show(FragmentActivity fragmentActivity, boolean isLeft) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        PhraseDialog myDialogFragment = new PhraseDialog();
        Bundle params = new Bundle();
        params.putBoolean("isLeft", isLeft);
        myDialogFragment.setArguments(params);
        myDialogFragment.activity = fragmentActivity;
        myDialogFragment.show(manager, "PhraseDialog");
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
        titleTextView.setText(R.string.choose_event);
        isLeft = getArguments().getBoolean("isLeft");
        ImageButton closeButton = contentView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);

        Dialog d = builder.setCustomTitle(contentView)
                .setItems(LocaleHelper.getLanguage(getContext()).equals("ru") ? phrasesRU : phrasesEN, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onEventSelected(which, isLeft, false);
                        }
                    }
                }).show();

        return d;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
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
        void onEventSelected(int position, boolean isLeft, boolean cancelled);
    }
}
