package ru.inspirationpoint.remotecontrol.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.io.IOException;
import java.util.Locale;

import ru.inspirationpoint.inspirationrc.BR;
import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.camera.utils.PermissionHelper;
import ru.inspirationpoint.inspirationrc.databinding.ActivityNewFightBinding;
import ru.inspirationpoint.inspirationrc.databinding.NavHeaderDairyBinding;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.coreObjects.Device;
import ru.inspirationpoint.inspirationrc.manager.helpers.LocaleHelper;
import ru.inspirationpoint.inspirationrc.rc.manager.Camera;
import ru.inspirationpoint.inspirationrc.rc.manager.Referee;
import ru.inspirationpoint.inspirationrc.rc.manager.helpers.ToolbarHelper;
import ru.inspirationpoint.inspirationrc.rc.ui.NavHeaderVM;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.CameraConnectionDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.ConfirmationDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.MessageDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.QRScanDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.SyncDialog;


public class NewFightActivity extends BindingActivity<ActivityNewFightBinding, NewFightActivityVM>
        implements SyncDialog.SyncListener, ConfirmationDialog.Listener, NavigationView.OnNavigationItemSelectedListener,
        CameraConnectionDialog.CameraConnectionListener, QRScanDialog.QRListener, MessageDialog.Listener {

    private Device camera;
    private Device referee;
    private int currentApiVersion;

    @Override
    public NewFightActivityVM onCreate() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        currentApiVersion = Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
        return new NewFightActivityVM(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang_code = SettingsManager.getValue(CommonConstants.LANGUAGE_FIELD, "en");
        Context context = LocaleHelper.changeLang(newBase, lang_code);
        super.attachBaseContext(context);
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ? R.style.AppThemeDark_NoActionBar : R.style.AppTheme_NoActionBar);
        String languageToLoad  = SettingsManager.getValue(CommonConstants.LANGUAGE_FIELD, "en"); // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        Toolbar toolbar = ToolbarHelper.configureToolbar(this, true, getResources().getString(R.string.new_fight));
        DrawerLayout drawer = getBinding().drawerLayoutTrainings;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        getBinding().navViewTraining.setNavigationItemSelectedListener(this);
        NavHeaderDairyBinding bind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.nav_header_dairy, getBinding()
                .navViewTraining, true);
        bind.setViewModel(new NavHeaderVM(SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""),
                SettingsManager.getValue(CommonConstants.LAST_USER_EMAIL_FIELD, "")));
        bind.userEmail.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        bind.userName.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        bind.executePendingBindings();
        getBinding().navViewTraining.getMenu().getItem(8).setVisible(false);
        getBinding().navViewTraining.getMenu().getItem(9).setVisible(false);
//        changeSelectedWeapon(5);
//        getBinding().navViewTraining.getMenu().findItem(R.id.messages).setVisible(false);
//        getBinding().navViewTraining.getMenu().findItem(R.id.profile).setVisible(false);
//        SpannableString s1 = new SpannableString(getResources().getString(R.string.new_fight));
//        s1.setSpan(new UnderlineSpan(), 0, s1.length(), 0);
//        s1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textColorSecondary)), 0, s1.length(), 0);
//        SpannableString s2 = new SpannableString(getResources().getString(R.string.mode_camera));
//        s2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textColorSecondary)), 0, s2.length(), 0);
//        SpannableString s3 = new SpannableString(getResources().getString(R.string.my_diary));
//        s3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textColorSecondary)), 0, s3.length(), 0);
//        SpannableString s4 = new SpannableString(getResources().getString(R.string.reserve_referee));
//        s4.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textColorSecondary)), 0, s4.length(), 0);
//        getBinding().navViewTraining.getMenu().findItem(R.id.mode_rc).setTitle(s1);
//        getBinding().navViewTraining.getMenu().findItem(R.id.mode_rc).setIcon(R.drawable.menu_item_active);
//        getBinding().navViewTraining.getMenu().findItem(R.id.mode_camera).setTitle(s2);
//        getBinding().navViewTraining.getMenu().findItem(R.id.mode_camera).setIcon(R.drawable.menu_item_inactive);
//        getBinding().navViewTraining.getMenu().findItem(R.id.mode_diary).setTitle(s3);
//        getBinding().navViewTraining.getMenu().findItem(R.id.mode_diary).setIcon(R.drawable.menu_item_inactive);
//        getBinding().navViewTraining.getMenu().findItem(R.id.reserve_ref).setTitle(s4);
//        getBinding().navViewTraining.getMenu().findItem(R.id.reserve_ref).setIcon(R.drawable.menu_item_inactive);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_fight;
    }


    @Override
    public void onSyncCodeSet(int code) {
        getViewModel().onSyncCodeSet(code);
    }

