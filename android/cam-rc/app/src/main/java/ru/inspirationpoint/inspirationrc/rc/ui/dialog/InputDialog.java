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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class InputDialog extends DialogFragment {

    private final static String MESSAGE_ID_PARAM = "message_id";
    private final static String MESSAGE_FIELD = "message";
    private final static String EDIT_FIELD = "edit";

    private Listener mListener;
    private EditText mInputTextView;
    private int mMessageId;

    public static void show(FragmentActivity fragmentActivity, int messageId, String title, String message, String edit) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        InputDialog myDialogFragment = new InputDialog();
        Bundle params = new Bundle();
        params.putString(MESSAGE_FIELD, message);
        params.putString(EDIT_FIELD, edit);
        params.putInt(MESSAGE_ID_PARAM, messageId);
        params.putString("title", title);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "InputDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        String message = getArguments().getString(MESSAGE_FIELD);
        String title = getArguments().getString("title");
        String edit = getArguments().getString(EDIT_FIELD);
        if (message == null) message = "";
        if (edit == null) edit = "";
        mMessageId = getArguments().getInt(MESSAGE_ID_PARAM);

        View contentView = inflater.inflate(R.layout.input_dlg, null);

        TextView titleTextView = contentView.findViewById(R.id.title);
        titleTextView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        titleTextView.setText(title);

        ImageButton closeButton = contentView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);

        mInputTextView = contentView.findViewById(R.id.email_input);
        mInputTextView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        if (!edit.equals("")) mInputTextView.setText(edit);

        TextView messageTv = contentView.findViewById(R.id.input_message);
        messageTv.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        if (!message.equals("")) messageTv.setText(message);

//        builder.setView(contentView)
//                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        if (mListener != null) {
//                            mListener.onCommentSet(mMessageId, mCommentTextView.getText().toString());
//                        }
//                    }
//                });

        Dialog d = builder.setView(contentView)
                .setPositiveButton(getString(R.string.reset_password), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onInputSet(mMessageId, mInputTextView.getText().toString());
                        }
                    }
                }).show();

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
        void onInputSet(int messageId, String input);
    }
}
