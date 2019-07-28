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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.rc.ui.adapter.FightersAutoCompleteAdapter;
import ru.inspirationpoint.inspirationrc.rc.ui.view.FightersAutoCompleteTextView;
import server.schemas.responses.ListUser;

public class FighterSearchDialog extends DialogFragment {

    private final static String MESSAGE_ID_PARAM = "message_id";
    private final static String MESSAGE_FIELD = "message";
    private final static String MESSAGE_TITLE = "title";

    private Listener mListener;
    private int mMessageId;
    private ListUser mNewFriendUser;
    private FightersAutoCompleteTextView mNewFriend;

    public static void show(FragmentActivity fragmentActivity, int messageId, String title, String message) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        FighterSearchDialog myDialogFragment = new FighterSearchDialog();
        Bundle params = new Bundle();
        params.putString(MESSAGE_FIELD, message);
        params.putInt(MESSAGE_ID_PARAM, messageId);
        params.putString(MESSAGE_TITLE, title);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "FighterSearchDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        String message = getArguments().getString(MESSAGE_FIELD);
        String title = getArguments().getString(MESSAGE_TITLE);
        if (message == null) message = "";

        View contentView = inflater.inflate(R.layout.fighter_search_dlg, null);

        TextView titleTextView = contentView.findViewById(R.id.title);
        titleTextView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        titleTextView.setText(title);

        ImageButton closeButton = contentView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);

        TextView tvMessage = contentView.findViewById(R.id.message);
        tvMessage.setText(message);
        tvMessage.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));

        FightersAutoCompleteAdapter mNewFriendAdapter = new FightersAutoCompleteAdapter(getActivity(), false);
        mNewFriend = contentView.findViewById(R.id.new_friend);
        mNewFriend.setThreshold(1);
        mNewFriend.setAdapter(mNewFriendAdapter);
        mNewFriend.setLoadingIndicator(contentView.findViewById(R.id.new_friend_progress_bar));
        mNewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mNewFriendUser = (ListUser) adapterView.getItemAtPosition(position);
                mNewFriend.setText(mNewFriendUser.name);
            }
        });

        TextView tv_yes = contentView.findViewById(R.id.btn_yes);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNewFriendUser == null) {
                    mNewFriend.setError(getResources().getString(R.string.enter_name));
                } else {
                    if (mListener != null) {
                        mListener.onFighterSelected(mMessageId, mNewFriendUser);
                    }

                    dismiss();
                }
            }
        });

        TextView tv_no = contentView.findViewById(R.id.btn_no);
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ID IN DLG", mMessageId + "");
//                if (mMessageId == FightActionActivity.PHRASE_SELECT) {
//                    mListener.onFighterSelected(mMessageId, null);
//                }
                dismiss();
            }
        });

        Dialog d = builder.setView(contentView).show();

        Button btn1 = d.getWindow().findViewById(android.R.id.button1);
        Button btn2 = d.getWindow().findViewById(android.R.id.button2);
        Button btn3 = d.getWindow().findViewById(android.R.id.button3);

        btn1.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btn1.setTextColor(getActivity().getResources().getColor(R.color.textColorSecondary));
        btn2.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btn2.setTextColor(getActivity().getResources().getColor(R.color.textColorRed));
        btn3.setTypeface(InspirationDayApplication.getCustomFontTypeface());

        return d;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        InputMethodManager imm =
                (InputMethodManager)mNewFriend.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive())
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        super.onDismiss(dialog);
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
        void onFighterSelected (int messageId, ListUser user);
    }

}
