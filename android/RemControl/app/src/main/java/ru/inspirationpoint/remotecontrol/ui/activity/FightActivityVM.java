package ru.inspirationpoint.remotecontrol.ui.activity;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import kotlin.text.Charsets;
import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.EthernetApplyFightCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.EthernetNextCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.EthernetPrevCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.FinishFightCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.PlayerCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.StartTimerCommand;
import ru.inspirationpoint.inspirationrc.manager.coreObjects.Device;
import ru.inspirationpoint.inspirationrc.manager.handlers.CoreHandler;
import ru.inspirationpoint.inspirationrc.manager.handlers.EthernetCommandsHelpers.FightFinishAskHandler;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.rc.manager.ActionUploadCallback;
import ru.inspirationpoint.inspirationrc.rc.manager.Camera;
import ru.inspirationpoint.inspirationrc.rc.manager.FightersAutoComplConfig;
import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightActionData;
import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightData;
import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FighterData;
import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FullFightInfo;
import ru.inspirationpoint.inspirationrc.rc.manager.helpers.BackupHelper;
import ru.inspirationpoint.inspirationrc.rc.manager.helpers.JSONHelper;
import ru.inspirationpoint.inspirationrc.rc.ui.adapter.FightersAutoCompleteAdapter;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.ConfirmationDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.FightApplyDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.FightCantEndDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.FightFinishAskDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.FightFinishedDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.FightRestoreDialog;
import ru.inspirationpoint.inspirationrc.tcpHandle.CommandHelper;
import server.schemas.requests.FightAction;
import server.schemas.responses.ListUser;
import server.schemas.responses.SaveFightResult;

import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.TIMER_START_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.TIMER_STOP_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UNFINISHED_FIGHT;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.CARD_P_STATUS_BLACK;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.CARD_P_STATUS_NONE;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.CARD_P_STATUS_RED;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.CARD_P_STATUS_YELLOW;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.CARD_STATUS_BLACK;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.CARD_STATUS_NONE;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.CARD_STATUS_RED;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.CARD_STATUS_YELLOW;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.DISP_RECEIVE_CMD;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.ETH_ACK;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.ETH_NAK;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.ETH_SEMI_ACTIVE;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.FLAG_TCP_CMD;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.PASSIVE_MAX;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.PASSIVE_SHOW;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.PAUSE_FINISHED;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.PLAYER_PAUSE;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.PLAYER_START;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.PLAYER_TCP_CMD;
import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.SETTIMER_TCP_CMD;


public class FightActivityVM extends ActivityViewModel<FightActivity> implements CoreHandler.CoreServerCallback {

    public static final int COLOR_USUAL = R.color.white;
    public static final int COLOR_SELECTED = R.color.textColorSecondary;

    //TODO fix timer color as weapon
    public static final int COLOR_RED = R.color.redCard;
    public static final int COLOR_YELLOW = R.color.yellowCard;

    public static final int DEC_USUAL = R.drawable.minus_white;
    public static final int DEC_SELECTED = R.drawable.minus_blue;
    public static final int INC_USUAL = R.drawable.plus_white;
    public static final int INC_SELECTED = R.drawable.plus_blue;

    public static final int SCREEN_MAIN = 0;
    public static final int SCREEN_P_CARDS_LAY = 2;
    public static final int SCREEN_PASSIVE_LAY = 3;
    public static final int SCREEN_CARDS_LAY = 4;
    public static final int SCREEN_PRIORITY_LAY = 5;
    public static final int SCREEN_SCORE_LAY = 6;
    public static final int SCREEN_TIMER_LAY = 7;
    public static final int SCREEN_NAMES_LAY = 8;
    public static final int SCREEN_MENU_CARDS_PRIO = 9;
    public static final int SCREEN_MENU_SETTINGS = 10;
    public static final int SCREEN_MENU_FIGHT_ACTIONS = 11;
    public static final int SCREEN_PERIOD_LAY = 12;
    public static final int SCREEN_WEAPON_LAY = 13;
    public static final int SCREEN_SYNC_LAY = 14;
    public static final int SCREEN_DEF_TIME_LAY = 15;
    public static final int SCREEN_STATE_WAITING = 16;
    public static final int SCREEN_STATE_OPTIONS = 17;
    public static final int SCREEN_STATE_VIDEO = 18;
    public static final int SCREEN_COMPETITION_LAY = 19;
    public static final int SCREEN_REPLAYS_LAY = 20;

    public static final int TIMER_MODE_MAIN = 80;
    public static final int TIMER_MODE_PAUSE = 81;
    public static final int TIMER_MODE_MEDICINE = 82;

    public static final int TIMER_STATE_NOT_STARTED = 50;
    public static final int TIMER_STATE_IN_PROGRESS = 51;
    public static final int TIMER_STATE_PAUSED = 52;

    public static final int CARD_STATE_NONE = 20;
    public static final int CARD_STATE_YELLOW = 21;
    public static final int CARD_STATE_RED = 22;
    public static final int CARD_STATE_BLACK = 23;

    public static final int WEAPON_FOIL = 30;
    public static final int WEAPON_EPEE = 31;
    public static final int WEAPON_SABER = 32;

    public static final int SYNC_STATE_NONE = 40;
    public static final int SYNC_STATE_SYNCING = 41;
    public static final int SYNC_STATE_SYNCED = 42;

    public static final int PERSON_TYPE_NONE = 0;
    public static final int PERSON_TYPE_LEFT = 1;
    public static final int PERSON_TYPE_RIGHT = 2;


    public ObservableInt screenState = new ObservableInt(SCREEN_MAIN);
    public ObservableInt timerMode = new ObservableInt(TIMER_MODE_MAIN);
    public ObservableInt timerState = new ObservableInt(TIMER_STATE_NOT_STARTED);
    public ObservableField<String> timeToDisplay = new ObservableField<>("03:00");
    public ObservableInt defaultTime = new ObservableInt(180000);
    public ObservableInt timeMillisecs = new ObservableInt(defaultTime.get());
    public ObservableInt timerColor = new ObservableInt(COLOR_USUAL);
    public ObservableInt leftPCard = new ObservableInt(CARD_STATE_NONE);
    public ObservableInt rightPCard = new ObservableInt(CARD_STATE_NONE);
    public ObservableInt leftCard = new ObservableInt(CARD_STATE_NONE);
    public ObservableInt rightCard = new ObservableInt(CARD_STATE_NONE);
    public ObservableInt leftScore = new ObservableInt(0);
    public ObservableInt rightScore = new ObservableInt(0);
    public ObservableInt timerMinDec = new ObservableInt(0);
    public ObservableInt timerMinUnit = new ObservableInt(0);
    public ObservableInt timerSecDec = new ObservableInt(0);
    public ObservableInt timerSecUnit = new ObservableInt(0);
    public ObservableInt period = new ObservableInt(1);
    public ObservableInt weapon = new ObservableInt(WEAPON_FOIL);
    public ObservableInt syncState = new ObservableInt(SYNC_STATE_NONE);
    public ObservableInt priority = new ObservableInt(PERSON_TYPE_NONE);
    public ObservableInt timerDefMinDec = new ObservableInt(0);
    public ObservableInt timerDefMinUnit = new ObservableInt(3);
    public ObservableInt timerDefSecDec = new ObservableInt(0);
    public ObservableInt timerDefSecUnit = new ObservableInt(0);
    public ObservableBoolean isVideo = new ObservableBoolean(false);
    public ObservableBoolean isPhoto = new ObservableBoolean(false);
    public ObservableBoolean isPassive = new ObservableBoolean(true);
    public ObservableBoolean isCountry = new ObservableBoolean(false);
    public ObservableInt videosLeft = new ObservableInt(2);
    public ObservableInt videosRight = new ObservableInt(2);
    public ObservableBoolean isPassiveBtnVisible = new ObservableBoolean(false);
    public ObservableBoolean isVideoBtnVisible = new ObservableBoolean(false);

