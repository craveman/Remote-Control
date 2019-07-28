package ru.inspirationpoint.inspirationrc.rc.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class AboutAppActivity extends LocalAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(true);

        configureToolbar();
        setCustomTitle(R.string.about_app);

        String version = "FIE 1.0";
//        try {
//            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            version = pInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }


        AppCompatImageView view = findViewById(R.id.iv_about_logo);
        view.setImageResource(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
        R.drawable.logo_dark : R.drawable.about_logo);

        TextView verTextView = findViewById(R.id.about_app_ver);
        verTextView.setText(getString(R.string.about_app_ver, version));

        TextView instructionButton = findViewById(R.id.about_app_instruction);
        instructionButton.setTextColor(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
        getResources().getColor(R.color.whiteCard) : getResources().getColor(R.color.textColorPrimary));
        instructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(getString(R.string.about_app_web_link)));
                startActivityWithException(intent);
            }
        });

        TextView emailButton = findViewById(R.id.about_app_email);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(getString(R.string.about_app_mail_link)));
                startActivityWithException(intent);
            }
        });

        TextView telegramButton = findViewById(R.id.about_app_telegram);
        telegramButton.setText(Html.fromHtml(
                getResources().getString(R.string.telegram) +
                        "<a href=\"https://t.me/inspirationpoint\">@inspirationpoint</a> "));
        telegramButton.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SettingsManager.getValue(CommonConstants.LOCALE_CHANGED_FIELD, false)) {
            SettingsManager.setValue(CommonConstants.LOCALE_CHANGED_FIELD, false);

            recreate();
        }
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

    public void startActivityWithException(Intent intent) {
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.intent_error), Toast.LENGTH_SHORT).show();
        }
    }
}
