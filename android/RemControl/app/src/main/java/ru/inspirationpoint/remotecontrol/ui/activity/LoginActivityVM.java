package ru.inspirationpoint.remotecontrol.ui.activity;

import android.content.Intent;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.helpers.Helper;
import ru.inspirationpoint.remotecontrol.manager.helpers.ToolbarHelper;
import ru.inspirationpoint.remotecontrol.ui.dialog.InputDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.MessageDialog;


public class LoginActivityVM extends ActivityViewModel<LoginActivity> {

    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableBoolean isInProgress = new ObservableBoolean();

    public LoginActivityVM(LoginActivity activity) {
        super(activity);
        ToolbarHelper.configureToolbar(activity, false, activity.getResources().getString(R.string.login));
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getActivity().getBinding().passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    attemptLogin();
                }
                return false;
            }
        });
        email.set(SettingsManager.getValue(CommonConstants.LAST_USER_EMAIL_FIELD, ""));
    }

    public void attemptLogin() {
        Helper.hideKeyboard(getActivity().getCurrentFocus(), getActivity());

        EditText mEmailEditText = getActivity().getBinding().emailEdit;
        EditText mPasswordEditText = getActivity().getBinding().passwordEdit;
        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);


        if (TextUtils.isEmpty(email.get())) {
            mEmailEditText.setError(getActivity().getString(R.string.error_field_required));
            mEmailEditText.requestFocus();
            return;
        }

        if (!Helper.isValidEmail(email.get())) {
            mEmailEditText.setError(getActivity().getString(R.string.wrong_email));
            mEmailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password.get())) {
            mPasswordEditText.setError(getActivity().getString(R.string.error_field_required));
            mPasswordEditText.requestFocus();
            return;
        }

//        DataManager.instance().login(email.get(), password.get(), new DataManager.RequestListener<LoginResult>() {
//
//            @Override
//            public void onSuccess(LoginResult result) {
//                if (DataManager.instance().isLoggedIn()) {
//                    SettingsManager.setValue(CommonConstants.LAST_USER_ID_FIELD, result.profile._id);
//                    SettingsManager.setValue(CommonConstants.LAST_USER_NAME_FIELD, result.profile.name);
//                    SettingsManager.setValue(CommonConstants.LAST_USER_EMAIL_FIELD, result.profile.email);
//
//                    Intent intent = new Intent(getActivity(), InfoActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    getActivity().startActivity(intent);
//                    getActivity().finish();
//                }
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                if (error.equalsIgnoreCase("invalid_user")) {
//                    message = getActivity().getString(R.string.invalid_user_and_password);
//                }
//                MessageDialog.show(getActivity(), 0, getActivity().getString(R.string.login_error), message);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//                isInProgress.set(inProgress);
//            }
//        });
    }

    public void register() {
        Intent intent = new Intent(getActivity(), RegistrationActivity.class);
        getActivity().startActivity(intent);
    }

    private void attemptReset(String email) {
        Helper.hideKeyboard(getActivity().getCurrentFocus(), getActivity());

        if (!Helper.isValidEmail(email)) {
            MessageDialog.show(getActivity(), 111, getActivity().getResources().getString(R.string.warning),
                    getActivity().getResources().getString(R.string.wrong_email));
            return;
        }

//        DataManager.instance().resetPassword(email, new DataManager.RequestListener<ResetPasswordResult>() {
//            @Override
//            public void onSuccess(ResetPasswordResult result) {
//                String messageText = getActivity().getString(R.string.reset_complete);
//                MessageDialog.show(getActivity(), 0, null, messageText);
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                if (error.equalsIgnoreCase("invalid_user") || error.equalsIgnoreCase("invalid_parameter")) {
//                    message = getActivity().getString(R.string.invalid_email);
//                }
//                MessageDialog.show(getActivity(), 0, getActivity().getString(R.string.reset_error), message);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//                isInProgress.set(inProgress);
//            }
//        });
    }

    public void attemptReset() {
        InputDialog.show(getActivity(), 1, getActivity().getResources().getString(R.string.reset_password), "", "");
    }

    public void onInputSet(String input) {
        attemptReset(input);
    }

    public void onMessageDialogDismissed(int messageId) {
        if (messageId == 111)
            InputDialog.show(getActivity(), 0, getActivity().getResources().getString(R.string.reset_password), "", "");
    }

    @Override
    public boolean onBackKeyPress() {
        return true;
    }
}