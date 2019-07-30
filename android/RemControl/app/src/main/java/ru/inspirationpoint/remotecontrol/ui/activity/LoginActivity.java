package ru.inspirationpoint.remotecontrol.ui.activity;

import android.view.WindowManager;

import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import ru.inspirationpoint.remotecontrol.BR;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.databinding.ActivityLoginBinding;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.ui.dialog.InputDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.MessageDialog;


public class LoginActivity extends BindingActivity<ActivityLoginBinding, LoginActivityVM> implements InputDialog.Listener, MessageDialog.Listener{

    @Override
    public LoginActivityVM onCreate() {
        setTheme(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ? R.style.AppThemeDark_NoActionBar : R.style.AppTheme_NoActionBar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return new LoginActivityVM(this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onInputSet(int messageId, String input) {
        getViewModel().onInputSet(input);
    }

    @Override
    public void onMessageDialogDismissed(int messageId) {
        getViewModel().onMessageDialogDismissed(messageId);
    }
}