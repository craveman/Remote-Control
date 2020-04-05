package ru.inspirationpoint.remotecontrol.ui.activity;

import android.content.Intent;

import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import ru.inspirationpoint.remotecontrol.BR;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.databinding.ActivityStartBinding;
import ru.inspirationpoint.remotecontrol.ui.dialog.MessageDialog;

import static ru.inspirationpoint.remotecontrol.ui.activity.StartActivityVM.LOCATION_REQUEST;


public class StartActivity extends BindingActivity<ActivityStartBinding, StartActivityVM> implements MessageDialog.Listener {

    @Override
    public StartActivityVM onCreate() {
        return new StartActivityVM(this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    public void onMessageDialogDismissed(int messageId) {
        if (messageId == LOCATION_REQUEST) {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
        }
    }
}