    public ObservableBoolean isFightFinishPassed = new ObservableBoolean(false);


    public String leftName;
    public String rightName;
    private boolean timerStateChanged = false;
    public FightData fightData;
    private String competition = "";

    public String fightId;

    public ObservableBoolean withSEMI = new ObservableBoolean(false);

    private Handler uiHandler = new Handler();
    public CoreHandler core;
    private CodeScanner mCodeScanner;
    private BackupHelper backupHelper = new BackupHelper(getActivity());

    public final FightersAutoComplConfig configLeft = new FightersAutoComplConfig();
    public final FightersAutoComplConfig configRight = new FightersAutoComplConfig();
    public boolean goToNewScreen = false;

    private long lastScoreTS = 0;
    private long lastTimerTS = 0;
    private String lastScoreActionId = "";
    public ObservableBoolean phrasesEnabled = new ObservableBoolean(false);
    private boolean isLastAndOutdated = false;

    private int lastTimerBeforePause = 0;

    private FightFinishAskHandler fightFinishAskHandler;

    public boolean isPeriodFinished = false;

    public ObservableBoolean isVideoReady = new ObservableBoolean(false);

    public ObservableInt videoSpeed = new ObservableInt(4);
    public ObservableInt videoProgress = new ObservableInt(0);

    public FightActivityVM(FightActivity activity) {
        super(activity);
//        if (DataManager.instance().getCurrentFight() != null) {
//            fightData = DataManager.instance().getCurrentFight();
//        } else {
            fightData = new FightData("", new Date(), new FighterData("", leftName), new FighterData("", rightName),
                    "", SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
            fightData.setmStartTime(System.currentTimeMillis());
            fightData.setmCurrentTime(defaultTime.get());
            fightData.setmCurrentPeriod(period.get());
//        }
//        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        CodeScannerView scannerView = getActivity().getBinding().syncLay.scannerView;
        core = InspirationDayApplication.getApplication().getCoreHandler();
        core.setActivity(getActivity());
        core.setServerCallback(this);
        fightFinishAskHandler = new FightFinishAskHandler(core);
//        withSEMI.set(getActivity().getIntent().getBooleanExtra("SEMI", false));
        if (!withSEMI.get()) {
            leftName = getActivity().getIntent().getStringExtra("LEFT");
            rightName = getActivity().getIntent().getStringExtra("RIGHT");
        }
        phrasesEnabled.set(getActivity().getIntent().getBooleanExtra("PHRASES", false));
//        mCodeScanner = new CodeScanner(getActivity(), scannerView);
//        mCodeScanner.setDecodeCallback(result -> {
//            mCodeScanner.stopPreview();
//            mCodeScanner.releaseResources();
//            syncState.set(SYNC_STATE_SYNCING);
//            core.keepAliveDirectServer = true;
////            core.startCroup(result.getText().substring(result.getText().length() - 5));
//            SettingsManager.setValue(CommonConstants.GROUP_ADDRESS, result.getText().substring(result.getText().length() - 5));
//        });
        isVideo.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                getActivity().getBinding().videoLay.videoChange.setText(isVideo.get() ? R.string.dont_show : R.string.show);
            }
        });
        screenState.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                switch (screenState.get()) {
                    case SCREEN_SYNC_LAY:
                        mCodeScanner.startPreview();
                        break;
                    case SCREEN_SCORE_LAY:
                        getActivity().getBinding().scoreLay.scoreLeft.setValue(leftScore.get());
                        getActivity().getBinding().scoreLay.scoreRight.setValue(rightScore.get());
                        break;
                    case SCREEN_TIMER_LAY:
                        getActivity().getBinding().timerLay.timerDecMin.setValue(timerMinDec.get());
                        getActivity().getBinding().timerLay.timerUnitMin.setValue(timerMinUnit.get());
                        getActivity().getBinding().timerLay.timerDecSec.setValue(timerSecDec.get());
                        getActivity().getBinding().timerLay.timerUnitSec.setValue(timerSecUnit.get());
                        break;
                    case SCREEN_DEF_TIME_LAY:
                        getActivity().getBinding().defTimeLay.defTimeDecMin.setValue(timerDefMinDec.get());
                        getActivity().getBinding().defTimeLay.defTimeUnitMin.setValue(timerDefMinUnit.get());
                        getActivity().getBinding().defTimeLay.defTimeDecSec.setValue(timerDefSecDec.get());
                        getActivity().getBinding().defTimeLay.defTimeUnitSec.setValue(timerDefSecUnit.get());
                        break;
                    case SCREEN_PERIOD_LAY:
                        getActivity().getBinding().periodLay.periodNp.setValue(period.get());
                        break;
                    case SCREEN_STATE_WAITING:
                        core.sendToSM(CommandHelper.ethStart());
                        break;
                }
            }
        });
        defaultTime.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.sendToSM(CommandHelper.setDefTimer(defaultTime.get()));
            }
        });
        timerMode.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                switch (timerMode.get()) {
                    case TIMER_MODE_MAIN:
                        timerColor.set(COLOR_USUAL);
                        break;
                    case TIMER_MODE_MEDICINE:
                        timerColor.set(COLOR_RED);
                        break;
                    case TIMER_MODE_PAUSE:
                        timerColor.set(COLOR_YELLOW);
                        fightData.addAction(FightActionData.createSetPause(timeMillisecs.get(),
                                period.get(), System.currentTimeMillis()), null, null);
                        backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
                        break;
                }
            }
        });
        leftScore.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (leftScore.get() < 0) {
                    leftScore.set(0);
                }
                core.sendToSM(CommandHelper.setScore(PERSON_TYPE_LEFT, leftScore.get()));
                fightData.addAction(FightActionData.createSetScoreLeft(timeMillisecs.get(),
                        period.get(), FightActionData.Fighter.Left, leftScore.get(),
                        System.currentTimeMillis(), 14), new ActionUploadCallback() {
                    @Override
                    public void onUpload(FightAction action) {
                        if (phrasesEnabled.get()) {
                            lastScoreActionId = action._id;
                            isLastAndOutdated = true;
                        }
                    }

                    @Override
                    public void onRefresh(String id) {

                    }
                }, null);
                backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
            }
        });
        rightScore.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (rightScore.get() < 0) {
                    rightScore.set(0);
                }
                core.sendToSM(CommandHelper.setScore(PERSON_TYPE_RIGHT, rightScore.get()));
                fightData.addAction(FightActionData.createSetScoreRight(timeMillisecs.get(),
                        period.get(), FightActionData.Fighter.Right, rightScore.get(),
                        System.currentTimeMillis(), 14), new ActionUploadCallback() {
                    @Override
                    public void onUpload(FightAction action) {
                        if (phrasesEnabled.get()) {
                            lastScoreActionId = action._id;
                            isLastAndOutdated = true;
                        }
                    }

                    @Override
                    public void onRefresh(String id) {

                    }
                }, null);
                backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
            }
        });
        period.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                timerMode.set(TIMER_MODE_MAIN);
                core.sendToSM(CommandHelper.setPeriod(period.get()));
                core.sendToSM(CommandHelper.setTimer(defaultTime.get(), timerMode.get() - 80));
                fightData.addAction(FightActionData.createSetPeriod(timeMillisecs.get(),
                        period.get(), System.currentTimeMillis()), null, null);
                backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
                isPeriodFinished = false;
            }
        });
        syncState.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
