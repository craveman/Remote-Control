package ru.inspirationpoint.inspirationrc.rc.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.helpers.LocaleHelper;

public class SettingsActivity extends LocalAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        configureToolbar();

        setCustomTitle(R.string.settings);

        Button button = findViewById(R.id.ru_language);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleHelper.setLocale(SettingsActivity.this, "ru");
                SettingsManager.setValue(CommonConstants.LOCALE_CHANGED_FIELD, true);
                recreate();
            }
        });

        button = findViewById(R.id.en_language);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleHelper.setLocale(SettingsActivity.this, "en");
                SettingsManager.setValue(CommonConstants.LOCALE_CHANGED_FIELD, true);
                recreate();
            }
        });

        CheckBox checkBox = findViewById(R.id.sound);
        checkBox.setChecked(SettingsManager.getValue(CommonConstants.SOUND_FIELD, true));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsManager.setValue(CommonConstants.SOUND_FIELD, isChecked);
            }
        });
        checkBox.setButtonDrawable(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ? R.drawable.checkbox_selector_dark : R.drawable.checkbox_selector_light);

        checkBox = findViewById(R.id.vibration);
        checkBox.setChecked(SettingsManager.getValue(CommonConstants.VIBRATION_FIELD, true));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsManager.setValue(CommonConstants.VIBRATION_FIELD, isChecked);
            }
        });
        checkBox.setButtonDrawable(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ? R.drawable.checkbox_selector_dark : R.drawable.checkbox_selector_light);

        SwitchCompat themeSwitch = findViewById(R.id.theme_switch);
        themeSwitch.setChecked(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false));
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsManager.setValue(CommonConstants.IS_DARK_THEME, b);
                SettingsManager.setValue(CommonConstants.IS_THEME_CHANGED, true);
                InspirationDayApplication.setDarkTheme(b);
                InspirationDayApplication.getApplication().setTheme(b);
                recreate();
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
}
