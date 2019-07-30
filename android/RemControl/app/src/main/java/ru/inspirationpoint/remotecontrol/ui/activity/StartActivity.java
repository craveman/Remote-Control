package ru.inspirationpoint.remotecontrol.ui.activity;

import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import ru.inspirationpoint.remotecontrol.BR;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.databinding.ActivityStartBinding;


public class StartActivity extends BindingActivity<ActivityStartBinding, StartActivityVM> {

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

}