package ru.inspirationpoint.inspirationrc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.DataManager;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.ui.dialog.InputDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.MessageDialog;
import server.schemas.responses.LoginResult;
import server.schemas.responses.ResetPasswordResult;

public class LoginActivity extends LocalAppCompatActivity implements InputDialog.Listener, MessageDialog.Listener {

    private final static int EXIT_MESSAGE_ID = 1;

    private View mProgressView;
    private View mMainContentView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        configureToolbar();
        setCustomTitle(getResources().getString(R.string.login));

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        ImageButton signInButton = (ImageButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView registerButton = (TextView) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        TextView resetButton = (TextView) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputDialog.show(LoginActivity.this, EXIT_MESSAGE_ID, getResources().getString(R.string.reset_password), "", "");
            }
        });

        mMainContentView = findViewById(R.id.main_content);
        mProgressView = findViewById(R.id.login_progress);
        mEmailEditText = (EditText) findViewById(R.id.email_edit);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit);
        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    attemptLogin();
                }
                return false;
            }
        });

        mEmailEditText.setText(SettingsManager.getValue(Constants.LAST_USER_EMAIL_FIELD, ""));
    }

    private void attemptLogin() {
        Helper.hideKeyboard(getCurrentFocus(), this);

        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);

        final String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            mEmailEditText.requestFocus();
            return;
        }

        if (!Helper.isValidEmail(email)) {
            mEmailEditText.setError(getString(R.string.wrong_email));
            mEmailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getString(R.string.error_field_required));
            mPasswordEditText.requestFocus();
            return;
        }

        DataManager.instance().login(email, password, new DataManager.RequestListener<LoginResult>() {

            @Override
            public void onSuccess(LoginResult result) {
                if (DataManager.instance().isLoggedIn()) {
                    SettingsManager.setValue(Constants.LAST_USER_ID_FIELD, result.profile._id);
                    SettingsManager.setValue(Constants.LAST_USER_NAME_FIELD, result.profile.name);
                    SettingsManager.setValue(Constants.LAST_USER_EMAIL_FIELD, result.profile.email);
                    goToMainActivity();
                }
            }

            @Override
            public void onFailed(String error, String message) {
                if (error.equalsIgnoreCase("invalid_user")) {
                    message = getString(R.string.invalid_user_and_password);
                }
                MessageDialog.show(LoginActivity.this, 0, getString(R.string.login_error), message);
            }

            @Override
            public void onStateChanged(boolean inProgress) {
                showProgress(inProgress);
            }
        });
    }

    private void register() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void attemptReset(String email) {
        Helper.hideKeyboard(getCurrentFocus(), this);

        if (!Helper.isValidEmail(email)) {
            MessageDialog.show(this, 111, getResources().getString(R.string.warning),
                    getResources().getString(R.string.wrong_email));
            return;
        }

        DataManager.instance().resetPassword(email, new DataManager.RequestListener<ResetPasswordResult>() {
            @Override
            public void onSuccess(ResetPasswordResult result) {
                String messageText = getString(R.string.reset_complete);
                MessageDialog.show(LoginActivity.this, 0, null, messageText);
            }

            @Override
            public void onFailed(String error, String message) {
                if (error.equalsIgnoreCase("invalid_user") || error.equalsIgnoreCase("invalid_parameter")) {
                    message = getString(R.string.invalid_email);
                }
                MessageDialog.show(LoginActivity.this, 0, getString(R.string.reset_error), message);
            }

            @Override
            public void onStateChanged(boolean inProgress) {
                showProgress(inProgress);
            }
        });
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mMainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void goToMainActivity() {
        finish();

        Intent intent = new Intent(this, NewFightActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onInputSet(int messageId, String input) {
        attemptReset(input);
    }

    @Override
    public void onMessageDialogDismissed(int messageId) {
        if (messageId == 111)
        InputDialog.show(this, 0, getResources().getString(R.string.reset_password), "", "");
    }
}