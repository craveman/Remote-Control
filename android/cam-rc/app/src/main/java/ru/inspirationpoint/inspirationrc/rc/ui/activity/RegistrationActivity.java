package ru.inspirationpoint.inspirationrc.rc.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.MessageDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.WeaponChooseDialog;
import server.schemas.responses.RegisterResult;

public class RegistrationActivity extends LocalAppCompatActivity implements MessageDialog.Listener, WeaponChooseDialog.Listener {

    private static final int SUCCESS_MESSAGE_ID = 1;
    private static final int OTHER_MESSAGE_ID = 2;

    private View mProgressView;
    private View mMainContentView;
    private EditText mNameEditText;
    private EditText mEmailEditText;
    private TextView weaponType;
    private TextView weaponTitle;
    private String weapon = "";
    private EditText mNickEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        configureToolbar();

        setCustomTitle(R.string.registration);

        ImageButton registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(view -> attemptRegister());

        mMainContentView = findViewById(R.id.main_content);
        mProgressView = findViewById(R.id.login_progress);
        weaponType = findViewById(R.id.weapon_type);
        weaponTitle = findViewById(R.id.weapon_title);
        weaponTitle.setOnClickListener(view -> WeaponChooseDialog.show(RegistrationActivity.this));
        weaponType.setOnClickListener(view -> WeaponChooseDialog.show(RegistrationActivity.this));

        mNameEditText = findViewById(R.id.name_edit);
        mEmailEditText = findViewById(R.id.email_edit);
        mNickEditText = findViewById(R.id.nick_edit);
        mNickEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                EditText edit = (EditText) getCurrentFocus();
                if (edit != null) {
                    if (!s.toString().startsWith("@")) {
                        edit.setText("@");
                        Selection.setSelection(edit.getText(), edit.getText().length());
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void attemptRegister() {
        Helper.hideKeyboard(getCurrentFocus(), this);

        mNameEditText.setError(null);
        mEmailEditText.setError(null);

        String name = mNameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String nick = mNickEditText.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mNameEditText.setError(getString(R.string.error_field_required));
            mNameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            mEmailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nick)) {
            mNickEditText.setError(getString(R.string.error_field_required));
            mNickEditText.requestFocus();
            return;
        }

        if (!Helper.isValidEmail(email)) {
            mEmailEditText.setError(getString(R.string.wrong_email));
            mEmailEditText.requestFocus();
            return;
        }

//        DataManager.instance().register(email, name, weapon, nick, new DataManager.RequestListener<RegisterResult>() {
//            @Override
//            public void onSuccess(RegisterResult result) {
//                MessageDialog.show(RegistrationActivity.this, SUCCESS_MESSAGE_ID, null, getString(R.string.success_registration_message));
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
////                if (error.equalsIgnoreCase("already_exists")) {
////                    message = getString(R.string.email_exist);
////                }
//                MessageDialog.show(RegistrationActivity.this, 0, getString(R.string.registration_error), message);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//                showProgress(inProgress);
//            }
//        });
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mMainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onMessageDialogDismissed(int messageId) {
        if (messageId == SUCCESS_MESSAGE_ID) {
            finish();
        }
    }

    @Override
    public void onWeaponChoosed(String weaponType) {
        weapon = weaponType;
        this.weaponType.setText(weaponType);
        this.weaponTitle.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        this.weaponTitle.setText(String.format("%s:", getResources().getString(R.string.weapon_type)));
    }
}
