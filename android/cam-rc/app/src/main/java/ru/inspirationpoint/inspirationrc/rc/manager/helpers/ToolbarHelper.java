package ru.inspirationpoint.inspirationrc.rc.manager.helpers;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class ToolbarHelper {
    public static Toolbar configureToolbar(AppCompatActivity activity, boolean show, String title) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ViewStub viewStub = activity.findViewById(R.id.viewStub);
        viewStub.setLayoutResource(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ? R.layout.app_bar_theme_dark : R.layout.app_bar);
        View testView = viewStub.inflate();
        Toolbar toolbar = testView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(show);
            activity.getSupportActionBar().setHomeButtonEnabled(show);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(show);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView titleTv = toolbar.findViewById(R.id.title_custom);
            titleTv.setText(title);
            return toolbar;
        } else return null;
    }
}
