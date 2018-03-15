package ru.inspirationpoint.inspirationrc.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.DataManager;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.ui.dialog.MessageDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.WeaponChooseDialog;
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

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(true);

        setCustomTitle(R.string.registration);

        ImageButton registerButton = (ImageButton) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mMainContentView = findViewById(R.id.main_content);
        mProgressView = findViewById(R.id.login_progress);
        weaponType = (TextView) findViewById(R.id.weapon_type);
        weaponTitle = (TextView) findViewById(R.id.weapon_title);
        weaponTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeaponChooseDialog.show(RegistrationActivity.this);
            }
        });
        weaponType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeaponChooseDialog.show(RegistrationActivity.this);
            }
        });

        mNameEditText = (EditText) findViewById(R.id.name_edit);
        mEmailEditText = (EditText) findViewById(R.id.email_edit);
        mNickEditText = (EditText) findViewById(R.id.nick_edit);
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

        DataManager.instance().register(email, name, weapon, nick, new DataManager.RequestListener<RegisterResult>() {
            @Override
            public void onSuccess(RegisterResult result) {
                MessageDialog.show(RegistrationActivity.this, SUCCESS_MESSAGE_ID, null, getString(R.string.success_registration_message));
            }

            @Override
            public void onFailed(String error, String message) {
//                if (error.equalsIgnoreCase("already_exists")) {
//                    message = getString(R.string.email_exist);
//                }
                MessageDialog.show(RegistrationActivity.this, 0, getString(R.string.registration_error), message);
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
        this.weaponTitle.setTextColor(!SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        this.weaponTitle.setText(String.format("%s:", getResources().getString(R.string.weapon_type)));
    }
}
