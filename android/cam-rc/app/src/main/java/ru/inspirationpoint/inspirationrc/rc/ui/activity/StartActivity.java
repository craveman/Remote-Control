package ru.inspirationpoint.inspirationrc.rc.ui.activity;

import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import ru.inspirationpoint.inspirationrc.BR;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.databinding.ActivityStartBinding;


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