package ru.inspirationpoint.remotecontrol.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.inspirationpoint.remotecontrol.BR;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.databinding.ActivityFightBinding;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FullFightInfo;
import ru.inspirationpoint.remotecontrol.manager.helpers.LocaleHelper;
import ru.inspirationpoint.remotecontrol.ui.dialog.ConfirmationDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightApplyDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightFinishAskDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightFinishedDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightRestoreDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.PhraseDialog;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.UNFINISHED_FIGHT;


public class FightActivity extends BindingActivity<ActivityFightBinding, FightActivityVM> implements
        FightApplyDialog.ApplyListener, FightFinishedDialog.FightFinishedListener,
        FightFinishAskDialog.FightFinisAskListener, FightRestoreDialog.RestoreListener, PhraseDialog.Listener,
        ConfirmationDialog.Listener {


    private int currentApiVersion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        String languageToLoad  = SettingsManager.getValue(CommonConstants.LANGUAGE_FIELD, "en"); // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
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
        if(!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    public FightActivityVM onCreate() {
        return new FightActivityVM(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        getViewModel().onTimerStopClick();
        if (getViewModel().goToNewScreen) {
            getViewModel().core.keepAliveDirectServer = true;
            getViewModel().goToNewScreen = false;
        } else {
            getViewModel().core.keepAliveDirectServer = false;
        }
        getViewModel().stopSync();
        super.onStop();
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fight;
    }

    @Override
    public void onBackPressed() {
        if (getViewModel().screenState.get() != FightActivityVM.SCREEN_SYNC_LAY &&
                getViewModel().timerState.get() != FightActivityVM.TIMER_STATE_IN_PROGRESS) {
            getViewModel().onCloseBtn();
        }
    }

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource){
        imageView.setImageResource(resource);
    }

    @BindingAdapter("android:background")
    public static void setBackground(View view, int resource){
        view.setBackgroundColor(resource);
    }

    @BindingAdapter("android:textColor")
    public static void setTextColor(TextView textView, int color) {
        textView.setTextColor(color);
    }

    @BindingAdapter("android:src")
    public static void setImageDrawable(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @Override
    public void apply() {
        getViewModel().fightApplyOk();
    }

    @Override
    public void askPrev() {
        getViewModel().fightPrev();
    }

    @Override
    public void askNext() {
        getViewModel().fightNext();
    }

    @Override
    public void prev() {
        getViewModel().fightPrev();
    }

    @Override
    public void next() {
        getViewModel().fightNext();
    }

    @Override
    public void semiExit() {
        Intent intent = new Intent(FightActivity.this, NewFightActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void ok() {
        getViewModel().onFightFinishBegin();
    }

    @Override
    public void onAccept(FullFightInfo restoredInfo) {
        getViewModel().restoreFromExisted(restoredInfo);
    }

    @Override
    public void onDecline() {
        getViewModel().fightId = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        SettingsManager.setValue(UNFINISHED_FIGHT, getViewModel().fightId);
//        if (DataManager.instance().getCurrentFight() != null) {
//            getViewModel().fightData = DataManager.instance().getCurrentFight();
//        } else {
//            getViewModel().fightData = new FightData("", new Date(), new FighterData("", getViewModel().leftName),
//                    new FighterData("", getViewModel().rightName),
//                    "", SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
//        }
//        DataManager.instance().saveFight(Helper.convertFightDataToInput(getViewModel().fightData), new DataManager.RequestListener<SaveFightResult>() {
//            @Override
//            public void onSuccess(SaveFightResult result) {
//                SettingsManager.setValue(CommonConstants.LAST_FIGHT_ID, result.fight._id);
//                Log.wtf("Fight ID", result.fight._id);
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                Log.wtf("ERR FIGHT UPL", error + "|" + message);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//            }
//        });
    }

    public void showPhraseDialog(boolean left) {
        PhraseDialog.show(this, left);
    }

    @Override
    public void onEventSelected(int position, boolean isLeft, boolean cancelled) {
        getViewModel().onPhraseSelected(position, isLeft);
    }

    @Override
    public void onConfirmed(int messageId) {
        if (messageId == 456) {
            getViewModel().onMenuPause();
        }
    }
}