//    @Override
//    public void onSyncDecline() {
//        getViewModel().onSyncDecline();
//    }

    public void askForCommands() {
        ConfirmationDialog.show(this, 565, "Командные соревнования", "Включить режим?");
    }

    public void cancelCommands() {
        ConfirmationDialog.show(this, 676, "Командные соревнования", "Выйти из режима?");
    }

    @Override
    public void onSyncCancel() {
        getViewModel().onSyncCancel();
    }

    public void onSyncReset() {
        ConfirmationDialog.show(NewFightActivity.this, 1, getResources().getString(R.string.dlg_reset_title),
                getResources().getString(R.string.dlg_reset_message));
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = getBinding().drawerLayoutTrainings;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getViewModel().onNavigationItemSelected(item);
        return true;
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

    @Override
    public void onConfirmed(int messageId) {
        switch (messageId) {
            case 1:
                InspirationDayApplication.getApplication().endTcp();
                getViewModel().syncState.set(NewFightActivityVM.SYNC_STATE_NONE);
                SettingsManager.removeValue(CommonConstants.LAST_CONNECTED_SM_IP);
                SettingsManager.removeValue(CommonConstants.LAST_CONNECTED_SM_CODE);
                break;
            case 101:
//                new Thread(() -> {
//                    try {
//                        //TODO make remove device from core
//
//                    } finally {
//                        runOnUiThread(() -> {
//                            getViewModel().adapter.setCameras(getViewModel().core.getConnectedCameras());
//                            camera = null;
//                        });
//                    }
//                }).start();
                break;
            case 565:
//                CommandFightInput input = new CommandFightInput();
//                input.fightNumber = 1;
//                input.left = 0;
//                input.right = 0;
//                DataManager.instance().saveCommandFight(new CommandFightInput(), new DataManager.RequestListener<SaveCommandFightResult>() {
//                    @Override
//                    public void onSuccess(SaveCommandFightResult result) {
//                        Log.wtf("SAVED COMMAND", result.commandFight._id);
//                        SettingsManager.setValue(CURRENT_COMMANDS_ID, result.commandFight._id);
//                        getViewModel().commandsChecked.set(true);
//                        SettingsManager.setValue(IS_COMMANDS_ENABLED, true);
//                    }
//
//                    @Override
//                    public void onFailed(String error, String message) {
//                        Log.wtf("FAIL CREATE COMM", error);
//                    }
//
//                    @Override
//                    public void onStateChanged(boolean inProgress) {
//
//                    }
//                });
                break;
        }
    }

    public void changeSelectedWeapon(int weapon) {
        SpannableString s1 = new SpannableString(getResources().getString(R.string.type_rapier));
        SpannableString s2 = new SpannableString(getResources().getString(R.string.type_epee));
        SpannableString s3 = new SpannableString(getResources().getString(R.string.type_saber));
        SpannableString s4 = new SpannableString(getResources().getString(R.string.off));
        s1.setSpan(new ForegroundColorSpan(getResources().getColor(weapon == 1 ? R.color.textColorPrimary : R.color.textColorSecondary)), 0, s1.length(), 0);
        s2.setSpan(new ForegroundColorSpan(getResources().getColor(weapon == 2 ? R.color.textColorPrimary : R.color.textColorSecondary)), 0, s2.length(), 0);
        s3.setSpan(new ForegroundColorSpan(getResources().getColor(weapon == 3 ? R.color.textColorPrimary : R.color.textColorSecondary)), 0, s3.length(), 0);
        s4.setSpan(new ForegroundColorSpan(getResources().getColor(weapon == 0 ? R.color.textColorPrimary : R.color.textColorSecondary)), 0, s4.length(), 0);
        getBinding().navViewTraining.getMenu().findItem(R.id.foil).setTitle(s1);
        getBinding().navViewTraining.getMenu().findItem(R.id.epee).setTitle(s2);
        getBinding().navViewTraining.getMenu().findItem(R.id.saber).setTitle(s3);
        getBinding().navViewTraining.getMenu().findItem(R.id.off).setTitle(s4);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO resume to it
//        if (!PermissionHelper.hasCameraPermission(this) || !PermissionHelper.hasWriteStoragePermission(this)) {
//            PermissionHelper.requestCameraPermission(this, true);
//        }
    }

    public void onCamClick(Device camera) {
        this.camera = camera;
        ConfirmationDialog.show(this, 101, getString(R.string.disconnect_title),
                getString(R.string.disconnect_text, camera.getName()));
    }

    public void onQRScanNeed() {
        QRScanDialog.newInstance().show(this.getSupportFragmentManager(), "QRSCAN");
    }

    public void showCamConnDlg(Camera camera){
        CameraConnectionDialog dialog = CameraConnectionDialog.newInstance(camera.name.get(), !InspirationDayApplication.getApplication().getRepeaters().isEmpty());
        dialog.show(getSupportFragmentManager(), "CAM CONN");
    }

    @Override
    public void onReady(boolean isTargetSM, boolean isSaveNeeded) {
        InspirationDayApplication.getApplication().setSMVideoTarget(isTargetSM);
        InspirationDayApplication.getApplication().setSaveNeeded(isSaveNeeded);
    }

    @Override
    public void onCodeDetected(String text) {
        getViewModel().onQRScanned(text);
    }

    @Override
    public void onMessageDialogDismissed(int messageId) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}