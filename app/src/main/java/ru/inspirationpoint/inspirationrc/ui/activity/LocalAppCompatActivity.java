package ru.inspirationpoint.inspirationrc.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.helpers.LocaleHelper;

public class LocalAppCompatActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected ViewStub viewStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SettingsManager.getValue(Constants.IS_DARK_THEME, false) ? R.style.AppThemeDark_NoActionBar : R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    protected int getLayoutResource() {
        return  0;
    }

    protected void setCustomTitle(String title) {
        TextView titleTv = (TextView) toolbar.findViewById(R.id.title_custom);
        titleTv.setText(title);
    }

    protected void setCustomTitle(int resId) {
        TextView titleTv = (TextView) toolbar.findViewById(R.id.title_custom);
        titleTv.setText(getResources().getString(resId));
    }

    protected void configureToolbar() {
        viewStub = (ViewStub) findViewById(R.id.viewStub);
        viewStub.setLayoutResource(SettingsManager.getValue(Constants.IS_DARK_THEME, false) ? R.layout.app_bar_theme_dark : R.layout.app_bar);
        View testView = viewStub.inflate();
        toolbar = (Toolbar) testView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

}
