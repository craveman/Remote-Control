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
import android.provider.Settings;
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
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FighterData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FullFightInfo;
import ru.inspirationpoint.remotecontrol.manager.helpers.LocaleHelper;
import ru.inspirationpoint.remotecontrol.ui.dialog.ConfirmationDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightApplyDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightFinishAskDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightFinishedDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightRestoreDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.MessageDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.PhraseDialog;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.UNFINISHED_FIGHT;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.SCREEN_MAIN;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.TIMER_STATE_IN_PROGRESS;


public class FightActivity extends BindingActivity<ActivityFightBinding, FightActivityVM> implements
        FightApplyDialog.ApplyListener, FightFinishedDialog.FightFinishedListener,
        FightFinishAskDialog.FightFinisAskListener, FightRestoreDialog.RestoreListener, PhraseDialog.Listener,
        ConfirmationDialog.Listener, MessageDialog.Listener {


    private int currentApiVersion;
    public static final int PAUSE_START_MESSAGE = 456;
    public static final int PAUSE_FINISH_MESSAGE = 435;
    public static final int RESET_MESSAGE = 424;
    public static final int EXIT_MESSAGE = 283;
    public static final int WIFI_MESSAGE = 7585;
    public static final int PAUSE_CALL_MAIN_MESSAGE = 457;

    public static final int REMOVE_FIGHT_MESSAGE = 255;
    public static final int END_FIGHT_MESSAGE = 232;

    public static final int FIGHT_FINISHED_OK = 223;


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

        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(visibility -> {
                        if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                        {
                            decorView.setSystemUiVisibility(flags);
                        }
                    });
        }
//        String languageToLoad  = SettingsManager.getValue(CommonConstants.LANGUAGE_FIELD, "en");
//        Locale locale = new Locale(languageToLoad);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getBaseContext().getResources().updateConfiguration(config,
//                getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        String lang_code = SettingsManager.getValue(CommonConstants.LANGUAGE_FIELD, "en");
//        Context context = LocaleHelper.changeLang(newBase, lang_code);
//        super.attachBaseContext(context);
//    }

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
                getViewModel().timerState.get() != TIMER_STATE_IN_PROGRESS) {
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
//        Intent intent = new Intent(FightActivity.this, NewFightActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
        getViewModel().exitCyranoMode();
    }

    @Override
    public void ok() {
        getViewModel().onFightFinishBegin();
    }

    @Override
    public void onAccept(FightData restoredInfo) {
        getViewModel().restoreFromExisted(restoredInfo);
    }

    @Override
    public void onDecline() {
        getViewModel().core.vibr();
//        DataManager.instance().saveFight(Helper.convertFightDataToInput(fightData), new DataManager.RequestListener<SaveFightResult>() {
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
        getViewModel().fightId = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        SettingsManager.setValue(UNFINISHED_FIGHT, getViewModel().fightId);
        getViewModel().reset();
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
        switch (messageId) {
            case PAUSE_START_MESSAGE:
                getViewModel().performPause();
                break;
            case RESET_MESSAGE:
                getViewModel().core.vibr();
//        DataManager.instance().saveFight(Helper.convertFightDataToInput(fightData), new DataManager.RequestListener<SaveFightResult>() {
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
                getViewModel().fightId = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                SettingsManager.setValue(UNFINISHED_FIGHT, getViewModel().fightId);
                getViewModel().reset();
                break;
            case EXIT_MESSAGE:
                finishAndRemoveTask();
                System.exit(0);
                break;
            case WIFI_MESSAGE:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            case PAUSE_FINISH_MESSAGE:
                getViewModel().pauseBreak();
                break;
            case PAUSE_CALL_MAIN_MESSAGE:
                getViewModel().performPause();
                break;
            case REMOVE_FIGHT_MESSAGE:
                getViewModel().fightRemove();
                break;
            case END_FIGHT_MESSAGE:
                getViewModel().onFightFinishBegin();
                break;
        }
    }

    @Override
    public void onConfirmDeclined(int messageId) {
        if (messageId == 424) {
            getViewModel().isSM01alive.set(true);
        } else if (messageId == 7585) {
            finishAndRemoveTask();
            System.exit(0);
        } else if (messageId == PAUSE_START_MESSAGE) {
            getViewModel().pauseBreak();
        }
    }

    @Override
    public void onMessageDialogDismissed(int messageId) {
        if (messageId == 145965) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
//        else if (messageId == FIGHT_FINISHED_OK) {
//            getViewModel().isSemiInWork.set(false);
//        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (getViewModel().timerState.get() == TIMER_STATE_IN_PROGRESS) {
                        getViewModel().onTimerStopClick();
                    } else {
                        getViewModel().onTimerStartClick();
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}