//                if (syncState.get() == SYNC_STATE_SYNCED) {
//                    if (withSEMI.get()) {
//                        screenState.set(SCREEN_STATE_WAITING);
//                    } else {
                    screenState.set(SCREEN_MAIN);
                    core.sendToSM(CommandHelper.setName(CommandsContract.PERSON_TYPE_LEFT, leftName));
                    core.sendToSM(CommandHelper.setName(CommandsContract.PERSON_TYPE_RIGHT, rightName));
                    //TODO create fight info, set names
                    if (!TextUtils.isEmpty(SettingsManager.getValue(CommonConstants.UNFINISHED_FIGHT, ""))) {
                        FullFightInfo infoToRestore = JSONHelper.importLastFightFromJSON(getActivity(),
                                SettingsManager.getValue(CommonConstants.UNFINISHED_FIGHT, ""));
                        if (infoToRestore != null) {
                            if (infoToRestore.getFightData() != null)
                                if (infoToRestore.getFightData().getLeftFighter().getScore() != 0 ||
                                        infoToRestore.getFightData().getRightFighter().getScore() != 0) {
                                    FightRestoreDialog.show(getActivity(), infoToRestore);
                                }
                        }
                    } else {
//                        if (DataManager.instance().getCurrentFight() != null) {
//                            fightData = DataManager.instance().getCurrentFight();
//                        } else {
                            fightData = new FightData("", new Date(), new FighterData("", leftName), new FighterData("", rightName),
                                    "", SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
                            fightData.setmStartTime(System.currentTimeMillis());
                            fightData.setmCurrentTime(defaultTime.get());
                            fightData.setmCurrentPeriod(period.get());
//                        }
                    }
                    fightId = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                    SettingsManager.setValue(UNFINISHED_FIGHT, fightId);
                    saveFightData();
//                    }
//                } else {
////                    screenState.set(SCREEN_SYNC_LAY);
//                    //TODO notify user about no sync state
//                }
            }
        });
        leftCard.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                switch (leftCard.get()) {
                    case CARD_STATE_NONE:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, CARD_STATUS_NONE));
                        break;
                    case CARD_STATE_YELLOW:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, CARD_STATUS_YELLOW));
                        break;
                    case CARD_STATE_RED:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, CARD_STATUS_RED));
                        break;
                    case CARD_STATE_BLACK:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, CARD_STATUS_BLACK));
                        break;
                }
                if (leftCard.get() != CARD_STATE_NONE) {
                    fightData.addAction(FightActionData.createSetCardLeft(timeMillisecs.get(),
                            period.get(), FightActionData.Fighter.Left, leftScore.get(), leftCard.get() == CARD_STATE_YELLOW,
                            System.currentTimeMillis()), null, null);
                    backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
                }
            }
        });
        leftPCard.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                switch (leftPCard.get()) {
                    case CARD_STATE_NONE:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, CARD_P_STATUS_NONE));
                        break;
                    case CARD_STATE_YELLOW:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, CARD_P_STATUS_YELLOW));
                        break;
                    case CARD_STATE_RED:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, CARD_P_STATUS_RED));
                        break;
                    case CARD_STATE_BLACK:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, CARD_P_STATUS_BLACK));
                        break;
                }
            }
        });
        rightCard.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                switch (rightCard.get()) {
                    case CARD_STATE_NONE:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, CARD_STATUS_NONE));
                        break;
                    case CARD_STATE_YELLOW:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, CARD_STATUS_YELLOW));
                        break;
                    case CARD_STATE_RED:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, CARD_STATUS_RED));
                        break;
                    case CARD_STATE_BLACK:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, CARD_STATUS_BLACK));
                        break;
                }
                if (leftCard.get() != CARD_STATE_NONE) {
                    fightData.addAction(FightActionData.createSetCardRight(timeMillisecs.get(),
                            period.get(), FightActionData.Fighter.Right, rightScore.get(), rightCard.get() == CARD_STATE_YELLOW,
                            System.currentTimeMillis()), null, null);
                    backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
                }
            }
        });
        rightPCard.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                switch (rightPCard.get()) {
                    case CARD_STATE_NONE:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, CARD_P_STATUS_NONE));
                        break;
                    case CARD_STATE_YELLOW:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, CARD_P_STATUS_YELLOW));
                        break;
                    case CARD_STATE_RED:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, CARD_P_STATUS_RED));
                        break;
                    case CARD_STATE_BLACK:
                        core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, CARD_P_STATUS_BLACK));
                        break;
                }
            }
        });
        timeMillisecs.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                convertMStoDigits(timeMillisecs.get());
                fightData.setmCurrentTime(timeMillisecs.get());
                backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
            }
        });
        priority.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.sendToSM(CommandHelper.setPriority(priority.get()));
                if (priority.get() == PERSON_TYPE_LEFT) {
                    fightData.addAction(FightActionData.createSetPriorityLeft(timeMillisecs.get(),
                            period.get(), FightActionData.Fighter.Left,
                            System.currentTimeMillis()), null, null);
                } else if (priority.get() == PERSON_TYPE_RIGHT) {
                    if (leftCard.get() != CARD_STATE_NONE) {
                        fightData.addAction(FightActionData.createSetPriorityRight(timeMillisecs.get(),
                                period.get(), FightActionData.Fighter.Right,
                                System.currentTimeMillis()), null, null);
                    }
                }
                backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
            }
        });
        getActivity().getBinding().scoreLay.scoreLeft.setValue(leftScore.get());
        getActivity().getBinding().scoreLay.scoreLeft.setOnValueChangedListener((picker, oldVal, newVal) ->
                leftScore.set(newVal));
        getActivity().getBinding().scoreLay.scoreRight.setValue(rightScore.get());
        getActivity().getBinding().scoreLay.scoreRight.setOnValueChangedListener((picker, oldVal, newVal) ->
                rightScore.set(newVal));
        getActivity().getBinding().timerLay.timerDecMin.setValue(timerMinDec.get());
        getActivity().getBinding().timerLay.timerDecMin.setOnValueChangedListener((picker, oldVal, newVal) -> {
            timerMinDec.set(newVal);
            timeNotifySM02();
        });
        getActivity().getBinding().timerLay.timerUnitMin.setValue(timerMinUnit.get());
        getActivity().getBinding().timerLay.timerUnitMin.setOnValueChangedListener((picker, oldVal, newVal) -> {
            timerMinUnit.set(newVal);
            timeNotifySM02();
        });
        getActivity().getBinding().timerLay.timerDecSec.setValue(timerSecDec.get());
        getActivity().getBinding().timerLay.timerDecSec.setOnValueChangedListener((picker, oldVal, newVal) -> {
            timerSecDec.set(newVal);
            timeNotifySM02();
        });
        getActivity().getBinding().timerLay.timerUnitSec.setValue(timerSecUnit.get());
        getActivity().getBinding().timerLay.timerUnitSec.setOnValueChangedListener((picker, oldVal, newVal) -> {
            timerSecUnit.set(newVal);
            timeNotifySM02();
        });
        getActivity().getBinding().defTimeLay.defTimeDecMin.setValue(timerDefMinDec.get());
        getActivity().getBinding().defTimeLay.defTimeDecMin.setOnValueChangedListener((picker, oldVal, newVal) -> {
            defaultTime.set(((timerDefMinDec.get() * 10 + timerDefMinUnit.get()) * 60 +
                    (timerDefSecDec.get() * 10 + timerDefSecUnit.get())) * 1000);
            timerDefMinDec.set(newVal);
        });

        getActivity().getBinding().defTimeLay.defTimeUnitMin.setValue(timerDefMinUnit.get());
        getActivity().getBinding().defTimeLay.defTimeUnitMin.setOnValueChangedListener((picker, oldVal, newVal) -> {
            defaultTime.set(((timerDefMinDec.get() * 10 + timerDefMinUnit.get()) * 60 +
                    (timerDefSecDec.get() * 10 + timerDefSecUnit.get())) * 1000);
            timerDefMinUnit.set(newVal);
        });
        getActivity().getBinding().defTimeLay.defTimeDecSec.setValue(timerDefSecDec.get());
        getActivity().getBinding().defTimeLay.defTimeDecSec.setOnValueChangedListener((picker, oldVal, newVal) -> {
            defaultTime.set(((timerDefMinDec.get() * 10 + timerDefMinUnit.get()) * 60 +
                    (timerDefSecDec.get() * 10 + timerDefSecUnit.get())) * 1000);
            timerDefSecDec.set(newVal);
        });
        getActivity().getBinding().defTimeLay.defTimeUnitSec.setValue(timerDefSecUnit.get());
        getActivity().getBinding().defTimeLay.defTimeUnitSec.setOnValueChangedListener((picker, oldVal, newVal) -> {
            defaultTime.set(((timerDefMinDec.get() * 10 + timerDefMinUnit.get()) * 60 +
                    (timerDefSecDec.get() * 10 + timerDefSecUnit.get())) * 1000);
            timerDefSecUnit.set(newVal);
        });
        getActivity().getBinding().periodLay.periodNp.setValue(period.get());
        getActivity().getBinding().periodLay.periodNp.setOnValueChangedListener((picker, oldVal, newVal) ->
                period.set(newVal));
        getActivity().getBinding().videoLay.videoLeft.setOnValueChangedListener((picker, oldVal, newVal) -> {
            videosLeft.set(newVal);
            core.changeVideoCounters(videosLeft.get(), videosRight.get());
        });
        getActivity().getBinding().videoLay.videoRight.setOnValueChangedListener((picker, oldVal, newVal) -> {
            videosRight.set(newVal);
            core.changeVideoCounters(videosLeft.get(), videosRight.get());
        });
        getActivity().getBinding().competLay.competName.setText(SettingsManager.getValue("COMPETITION", ""));
        getActivity().getBinding().competLay.competName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                core.sendToSM(CommandHelper.setCompetition(s.toString()));
                SettingsManager.setValue("COMPETITION", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        videoProgress.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                core.sendToSM(CommandHelper.notifyPlayer(PLAYER_PAUSE, videoProgress.get()));
            }
        });
        videoSpeed.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                core.sendToSM(CommandHelper.notifyPlayer(PLAYER_START, videoSpeed.get()));
            }
        });
        getActivity().getBinding().playerLay.seekSb.setProgress(4);
        getActivity().getBinding().playerLay.seekSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                videoProgress.set(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        getActivity().getBinding().playerLay.speedSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                videoSpeed.set(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        configLeft.setIndicator(activity.findViewById(R.id.left_fighter_progress_bar));
        configLeft.setAdapter(new FightersAutoCompleteAdapter(activity, true));
        configLeft.setListener((adapterView, view, position, id) -> {
            ListUser listUser = (ListUser) adapterView.getItemAtPosition(position);
            fightData.setmLeftFighterData(new FighterData(listUser._id, listUser.name));
            activity.getBinding().namesLay.leftFighter.setText(listUser.name);
            configRight.getAdapter().setExcludeUser(listUser);
            fightData.setId(SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, ""));
            saveFightData();
        });
        configLeft.setWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                configRight.getAdapter().setExcludeUser(null);
                leftName = charSequence.toString();
                core.sendToSM(CommandHelper.setName(CommandsContract.PERSON_TYPE_LEFT, leftName));
                fightData.getLeftFighter().setName(leftName);
                saveFightData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        configRight.setIndicator(activity.findViewById(R.id.right_fighter_progress_bar));
        configRight.setAdapter(new FightersAutoCompleteAdapter(activity, true));
        configRight.setListener((adapterView, view, position, id) -> {
            ListUser listUser = (ListUser) adapterView.getItemAtPosition(position);
            activity.getBinding().namesLay.rightFighter.setText(listUser.name);
            fightData.setmRightFighterData(new FighterData(listUser._id, listUser.name));
            configLeft.getAdapter().setExcludeUser(listUser);
            fightData.setId(SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, ""));
            saveFightData();
        });
        configRight.setWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                configLeft.getAdapter().setExcludeUser(null);
                rightName = charSequence.toString();
                fightData.getRightFighter().setName(rightName);
                core.sendToSM(CommandHelper.setName(CommandsContract.PERSON_TYPE_RIGHT, rightName));
                saveFightData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        switch (screenState.get()) {
            case SCREEN_SYNC_LAY:
                mCodeScanner.startPreview();
                break;
            case SCREEN_SCORE_LAY:
                getActivity().getBinding().scoreLay.scoreLeft.setValue(leftScore.get());
                getActivity().getBinding().scoreLay.scoreRight.setValue(rightScore.get());
                break;
            case SCREEN_TIMER_LAY:
                getActivity().getBinding().timerLay.timerDecMin.setValue(timerMinDec.get());
                getActivity().getBinding().timerLay.timerUnitMin.setValue(timerMinUnit.get());
                getActivity().getBinding().timerLay.timerDecSec.setValue(timerSecDec.get());
                getActivity().getBinding().timerLay.timerUnitSec.setValue(timerSecUnit.get());
                break;
            case SCREEN_PERIOD_LAY:
                getActivity().getBinding().periodLay.periodNp.setValue(period.get());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (core.getConnectedSm() != null) {
            syncState.set(SYNC_STATE_SYNCED);
        } else {
            if (!TextUtils.isEmpty(SettingsManager.getValue(CommonConstants.GROUP_ADDRESS, ""))) {
                syncState.set(SYNC_STATE_SYNCING);
            }
        }
        if (syncState.get() == SYNC_STATE_SYNCED) {
            if (!TextUtils.isEmpty(SettingsManager.getValue(CommonConstants.UNFINISHED_FIGHT, ""))) {
                FullFightInfo info = JSONHelper.importLastFightFromJSON(getActivity(),
                        SettingsManager.getValue(CommonConstants.UNFINISHED_FIGHT, ""));
                if (info != null) {
                    if (info.getFightData().getLeftFighter().getScore() != 0 ||
                            info.getFightData().getRightFighter().getScore() != 0)
                        FightRestoreDialog.show(getActivity(), info);
                }
            } else {
                SettingsManager.setValue(UNFINISHED_FIGHT, fightId);
                backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
            }
        }

//        core.sendToCams(TIMER_START_UDP);
    }

    public void restoreFromExisted(FullFightInfo info) {
        fightId = SettingsManager.getValue(UNFINISHED_FIGHT, "");
        fightData = info.getFightData();
        leftScore.set(fightData.getLeftFighter().getScore());
        rightScore.set(fightData.getRightFighter().getScore());
        period.set(fightData.getmCurrentPeriod());
        core.sendToSM(CommandHelper.setPeriod(period.get()));
        rightName = fightData.getRightFighter().getName();
        core.sendToSM(CommandHelper.setName(CommandsContract.PERSON_TYPE_RIGHT, rightName));
        leftName = fightData.getLeftFighter().getName();
        core.sendToSM(CommandHelper.setName(CommandsContract.PERSON_TYPE_LEFT, leftName));
        timeMillisecs.set((int) fightData.getmCurrentTime());
        timeNotifySM02();
        if (fightData.getLeftFighter().getCard() == CommonConstants.CardStatus.CardStatus_Yellow) {
            leftCard.set(CARD_STATE_YELLOW);
        } else if (fightData.getLeftFighter().getCard() == CommonConstants.CardStatus.CardStatus_Red) {
            leftCard.set(CARD_STATE_RED);
        }
        if (fightData.getRightFighter().getCard() == CommonConstants.CardStatus.CardStatus_Yellow) {
            rightCard.set(CARD_STATE_YELLOW);
        } else if (fightData.getRightFighter().getCard() == CommonConstants.CardStatus.CardStatus_Red) {
            rightCard.set(CARD_STATE_RED);
        }
    }

    private void convertMStoDigits(int milliseconds) {
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        timerSecUnit.set(seconds % 10);
        timerMinUnit.set(minutes % 10);
        timerSecDec.set(seconds / 10);
        timerMinDec.set(minutes / 10);
        timeToDisplay.set(String.valueOf(timerMinDec.get()) + String.valueOf(timerMinUnit.get()) + ":" +
                String.valueOf(timerSecDec.get()) + String.valueOf(timerSecUnit.get()));
    }

    private void timeNotifySM02() {
        int timeToSend = ((timerMinDec.get() * 10 + timerMinUnit.get()) * 60 + (timerSecDec.get() * 10 + timerSecUnit.get())) * 1000;
        core.sendToSM(CommandHelper.setTimer(timeToSend, timerMode.get() - 80));
    }

    //View section

    public void onTimerStopClick() {
        if (System.currentTimeMillis() - lastTimerTS > 700) {
            if (timerState.get() == TIMER_STATE_IN_PROGRESS) {
                String filename = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "") + "_" +
                        timeToDisplay.get() + "_" + String.valueOf(period.get());
//                core.sendToCams(TIMER_STOP_UDP + "\0" + filename);
                isVideoReady.set(false);
                isVideoBtnVisible.set(true);
            }
            core.vibr();
            //TODO check
//            timerState.set(TIMER_STATE_PAUSED);
//            getActivity().getBinding().titleMain.setVisibility(View.VISIBLE);
            core.sendToSM(new StartTimerCommand(0).getBytes());
            if (timerMode.get() != TIMER_MODE_MAIN) {
                timeMillisecs.set(lastTimerBeforePause == 0 ? defaultTime.get() :
                        (timerMode.get() == TIMER_MODE_MEDICINE ? lastTimerBeforePause : defaultTime.get()));
                if (timerMode.get() == TIMER_MODE_PAUSE) {
                    onPeriodNext();
                }
                timerMode.set(TIMER_MODE_MAIN);
                timeNotifySM02();
                lastTimerBeforePause = 0;
            }
            lastTimerTS = System.currentTimeMillis();
        }
    }

    public void onTimerStartClick() {
        if (System.currentTimeMillis() - lastTimerTS > 700) {
            core.vibr();
            //TODO check
//            timerState.set(TIMER_STATE_IN_PROGRESS);
////        timerMode.set(TIMER_MODE_MAIN);
//            getActivity().getBinding().titleMain.setVisibility(View.GONE);
            core.sendToSM(new StartTimerCommand(1).getBytes());
            isPassiveBtnVisible.set(false);
            activity.getBinding().mainContent.mainPassiveBtn.setBackground(activity.getResources().getDrawable(R.drawable.big_btn_border));
            activity.getBinding().mainContent.mainPassiveBtn.setTextColor(activity.getResources().getColor(R.color.white));
            lastTimerTS = System.currentTimeMillis();
        }
    }

    public void onDecLeft() {
        if (System.currentTimeMillis() - lastScoreTS > 1000) {
            core.vibr();
            leftScore.set(leftScore.get() - 1);
            getActivity().getBinding().mainContent.decLeft.setImageResource(DEC_SELECTED);
            uiHandler.postDelayed(() -> getActivity().getBinding().mainContent.decLeft.setImageResource(DEC_USUAL), 1000);
            lastScoreTS = System.currentTimeMillis();
        }
    }

    public void onDecRight() {
        if (System.currentTimeMillis() - lastScoreTS > 1000) {
            core.vibr();
            rightScore.set(rightScore.get() - 1);
            getActivity().getBinding().mainContent.decRight.setImageResource(DEC_SELECTED);
            uiHandler.postDelayed(() -> getActivity().getBinding().mainContent.decRight.setImageResource(DEC_USUAL), 1000);
            lastScoreTS = System.currentTimeMillis();
        }
    }

    public void onIncLeft() {
        if (System.currentTimeMillis() - lastScoreTS > 1000) {
            core.vibr();
            leftScore.set(leftScore.get() + 1);
            getActivity().getBinding().mainContent.incLeft.setImageResource(INC_SELECTED);
            uiHandler.postDelayed(() -> getActivity().getBinding().mainContent.incLeft.setImageResource(INC_USUAL), 1000);
            lastScoreTS = System.currentTimeMillis();
            if (phrasesEnabled.get()) {
                getActivity().showPhraseDialog(true);
                isLastAndOutdated = false;
            }
        }
    }

    public void onIncRight() {
        if (System.currentTimeMillis() - lastScoreTS > 1000) {
            core.vibr();
            rightScore.set(rightScore.get() + 1);
            getActivity().getBinding().mainContent.incRight.setImageResource(INC_SELECTED);
            uiHandler.postDelayed(() -> getActivity().getBinding().mainContent.incRight.setImageResource(INC_USUAL), 1000);
            lastScoreTS = System.currentTimeMillis();
            if (phrasesEnabled.get()) {
                getActivity().showPhraseDialog(false);
                isLastAndOutdated = false;
            }
        }
    }

    public void onSettingsBtn() {
        core.vibr();
        screenState.set(SCREEN_MENU_SETTINGS);
    }

    public void onTPSBtn() {
        core.vibr();
        Log.wtf("ON TPS", "+");
        screenState.set(SCREEN_MENU_FIGHT_ACTIONS);
    }

    public void onPenaltiesPriorityBtn() {
        core.vibr();
        screenState.set(SCREEN_MENU_CARDS_PRIO);
    }

    public void onPBlackLeft() {
        core.vibr();
        leftPCard.set(CARD_STATE_BLACK);
    }

    public void onPBlackRight() {
        core.vibr();
        rightPCard.set(CARD_STATE_BLACK);
    }

    public void onPCancelLeft() {
        core.vibr();
        leftPCard.set(CARD_STATE_NONE);
    }

    public void onPCancelRight() {
        core.vibr();
        rightPCard.set(CARD_STATE_NONE);
    }

    public void onPCardLeft() {
        core.vibr();
        if (leftPCard.get() == CARD_STATE_NONE) {
            leftPCard.set(CARD_STATE_YELLOW);
        } else if (leftPCard.get() == CARD_STATE_YELLOW) {
            leftPCard.set(CARD_STATE_RED);
            rightScore.set(rightScore.get() + 1);
            uiHandler.postDelayed(() -> leftPCard.set(CARD_STATE_YELLOW), 2000);
        }
    }

    public void onPCardRight() {
        core.vibr();
        if (rightPCard.get() == CARD_STATE_NONE) {
            rightPCard.set(CARD_STATE_YELLOW);
        } else if (rightPCard.get() == CARD_STATE_YELLOW) {
            rightPCard.set(CARD_STATE_RED);
            leftScore.set(leftScore.get() + 1);
            uiHandler.postDelayed(() -> rightPCard.set(CARD_STATE_YELLOW), 2000);
        }
    }

    public void onBlackLeft() {
        core.vibr();
        leftCard.set(CARD_STATE_BLACK);
    }

    public void onBlackRight() {
        core.vibr();
        rightCard.set(CARD_STATE_BLACK);
    }

    public void onCancelLeft() {
        core.vibr();
        rightCard.set(CARD_STATE_NONE);
    }

    public void onCancelRight() {
        core.vibr();
        leftCard.set(CARD_STATE_NONE);
    }

    public void onCardLeft() {
        core.vibr();
        if (leftCard.get() == CARD_STATE_NONE) {
            leftCard.set(CARD_STATE_YELLOW);
        } else if (leftCard.get() == CARD_STATE_YELLOW) {
            leftCard.set(CARD_STATE_RED);
            rightScore.set(rightScore.get() + 1);
            uiHandler.postDelayed(() -> leftCard.set(CARD_STATE_YELLOW), 2000);
        }
    }

    public void onCardRight() {
        core.vibr();
        if (rightCard.get() == CARD_STATE_NONE) {
            rightCard.set(CARD_STATE_YELLOW);
        } else if (rightCard.get() == CARD_STATE_YELLOW) {
            rightCard.set(CARD_STATE_RED);
            leftScore.set(leftScore.get() + 1);
            uiHandler.postDelayed(() -> rightCard.set(CARD_STATE_YELLOW), 2000);
        }
    }

    public void onScoreAccepted() {
        onCloseBtn();
    }

    public void onTimerAccepted() {
        onCloseBtn();
    }

    public void onDefTimeAccepted() {
        core.vibr();
        defaultTime.set(((timerDefMinDec.get() * 10 + timerDefMinUnit.get()) * 60 +
                (timerDefSecDec.get() * 10 + timerDefSecUnit.get())) * 1000);
        screenState.set(SCREEN_MAIN);
    }

    public void onMenuPause() {
        core.vibr();
        onTimerStopClick();
        lastTimerBeforePause = timeMillisecs.get();
        timeMillisecs.set(60000);
        timerMode.set(TIMER_MODE_PAUSE);
        timeNotifySM02();
        //TODO check
//        timerState.set(TIMER_STATE_IN_PROGRESS);
//        getActivity().getBinding().titleMain.setVisibility(View.GONE);
        screenState.set(SCREEN_MAIN);
    }

    public void onMenuPeriod() {
        core.vibr();
        screenState.set(SCREEN_PERIOD_LAY);
    }

    public void onMenuScore() {
        core.vibr();
        screenState.set(SCREEN_SCORE_LAY);
    }

    public void onMenuTimer() {
        core.vibr();
        screenState.set(SCREEN_TIMER_LAY);
    }

    public void onMenuPassive() {
//        screenState.set(SCREEN_PASSIVE_LAY);
    }

    public void onMenuMedical() {
        core.vibr();
        lastTimerBeforePause = timeMillisecs.get();
        timeMillisecs.set(300000);
        timerMode.set(TIMER_MODE_MEDICINE);
        timeNotifySM02();
        //TODO check
//        timerState.set(TIMER_STATE_IN_PROGRESS);
//        getActivity().getBinding().titleMain.setVisibility(View.GONE);
        screenState.set(SCREEN_MAIN);
    }

    public void onMenuCards() {
        core.vibr();
        screenState.set(SCREEN_CARDS_LAY);
    }

    public void onMenuPriority() {
        core.vibr();
        screenState.set(SCREEN_PRIORITY_LAY);
    }

    public void onMenuPCards() {
        core.vibr();
        screenState.set(SCREEN_P_CARDS_LAY);
    }

    public void onMenuDeviceReset() {
        core.vibr();
        core.sendToSM(CommandHelper.reset(defaultTime.get()));
        fightData = new FightData("", new Date(), new FighterData("", leftName), new FighterData("", rightName),
                "", SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
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
        fightId = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        SettingsManager.setValue(UNFINISHED_FIGHT, fightId);
        backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
        reset();
    }

    public void reset() {
        fightId = "";
        fightData.setId("");
        SettingsManager.removeValue(CommonConstants.LAST_FIGHT_ID);
        timeToDisplay.set(String.valueOf(timerDefMinDec.get()) + String.valueOf(timerDefMinUnit.get()) + ":" +
                String.valueOf(timerDefSecDec.get()) + String.valueOf(timerDefSecUnit.get()));
        timeMillisecs.set(defaultTime.get());
        timerColor.set(COLOR_USUAL);
        leftPCard.set(CARD_STATE_NONE);
        rightPCard.set(CARD_STATE_NONE);
        leftCard.set(CARD_STATE_NONE);
        rightCard.set(CARD_STATE_NONE);
        leftName = "";
        rightName = "";
        leftScore.set(0);
        rightScore.set(0);
        timerMinDec.set(0);
        timerMinUnit.set(0);
        timerSecDec.set(0);
        timerSecUnit.set(0);
        period.set(1);
        weapon.set(WEAPON_FOIL);
        screenState.set(SCREEN_MAIN);
        priority.set(PERSON_TYPE_NONE);
    }

    public void onMenuWeaponType() {
        core.vibr();
        screenState.set(SCREEN_WEAPON_LAY);
    }

    public void onMenuNames() {
        core.vibr();
        screenState.set(SCREEN_NAMES_LAY);
    }

    public void onMenuDefTime() {
        core.vibr();
        screenState.set(SCREEN_DEF_TIME_LAY);
    }

    public void onMenuVideoReplays() {
        core.vibr();
        screenState.set(SCREEN_STATE_VIDEO);
    }

    public void onPeriodNext() {
        core.vibr();
        period.set(period.get() + 1);
        getActivity().getBinding().periodLay.periodNp.setValue(period.get());
    }

    public void onPeriodConfirmed() {
        onCloseBtn();
    }

    public void onWeaponChanged(int weapon) {
        core.vibr();
        this.weapon.set(weapon);
        core.sendToSM(CommandHelper.setWeapon(weapon - 29));
        screenState.set(SCREEN_MAIN);
    }

    public void onSwapBtn() {
        core.vibr();
        int cardptemp = leftPCard.get();
        leftPCard.set(rightPCard.get());
        rightPCard.set(cardptemp);
        int cardtemp = leftCard.get();
        leftCard.set(rightCard.get());
        rightCard.set(cardtemp);
        int scoretemt = leftScore.get();
        leftScore.set(rightScore.get());
        rightScore.set(scoretemt);
        String nametemp = leftName;
        leftName = rightName;
        rightName = nametemp;
        core.sendToSM(CommandHelper.swap());
        switch (priority.get()) {
            case PERSON_TYPE_NONE:
                break;
            case PERSON_TYPE_LEFT:
                priority.set(PERSON_TYPE_RIGHT);
                break;
            case PERSON_TYPE_RIGHT:
                priority.set(PERSON_TYPE_LEFT);
                break;
        }
        FighterData temp = fightData.getLeftFighter();
        fightData.setmLeftFighterData(fightData.getRightFighter());
        fightData.setmRightFighterData(temp);
        saveFightData();
        screenState.set(SCREEN_MAIN);
    }

    public void onPriority() {
        core.vibr();
        priority.set(new Random().nextInt() % 2 == 0 ? PERSON_TYPE_LEFT : PERSON_TYPE_RIGHT);
    }

    public void onPriorityNone() {
        core.vibr();
        priority.set(PERSON_TYPE_NONE);
    }

    public void onCloseBtn() {
        if (screenState.get() == SCREEN_REPLAYS_LAY) {
            core.sendToSM(CommandHelper.notifyPlayer(CommandsContract.PLAYER_STOP, 0));
        }
        core.vibr();
        screenState.set(SCREEN_MAIN);
        getActivity().getBinding().playerLay.seekSb.setProgress(4);
        getActivity().getBinding().playerLay.speedSb.setProgress(4);
    }

    public void onSyncCancel() {
        core.keepAliveDirectServer = false;
        stopSync();
        SettingsManager.removeValue(CommonConstants.GROUP_ADDRESS);
    }

    public void onSyncDecline() {
        core.vibr();
        Intent intent = new Intent(getActivity(), NewFightActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        goToNewScreen = false;
        getActivity().startActivity(intent);
        SettingsManager.removeValue(CommonConstants.UNFINISHED_FIGHT);
    }

    public void stopSync() {
//        if (withSEMI.get()) {
//            core.sendGOMessage(core.getConnectedSm(), new EthernetStopCommand().getBytes());
//        }
//        core.stopServer();
//        reset();
//        screenState.set(SCREEN_SYNC_LAY);
        syncState.set(SYNC_STATE_NONE);
//        WifiManager wifiManager = (WifiManager)this.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if (wifiManager != null) {
//            try {
//                wifiManager.setWifiEnabled(false);
//                Thread.sleep(50);
//                wifiManager.setWifiEnabled(true);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void onOptionsSelect() {
        core.vibr();
        screenState.set(SCREEN_STATE_OPTIONS);
    }

    public void countryCheck() {
        core.vibr();
        isCountry.set(!isCountry.get());
        core.notifyRCVisibility(isVideo.get(), isPhoto.get(), isPassive.get(), isCountry.get());
    }

    public void photoCheck() {
        core.vibr();
        //TODO
        core.notifyRCVisibility(isVideo.get(), isPhoto.get(), isPassive.get(), isCountry.get());
    }

    public void onMenuCompetition() {
        core.vibr();
        screenState.set(SCREEN_COMPETITION_LAY);
    }

    public void passiveCheck() {
        core.vibr();
        Log.wtf("PASSIVE CHECK", "+");
        isPassive.set(!isPassive.get());
        core.notifyRCVisibility(isVideo.get(), isPhoto.get(), isPassive.get(), isCountry.get());
    }

    public void eventsCheck() {
        core.vibr();
        phrasesEnabled.set(!phrasesEnabled.get());
    }

    public void onOptionsOk() {
        core.vibr();
        screenState.set(SCREEN_MENU_SETTINGS);
    }

    public void onVideoChange() {
        isVideo.set(!isVideo.get());
        core.notifyRCVisibility(isVideo.get(), isPhoto.get(), isPassive.get(), isCountry.get());
    }

    public void onFightEndStart() {
        core.vibr();
        if (leftScore.get() == rightScore.get() &&
                priority.get() == PERSON_TYPE_NONE) {
            FightCantEndDialog.show(getActivity(), withSEMI.get());
        } else {
            FightFinishAskDialog.show(getActivity(), withSEMI.get());
        }
    }

    public void onFightFinishBegin() {
        if (withSEMI.get()) {
            fightFinishAskHandler.start();
        } else {
            fightFinish();
            goToNewFight();
        }
    }

    public void onPassiveLock() {
        core.vibr();
        core.passiveLockChange();
        activity.getBinding().mainContent.mainPassiveBtn.setBackground(activity.getResources().getDrawable(R.drawable.big_btn_border_blue));
        activity.getBinding().mainContent.mainPassiveBtn.setTextColor(activity.getResources().getColor(R.color.textColorSecondary));
    }

    public void onPhraseSelected(int phrase, boolean isLeft) {
        if (isLastAndOutdated) {
            fightData.refreshActionPhrase(lastScoreActionId, null, phrase);
            isLastAndOutdated = false;
        }
    }

    public void onVideoBtn() {
        screenState.set(SCREEN_REPLAYS_LAY);
//        core.sendFileToSm();
    }

    public void onPlayBtn(){
        core.vibr();
        core.sendToSM(CommandHelper.notifyPlayer(PLAYER_START, videoSpeed.get()));
    }

    public void onPauseBtn(){
        core.vibr();
        core.sendToSM(CommandHelper.notifyPlayer(PLAYER_PAUSE, 444));
    }

    private void saveFightData() {
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
        backupHelper.backupLastFight(backupHelper.createBackup(fightData), fightId);
    }

    //Core section

    @Override
    public void messageReceived(byte command, byte[] message) {
        switch (command) {
            case SETTIMER_TCP_CMD:
                byte[] time = new byte[4];
                System.arraycopy(message, 1, time, 0, 4);
//                if (timerState.get() == TIMER_STATE_IN_PROGRESS && message[5] == 1) {
//                    getActivity().runOnUiThread(() -> {
//                        onTimerStopClick();
//                        getActivity().getBinding().titleMain.setVisibility(View.VISIBLE);
////                        getActivity().getBinding().menuBtnMain.setVisibility(View.VISIBLE);
//                    });
//                }
// else if (timerState.get() == TIMER_STATE_PAUSED && message[5] == 0) {
//                    getActivity().runOnUiThread(() -> {
//                        timerState.set(TIMER_STATE_IN_PROGRESS);
//                        getActivity().getBinding().titleMain.setVisibility(View.GONE);
//                        getActivity().getBinding().menuBtnMain.setVisibility(View.GONE);
//                    });
//                }
//                if (message[5] == 0) {
                timeMillisecs.set(ByteBuffer.wrap(time).getInt());
                if (!isPeriodFinished && ByteBuffer.wrap(time).getInt() < 5) {
                    ConfirmationDialog.show(getActivity(), 456, getActivity().getResources().getString(R.string.period_finished),
                            getActivity().getResources().getString(R.string.pause_start_ask));
                    isPeriodFinished = true;
                }
                if (message[5] == 1 && timerState.get() == TIMER_STATE_IN_PROGRESS) {
                    getActivity().runOnUiThread(() -> {
                        timerState.set(TIMER_STATE_PAUSED);
                        getActivity().getBinding().titleMain.setVisibility(View.VISIBLE);
                        isVideoBtnVisible.set(true);
                        String filename = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "") + "_" +
                                timeToDisplay.get() + "_" + String.valueOf(period.get());
//                        core.sendToCams(TIMER_STOP_UDP + "\0" + filename);
                        isVideoReady.set(false);
                    });
                } else if (message[5] == 0 && timerState.get() != TIMER_STATE_IN_PROGRESS) {
                    getActivity().runOnUiThread(() -> {
                        timerState.set(TIMER_STATE_IN_PROGRESS);
                        getActivity().getBinding().titleMain.setVisibility(View.GONE);
                        isPassiveBtnVisible.set(false);
                        activity.getBinding().mainContent.mainPassiveBtn.setBackground(activity.getResources().getDrawable(R.drawable.big_btn_border));
                        activity.getBinding().mainContent.mainPassiveBtn.setTextColor(activity.getResources().getColor(R.color.white));
                    });
                }
//                }
                break;
            case DISP_RECEIVE_CMD:
                Log.wtf("DISP MSG", Arrays.toString(message));
                //TODO create and fill fight data to store it later
                int scoreL = message[0];
                int scoreR = message[1];
                int per = message[2];
                byte[] nameLBuf = new byte[message[5]];
                System.arraycopy(message, 6, nameLBuf, 0, message[5]);
                byte[] nameRBuf = new byte[message[6 + message[5]]];
                System.arraycopy(message, 7 + message[5], nameRBuf, 0, message[6 + message[5]]);
//                byte[] timeBuf = new byte[message[7 + nameLBuf.length + nameRBuf.length]];
//                System.arraycopy(message, 8 + nameLBuf.length + nameRBuf.length, timeBuf, 0, timeBuf.length);
                String lName = new String(nameLBuf, Charsets.UTF_8);
                String rName = new String(nameRBuf, Charsets.UTF_8);
                core.sendToSM(CommandHelper.setName(PERSON_TYPE_LEFT, lName));
                core.sendToSM(CommandHelper.setName(PERSON_TYPE_RIGHT, rName));
                core.sendToSM(CommandHelper.setScore(PERSON_TYPE_LEFT, scoreL));
                core.sendToSM(CommandHelper.setScore(PERSON_TYPE_RIGHT, scoreR));
                screenState.set(SCREEN_MAIN);
                FightApplyDialog.show(getActivity());
                leftScore.set(scoreL);
                rightScore.set(scoreR);
                period.set(per);
                leftName = lName;
                rightName = rName;
                leftCard.set(message[3] + 20);
                rightCard.set(message[4] + 20);
                fightData = new FightData("", new Date(), new FighterData("", leftName), new FighterData("", rightName),
                        "", SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
//                DataManager.instance().setCurrentFight(fightData);
                fightData.setmStartTime(System.currentTimeMillis());
//                String timeStr = new String(timeBuf, Charsets.UTF_8);
//                Log.wtf("RECEIVED TIME", timeStr);
//                int ms = (timeStr.length() == 5 ? Integer.parseInt(String.valueOf(timeStr.charAt(0)))*10*60*1000 : 0) +
//                        Integer.parseInt(String.valueOf(timeStr.charAt(timeStr.length() - 4)))*60*1000+
//                        Integer.parseInt(String.valueOf(timeStr.charAt(timeStr.length() - 2)))*10*1000+
//                        Integer.parseInt(String.valueOf(timeStr.charAt(timeStr.length() - 1)))*1000;
//                convertMStoDigits(ms);
//                DataManager.instance().saveFight(Helper.convertFightDataToInput(fightData), new DataManager.RequestListener<SaveFightResult>() {
//                    @Override
//                    public void onSuccess(SaveFightResult result) {
//                        SettingsManager.setValue(CommonConstants.LAST_FIGHT_ID, result.fight._id);
//                        Log.wtf("Fight ID", result.fight._id);
//                    }
//
//                    @Override
//                    public void onFailed(String error, String message) {
//                        Log.wtf("ERR FIGHT UPL", error + "|" + message);
//                    }
//
//                    @Override
//                    public void onStateChanged(boolean inProgress) {
//                    }
//                });
//                break;
            case ETH_ACK:
                Log.wtf("ACK RECEIVED", "+");
                FightFinishedDialog.show(getActivity());
                if (fightFinishAskHandler != null) {
                    fightFinishAskHandler.finish();
                }
                break;
            case ETH_NAK:
                FightCantEndDialog.show(getActivity(), withSEMI.get());
                if (fightFinishAskHandler != null) {
                    fightFinishAskHandler.finish();
                }
                break;
            case PASSIVE_SHOW:
                //TODO resume to it
//                if (timerMode.get() == TIMER_MODE_MAIN) {
//                    getActivity().runOnUiThread(() -> {
//                        onTimerStopClick();
//                        if (isPassive.get()) {
//                            isPassiveBtnVisible.set(true);
//                        }
//                    });
//                }
                break;

            case PASSIVE_MAX:
                core.vibrContiniously();
                break;
            case ETH_SEMI_ACTIVE:
                withSEMI.set(message[0] == 1);
                break;
            case PAUSE_FINISHED:
                activity.runOnUiThread(() -> {
                    if (timerMode.get() == TIMER_MODE_PAUSE) {
                        onTimerStopClick();
                    } else if (timerMode.get() == TIMER_MODE_MEDICINE) {
                        timeToDisplay.set("00:00");
                    }
                });
                break;
            case FLAG_TCP_CMD:
                String filename = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "") + "_" +
                        timeToDisplay.get() + "_" + String.valueOf(period.get());
//                core.sendToCams(TIMER_STOP_UDP + "\0" + filename);
                isVideoReady.set(false);
                break;
            case PLAYER_TCP_CMD:
                isVideoReady.set(true);
                break;
        }
    }

    private void updateData() {
        //TODO fill data and send it to server if fighters ids != null
        //TODO THINK IF IT NEEDED
        fightData.setmCurrentPeriod(period.get());
    }

    @Override
    public void connectionLost() {
        syncState.set(SYNC_STATE_SYNCING);
    }

    @Override
    public void devicesUpdated(ArrayList<Device> devices) {
        //TODO
    }

    //TODO FILL METHODS

    public void fightApplyOk() {
        core.vibr();
        core.sendToSM(new EthernetApplyFightCommand().getBytes());
    }

    public void fightNext() {
        core.vibr();
        core.sendToSM(new EthernetNextCommand().getBytes());
//        screenState.set(SCREEN_STATE_WAITING);
    }

    public void fightPrev() {
        core.vibr();
        core.sendToSM(new EthernetPrevCommand().getBytes());
    }

    public void fightFinish() {
        onMenuDeviceReset();
        core.sendToSM(new FinishFightCommand().getBytes());
        SettingsManager.removeValue(CommonConstants.UNFINISHED_FIGHT);
    }

    public void goToNewFight() {
        core.vibr();
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        goToNewScreen = true;
        getActivity().startActivity(intent);
        SettingsManager.removeValue(CommonConstants.UNFINISHED_FIGHT);
    }

    //Binders section

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter("android:background")
    public static void setBackground(View view, int resource) {
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

//    @Override
//    public void onDestroy() {
//        if (Build.VERSION.SDK_INT >= 21)
//            getActivity().finishAndRemoveTask();
//        else
//            finish();
//        System.exit(0);
//        super.onDestroy();
//    }
}