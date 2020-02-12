package ru.inspirationpoint.remotecontrol.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.lantouzi.wheelview.WheelView;
import com.shawnlin.numberpicker.NumberPicker;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.ListUser;
import ru.inspirationpoint.remotecontrol.manager.FightersAutoComplConfig;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.EthNextPrevCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.EthernetApplyFightCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.EthernetFinishAskCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.SetBluetoothCommand;
import ru.inspirationpoint.remotecontrol.manager.coreObjects.Device;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FighterData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.UrlEncodeObject;
import ru.inspirationpoint.remotecontrol.manager.handlers.CoreHandler;
import ru.inspirationpoint.remotecontrol.manager.handlers.EthernetCommandsHelpers.EthernetDispHandler;
import ru.inspirationpoint.remotecontrol.manager.handlers.EthernetCommandsHelpers.FightFinishAskHandler;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;
import ru.inspirationpoint.remotecontrol.ui.DividerItemDecoration;
import ru.inspirationpoint.remotecontrol.ui.adapter.FightActionsAdapter;
import ru.inspirationpoint.remotecontrol.ui.adapter.FightersAutoCompleteAdapter;
import ru.inspirationpoint.remotecontrol.ui.dialog.ConfirmationDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightCantEndDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightFinishAskDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightFinishedDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightRestoreDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.MessageDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.SmOffDialog;

import static android.content.Context.WIFI_SERVICE;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.*;
import static ru.inspirationpoint.remotecontrol.manager.constants.ISEMIContract.ETH_SM_STATE_WAITING;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.*;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.EXIT_MESSAGE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.PAUSE_CALL_MAIN_MESSAGE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.PAUSE_FINISH_MESSAGE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.PAUSE_START_MESSAGE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.RESET_MESSAGE;


public class FightActivityVM extends ActivityViewModel<FightActivity> implements CoreHandler.CoreServerCallback {

    public static final int COLOR_USUAL = R.color.white;
    public static final int COLOR_SELECTED = R.color.textColorSecondary;
    public static final int COLOR_RED = R.color.redCard;
    public static final int COLOR_YELLOW = R.color.yellowCard;

    public static final int DEC_USUAL = R.drawable.minus_white;
    public static final int DEC_SELECTED = R.drawable.minus_blue;
    public static final int INC_USUAL = R.drawable.btn_plus;
    public static final int INC_SELECTED = R.drawable.btn_plus_blue;

    public static final int SCREEN_MAIN = 0;
    public static final int SCREEN_B_CARDS_LAY = 2;
    public static final int SCREEN_PASSIVE_LAY = 3;
    public static final int SCREEN_CARDS_LAY = 4;
    public static final int SCREEN_PRIORITY_LAY = 5;
    public static final int SCREEN_SCORE_LAY = 6;
    public static final int SCREEN_TIMER_LAY = 7;
    public static final int SCREEN_NAMES_LAY = 8;
    public static final int SCREEN_MENU_PENALTIES = 9;
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
    public static final int SCREEN_DEF_PASSIVE_TIME_LAY = 21;
    public static final int SCREEN_STATE_CYRANO_CONTROLS = 23;
    public static final int SCREEN_STATE_BLUETOOTH = 24;
    public static final int SCREEN_STATE_PROTOCOL = 26;

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

    public static final int WEAPON_FOIL = 1;
    public static final int WEAPON_EPEE = 2;
    public static final int WEAPON_SABER = 3;

    public static final int SYNC_STATE_NONE = 40;
    public static final int SYNC_STATE_SYNCING = 41;
    public static final int SYNC_STATE_SYNCED = 42;

    public static final int PERSON_TYPE_NONE = 0;

    public static final int BTN_STATE_RESET = 0;
    public static final int BTN_STATE_NEXT = 1;
    public static final int BTN_STATE_END = 2;

    public ObservableInt screenState = new ObservableInt(SCREEN_MAIN);
    public ObservableInt timerMode = new ObservableInt(TIMER_MODE_MAIN);
    public ObservableInt timerState = new ObservableInt(TIMER_STATE_NOT_STARTED);
    public ObservableField<String> timeToDisplay = new ObservableField<>("03:00");
    public ObservableInt defaultTime = new ObservableInt(180000);
    public ObservableInt defaultPassiveTime = new ObservableInt(60000);
    public ObservableInt timeMillisecs = new ObservableInt(defaultTime.get());
    public ObservableInt timerColor = new ObservableInt(COLOR_USUAL);
    public ObservableInt leftPCard = new ObservableInt(CARD_STATE_NONE);
    public ObservableInt rightPCard = new ObservableInt(CARD_STATE_NONE);
    public ObservableInt leftCard = new ObservableInt(CARD_STATE_NONE);
    public ObservableInt rightCard = new ObservableInt(CARD_STATE_NONE);
    public ObservableInt leftScore = new ObservableInt(0);
    public ObservableInt rightScore = new ObservableInt(0);
    public ObservableInt timerMinDec = new ObservableInt(0);
    public ObservableInt timerMinUnit = new ObservableInt(3);
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
    public ObservableInt timerPDefSecHoun = new ObservableInt(0);
    public ObservableInt timerPDefSecDec = new ObservableInt(6);
    public ObservableInt timerPDefSecUnit = new ObservableInt(0);
    public ObservableBoolean isVideo = new ObservableBoolean(false);
    public ObservableBoolean isPhoto = new ObservableBoolean(false);
    public ObservableBoolean isPassive = new ObservableBoolean(true);
    public ObservableBoolean isCountry = new ObservableBoolean(false);
    public ObservableInt videosLeft = new ObservableInt(2);
    public ObservableInt videosRight = new ObservableInt(2);
    public ObservableBoolean isPassiveBtnVisible = new ObservableBoolean(false);
    public ObservableBoolean isVideoBtnVisible = new ObservableBoolean(false);
    public ObservableBoolean isVideoReady = new ObservableBoolean(false);
    public ObservableField<String> dispDataFirst = new ObservableField<>();
    public ObservableField<String> dispDataSecond = new ObservableField<>();
    public ObservableBoolean isServerOnline = new ObservableBoolean(false);
    public ObservableField<String> ethState = new ObservableField<>("W");
    public ObservableInt mainScreenBtnState = new ObservableInt(0);
    private boolean isCamConnected = false;

    public ObservableBoolean isFightFinishPassed = new ObservableBoolean(false);


    public ObservableField<String> leftName = new ObservableField<>("");
    public ObservableField<String> rightName = new ObservableField<>("");
    private boolean timerStateChanged = false;
    private String competition = "";

    public String fightId;

    public ObservableBoolean withSEMI = new ObservableBoolean(false);

    private Handler uiHandler = new Handler();
    public CoreHandler core;
    private CodeScanner mCodeScanner;

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

    public boolean isPeriodFinished = true;

    public ObservableInt videoSpeed = new ObservableInt(4);
    public ObservableInt videoProgress = new ObservableInt(0);

    public ObservableBoolean isSM01alive = new ObservableBoolean(true);
    private SmOffDialog dialog;

    private int backupCounter = 0;

    private EthernetDispHandler dispHandler = new EthernetDispHandler();
    public ObservableBoolean isSemiInWork = new ObservableBoolean(false);

    private FightData dispTempData = null;

    private boolean isFightFinishInProgress = false;

    public ObservableBoolean isNamesLocked = new ObservableBoolean(false);
    public ObservableBoolean isSyncTimedOut = new ObservableBoolean(false);

    private FightActionsAdapter adapter;

    private BroadcastReceiver wifiReceiver;

    private String ethNecessaryInfo = "";

    public ObservableField<String> logTextTemp = new ObservableField<>();

    public FightActivityVM(FightActivity activity) {
        super(activity);
//        if (DataManager.instance().getCurrentFight() != null) {
//            fightData = DataManager.instance().getCurrentFight();
//        } else {
//            fightData = new FightData("", new Date(), new FighterData("", leftName), new FighterData("", rightName),
//                    "", SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
//            fightData.setmStartTime(System.currentTimeMillis());
//            fightData.setmCurrentTime(defaultTime.get());
//            fightData.setmCurrentPeriod(period.get());
//        }
//        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE?";//                | View.SYS bTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        CodeScannerView scannerView = getActivity().getBinding().syncLay.scannerView;
        core = InspirationDayApplication.getApplication().getCoreHandler();
        core.setActivity(getActivity());
        core.setServerCallback(this);
        fightFinishAskHandler = new FightFinishAskHandler(core);
//        withSEMI.set(getActivity().getIntent().getBooleanExtra("SEMI", false));
//        if (!withSEMI.get()) {
//            leftName = getActivity().getIntent().getStringExtra("LEFT");
//            rightName = getActivity().getIntent().getStringExtra("RIGHT");
//        }
//        phrasesEnabled.set(getActivity().getIntent().getBooleanExtra("PHRASES", false));
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        mCodeScanner.setDecodeCallback(result -> {
            if (result.getBarcodeFormat().equals(BarcodeFormat.QR_CODE)) {
                Log.wtf("SCAN", result.getText());
                if (result.getText().contains("access=")) {
                    mCodeScanner.stopPreview();
                    mCodeScanner.releaseResources();
                    syncState.set(SYNC_STATE_SYNCING);
                    Uri uri = Uri.parse(result.getText());
                    String encode = uri.getQueryParameter("access");
                    Gson gson = new Gson();
                    byte[] data = Base64.decode(encode, Base64.DEFAULT);
                    String text = new String(data, StandardCharsets.UTF_8);
                    Log.wtf("RESULT SCAN", text);
                    UrlEncodeObject encodeObject = gson.fromJson(text, UrlEncodeObject.class);
                    if (!encodeObject.getIp().equals("0.0.0.0")) {
                        SettingsManager.setValue(CommonConstants.SM_CODE, Arrays.toString(encodeObject.getCode()).replaceAll("\\[|]|,|\\s", ""));
                        SettingsManager.setValue(SM_IP, encodeObject.getIp());
                        core.startTCP(encodeObject.getIp());
                    } else {
                        onDisconnect();
                    }
                } else {
                    mCodeScanner.stopPreview();
                    mCodeScanner.releaseResources();
                    mCodeScanner.startPreview();
                }
            } else {
                mCodeScanner.stopPreview();
                mCodeScanner.releaseResources();
                mCodeScanner.startPreview();
            }
        });
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
//                    case SCREEN_TIMER_LAY:
//                        getActivity().getBinding().timerLay.timerDecMin.setValue(timerMinDec.get());
//                        getActivity().getBinding().timerLay.timerUnitMin.setValue(timerMinUnit.get());
//                        getActivity().getBinding().timerLay.timerDecSec.setValue(timerSecDec.get());
//                        getActivity().getBinding().timerLay.timerUnitSec.setValue(timerSecUnit.get());
//                        break;
                    case SCREEN_PERIOD_LAY:
                        getActivity().getBinding().periodLay.periodNp.setValue(period.get());
                        break;
                    case SCREEN_STATE_WAITING:
//                        core.sendToSM(CommandHelper.ethStart());
                        break;
                    case SCREEN_MAIN:
                        isVideoReady.set(false);
                        break;
                    case SCREEN_STATE_PROTOCOL:
                        fillProtocol();
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
        defaultPassiveTime.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.sendToSM(CommandHelper.passiveState(isPassive.get(), false, defaultPassiveTime.get()));
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
                        break;
                }
            }
        });
        leftScore.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (leftScore.get() < 0) {
                    leftScore.set(0);
                } else {
                    core.getFightHandler().setScore(timeMillisecs.get(), PERSON_TYPE_LEFT, leftScore.get());
                }
            }
        });
        rightScore.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (rightScore.get() < 0) {
                    rightScore.set(0);
                } else {
                    core.getFightHandler().setScore(timeMillisecs.get(), PERSON_TYPE_RIGHT, rightScore.get());
                }
            }
        });
        period.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                timerMode.set(TIMER_MODE_MAIN);
                core.getFightHandler().setPeriod(timeMillisecs.get(), period.get(), defaultTime.get());
            }
        });
        syncState.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (syncState.get() == SYNC_STATE_SYNCED) {
                    Log.wtf("SYNCED", "+");
//                    if (withSEMI.get()) {
//                        screenState.set(SCREEN_STATE_WAITING);
//                    } else {
                    screenState.set(SCREEN_MAIN);
                    FightData tempFightCache = core.getBackupHelper().getBackup();
                    Gson gson = new Gson();
                    Log.wtf("RESTORED", gson.toJson(tempFightCache));
                    if (tempFightCache != null) {
                        FightRestoreDialog.show(getActivity(), tempFightCache, false);
//                        uiHandler.postDelayed(() -> restoreFromExisted(tempFightCache), 1000);
                    } else {
                        uiHandler.postDelayed(() -> {
                            fightId = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                            SettingsManager.setValue(UNFINISHED_FIGHT, fightId);
                            reset();
                        }, 1000);
                    }
                    fightId = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                    SettingsManager.setValue(UNFINISHED_FIGHT, fightId);
                    saveFightData();
//                    }
                    uiHandler.removeCallbacksAndMessages(null);
                } else {
                    screenState.set(SCREEN_SYNC_LAY);
                    //TODO notify user about no sync state
                    if (syncState.get()==SYNC_STATE_SYNCING) {
                        uiHandler.postDelayed(() -> {
                            onSyncCancel();
                            WifiManager wm = (WifiManager) InspirationDayApplication.getApplication().getApplicationContext().getSystemService(WIFI_SERVICE);
                            String ssid = "No WiFi";
                            List<WifiConfiguration> listOfConfigurations = null;
                            if (wm != null) {
                                listOfConfigurations = wm.getConfiguredNetworks();
                                for (int index = 0; index < listOfConfigurations.size(); index++) {
                                    WifiConfiguration configuration = listOfConfigurations.get(index);
                                    if (configuration.networkId == wm.getConnectionInfo().getNetworkId()) {
                                        ssid = configuration.SSID.replaceAll("\\p{P}","");
                                    }
                                }
                            } else {
                                ssid = "No WiFi";
                            }

                            MessageDialog.show(getActivity(),
                                    145965, "",
                                    getActivity().getResources().getString(R.string.sync_warn_check_wifi,
                                            ssid));
                        }, 15000);
                    } else {
                        uiHandler.removeCallbacksAndMessages(null);
                    }
                }
            }
        });
        leftCard.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!(core.getFightHandler().getFightData().getLeftFighter().getRedCardCount() != 0 &&
                        leftCard.get() == CARD_STATE_YELLOW)) {
                    core.getFightHandler().setCard(timeMillisecs.get(), PERSON_TYPE_LEFT, leftCard.get());
                }
//                else {
//                    core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, leftCard.get() - 19));
//                }
            }
        });
        leftPCard.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!(core.getFightHandler().getFightData().getLeftFighter().getRedPCardCount() != 0 &&
                        leftPCard.get() == CARD_STATE_YELLOW)) {
                    core.getFightHandler().setPCard(timeMillisecs.get(), PERSON_TYPE_LEFT, leftPCard.get());
                }
//                else {
//                    core.sendToSM(CommandHelper.setCard(PERSON_TYPE_LEFT, leftPCard.get() - 15));
//                }
            }
        });
        rightCard.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!(core.getFightHandler().getFightData().getRightFighter().getRedCardCount() != 0 &&
                        rightCard.get() == CARD_STATE_YELLOW)) {
                    core.getFightHandler().setCard(timeMillisecs.get(), PERSON_TYPE_RIGHT, rightCard.get());
                }
//                else {
//                    core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, rightCard.get() - 19));
//                }
            }
        });
        rightPCard.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!(core.getFightHandler().getFightData().getRightFighter().getRedPCardCount() != 0 &&
                        rightPCard.get() == CARD_STATE_YELLOW)) {
                    core.getFightHandler().setPCard(timeMillisecs.get(), PERSON_TYPE_RIGHT, rightPCard.get());
                }
//                else {
//                    core.sendToSM(CommandHelper.setCard(PERSON_TYPE_RIGHT, rightPCard.get() - 15));
//                }
            }
        });
        timeMillisecs.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                convertMStoDigits(timeMillisecs.get());
                backupCounter++;
                if (backupCounter > 3) {
                    core.getFightHandler().updateTime(timeMillisecs.get());
                    backupCounter = 0;
                }
            }
        });
        priority.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.getFightHandler().setPriority(timeMillisecs.get(), priority.get());
            }
        });
        isSM01alive.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!isSM01alive.get()) {
                    dialog = SmOffDialog.newInstance();
                    dialog.show(getActivity().getSupportFragmentManager(), "sm_dialog");
                } else {
                    if (dialog != null) {
                        dialog.close();
                    }
                }
            }
        });
        isPassive.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.vibr();
                core.notifyRCVisibility(isVideo.get(), isPhoto.get(), isPassive.get(), isCountry.get());
            }
        });
        leftName.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.getFightHandler().setName(PERSON_TYPE_LEFT, leftName.get());
            }
        });
        rightName.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.getFightHandler().setName(PERSON_TYPE_RIGHT, rightName.get());
            }
        });

        OnPropertyChangedCallback timeSetCallback = new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (screenState.get() == SCREEN_MENU_FIGHT_ACTIONS) {
                    timeMillisecs.set(((timerMinDec.get() * 10 + timerMinUnit.get()) * 60 +
                            (timerSecDec.get() * 10 + timerSecUnit.get())) * 1000);
                    timeNotifySM02();
                    Log.wtf("IN TIMER SET CALL", "+");
                }
            }
        };
        timerMinDec.addOnPropertyChangedCallback(timeSetCallback);
        timerMinUnit.addOnPropertyChangedCallback(timeSetCallback);
        timerSecDec.addOnPropertyChangedCallback(timeSetCallback);
        timerSecUnit.addOnPropertyChangedCallback(timeSetCallback);

        OnPropertyChangedCallback defTimerSetCallback = new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                defaultTime.set(((timerDefMinDec.get() * 10 + timerDefMinUnit.get()) * 60 +
                        (timerDefSecDec.get() * 10 + timerDefSecUnit.get())) * 1000);
            }
        };
        timerDefMinDec.addOnPropertyChangedCallback(defTimerSetCallback);
        timerDefMinUnit.addOnPropertyChangedCallback(defTimerSetCallback);
        timerDefSecDec.addOnPropertyChangedCallback(defTimerSetCallback);
        timerDefSecUnit.addOnPropertyChangedCallback(defTimerSetCallback);

        OnPropertyChangedCallback defPassiveSetCallback = new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                defaultPassiveTime.set(((timerPDefSecHoun.get()) * 100 +
                        (timerPDefSecDec.get() * 10 + timerPDefSecUnit.get())) * 1000);
            }
        };
        timerPDefSecHoun.addOnPropertyChangedCallback(defPassiveSetCallback);
        timerPDefSecDec.addOnPropertyChangedCallback(defPassiveSetCallback);
        timerPDefSecUnit.addOnPropertyChangedCallback(defPassiveSetCallback);
        videosLeft.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.changeVideoCounters(videosLeft.get(), videosRight.get());
            }
        });
        videosRight.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.changeVideoCounters(videosLeft.get(), videosRight.get());
            }
        });
//        ethState.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                Log.wtf("ETH STATE", ethState.get());
//            }
//        });
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
                core.sendToSM(CommandHelper.notifyPlayer(PLAYER_PAUSE, videoSpeed.get(), videoProgress.get()));
            }
        });
        videoSpeed.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                Log.wtf("SPEED", videoSpeed.get() + "");
                core.sendToSM(CommandHelper.notifyPlayer(PLAYER_START, videoSpeed.get(), videoProgress.get()));
            }
        });
        isServerOnline.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!isServerOnline.get()) {
                    mainScreenBtnState.set(BTN_STATE_RESET);
                } else {
                    mainScreenBtnState.set(Objects.equals(ethState.get(), ETH_SM_STATE_WAITING) ? BTN_STATE_NEXT : BTN_STATE_END);
                }
            }
        });
        final List<String> items = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            items.add(String.valueOf(i));
        }
        getActivity().getBinding().playerLay.seekSb.setItems(items);
        getActivity().getBinding().playerLay.seekSb.setMinSelectableIndex(0);
        getActivity().getBinding().playerLay.seekSb.setMaxSelectableIndex(100);
        getActivity().getBinding().playerLay.seekSb.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {

            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                videoProgress.set(position);
            }
        });
        final List<String> items2 = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            items2.add(String.valueOf(i));
        }
        getActivity().getBinding().playerLay.speedSb.setItems(items2);
        getActivity().getBinding().playerLay.speedSb.setMinSelectableIndex(0);
        getActivity().getBinding().playerLay.speedSb.setMaxSelectableIndex(9);
        getActivity().getBinding().playerLay.speedSb.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {

            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                videoSpeed.set(position+1);
            }
        });
        configLeft.setIndicator(activity.findViewById(R.id.left_fighter_progress_bar));
        configLeft.setAdapter(new FightersAutoCompleteAdapter(activity, true));
        configLeft.setListener((adapterView, view, position, id) -> {
            ListUser listUser = (ListUser) adapterView.getItemAtPosition(position);
            activity.getBinding().namesLay.leftFighter.setText(listUser.name);
            configRight.getAdapter().setExcludeUser(listUser);
            saveFightData();
        });
        configLeft.setWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                configRight.getAdapter().setExcludeUser(null);
                leftName.set(charSequence.toString());
                core.sendToSM(CommandHelper.setName(CommandsContract.PERSON_TYPE_LEFT, leftName.get()));
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
            configLeft.getAdapter().setExcludeUser(listUser);
            saveFightData();
        });
        configRight.setWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                configLeft.getAdapter().setExcludeUser(null);
                rightName.set(charSequence.toString());
                core.sendToSM(CommandHelper.setName(CommandsContract.PERSON_TYPE_RIGHT, rightName.get()));
                saveFightData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getActivity().getBinding().protocolLay.fightResultRecycler.getContext(),
                R.color.white, DividerItemDecoration.VERTICAL_LIST);
        getActivity().getBinding().protocolLay.fightResultRecycler.addItemDecoration(dividerItemDecoration);
        getActivity().getBinding().protocolLay.fightResultRecycler.setLayoutManager(llm);
        adapter = new FightActionsAdapter(getActivity(), false);
//        adapter.setOnItemClickListener(this);
        getActivity().getBinding().protocolLay.fightResultRecycler.setAdapter(adapter);
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

//                if (action != null && action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
//                    if (checkWifiOnAndConnected()) {
//                        uiHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (!TextUtils.isEmpty(SettingsManager.getValue(SM_CODE, "")) &&
//                                        !TextUtils.isEmpty(SettingsManager.getValue(SM_IP, ""))) {
//                                    syncState.set(SYNC_STATE_SYNCING);
//                                    Log.wtf("RESYNCing", "+");
//                                    core.tryToConnect();
//                                }
//                            }
//                        }, 2000);
//                    }
//                }
            }
        };
//        core.startUSB();
    }

    @Override
    public void onStart() {
        super.onStart();
//        isFightReady.set(false);
        if (!core.isUSBMode.get()) {
            core.startWiFiNetworking();
        }
        switch (screenState.get()) {
            case SCREEN_SYNC_LAY:
                mCodeScanner.startPreview();
                break;
            case SCREEN_SCORE_LAY:
                getActivity().getBinding().scoreLay.scoreLeft.setValue(leftScore.get());
                getActivity().getBinding().scoreLay.scoreRight.setValue(rightScore.get());
                break;
            case SCREEN_MENU_FIGHT_ACTIONS:
                getActivity().getBinding().menuFightActions.timersDecMinT.setValue(timerMinDec.get());
                getActivity().getBinding().menuFightActions.timersUnitMinT.setValue(timerMinUnit.get());
                getActivity().getBinding().menuFightActions.timersDecSecT.setValue(timerSecDec.get());
                getActivity().getBinding().menuFightActions.timersUnitSecT.setValue(timerSecUnit.get());
                getActivity().getBinding().menuFightActions.periodNpTimers.setValue(period.get());
                break;
        }

        isNamesLocked.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (isNamesLocked.get()) {
                    activity.findViewById(R.id.left_fighter).setEnabled(false);
                    activity.findViewById(R.id.left_fighter).setFocusable(false);
                    activity.findViewById(R.id.left_fighter).setClickable(false);
                    activity.findViewById(R.id.right_fighter).setEnabled(false);
                    activity.findViewById(R.id.right_fighter).setFocusable(false);
                    activity.findViewById(R.id.right_fighter).setClickable(false);
                } else {
                    activity.findViewById(R.id.left_fighter).setEnabled(true);
                    activity.findViewById(R.id.left_fighter).setFocusable(true);
                    activity.findViewById(R.id.left_fighter).setClickable(true);
                    activity.findViewById(R.id.right_fighter).setEnabled(true);
                    activity.findViewById(R.id.right_fighter).setFocusable(true);
                    activity.findViewById(R.id.right_fighter).setClickable(true);
                }
            }
        });
        getActivity().getBinding().bluetoothLay.btSwitch.setChecked(false);
        getActivity().getBinding().bluetoothLay.btSwitch.addSwitchObserver
                ((switchView, isChecked) -> core.sendToSM(new SetBluetoothCommand(isChecked?1:0).getBytes()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!core.isUSBMode.get()) {
            if (core.getConnectedSm() != null) {
                Log.wtf("SYNCED", "+");
                syncState.set(SYNC_STATE_SYNCED);
            } else {
                screenState.set(SCREEN_SYNC_LAY);
                if (!TextUtils.isEmpty(SettingsManager.getValue(SM_CODE, "")) &&
                        !TextUtils.isEmpty(SettingsManager.getValue(SM_IP, ""))) {
                    syncState.set(SYNC_STATE_SYNCING);
                    Log.wtf("SYNCing", "+");
                    core.tryToConnect();
                } else {
                    syncState.set(SYNC_STATE_NONE);
                    Log.wtf("SYNCED", "NOO");
                }
            }
            IntentFilter wifiFilter = new IntentFilter();
            wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            getActivity().registerReceiver(wifiReceiver, wifiFilter);
        } else {
            screenState.set(SCREEN_SYNC_LAY);
            syncState.set(SYNC_STATE_SYNCING);
            core.sendToSM(CommandHelper.auth("111",
                    "USB_MODE"));
        }

//        core.sendToCams(TIMER_START_UDP);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException e) {
            Log.wtf("NO BRADCAST RCVR", "+");
        }

    }

    @Override
    public void onStop() {
//        if (core.isUSBMode.get()) {
//            core.sendToSM(CommandHelper.endUsb());
//        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (core.isUSBMode.get()) {
            core.sendToSM(CommandHelper.endUsb());
        }
        super.onDestroy();
    }

    private void fillProtocol() {
        FightData data = core.getBackupHelper().getBackup();
        adapter.setData(data.getActionsList());
        adapter.notifyDataSetChanged();

        getActivity().getBinding().protocolLay.leftScore.setText(String.valueOf(data.getLeftFighter().getScore()));
        getActivity().getBinding().protocolLay.rightScore.setText(String.valueOf(data.getRightFighter().getScore()));

        getActivity().getBinding().protocolLay.leftFighter.setText(data.getLeftFighter().getName());
        getActivity().getBinding().protocolLay.rightFighter.setText(data.getRightFighter().getName());
    }

    public void restoreFromExisted(FightData info) {
//            timeMillisecs.set((int) info.getmCurrentTime());
//        reset();
        core.getFightHandler().resetFightData();
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fightId = SettingsManager.getValue(UNFINISHED_FIGHT, "");
                leftScore.set(info.getLeftFighter().getScore());
                rightScore.set(info.getRightFighter().getScore());
                period.set(info.getmCurrentPeriod());
                core.sendToSM(CommandHelper.setTimer(info.getmCurrentTime(), 0));
                rightName.set(info.getRightFighter().getName());
                leftName.set(info.getLeftFighter().getName());
                priority.set(info.getmPriority());
                videosLeft.set(info.getmVideoLeft());
                videosRight.set(info.getmVideoRight());
                int rcl = info.getLeftFighter().getRedCardCount();
                if (rcl != 0) {
                    for (int i = 0; i < rcl; i++) {
                        leftCard.set(CARD_STATE_RED);
                        leftCard.set(CARD_STATE_YELLOW);
                    }
                } else {
                    int ycl = info.getLeftFighter().getYellowCardCount();
                    if (ycl != 0) {
                        leftCard.set(CARD_STATE_YELLOW);
                    } else if (info.getLeftFighter().getmCard() == CardStatus.CardStatus_Black) {
                        leftCard.set(CARD_STATE_BLACK);
                    }
                }

                int rcr = info.getRightFighter().getRedCardCount();
                if (rcr != 0) {
                    for (int i = 0; i < rcr; i++) {
                        rightCard.set(CARD_STATE_RED);
                        rightCard.set(CARD_STATE_YELLOW);
                    }
                } else {
                    int ycr = info.getRightFighter().getYellowCardCount();
                    if (ycr != 0) {
                        rightCard.set(CARD_STATE_YELLOW);
                    } else if (info.getRightFighter().getmCard() == CardStatus.CardStatus_Black) {
                        rightCard.set(CARD_STATE_BLACK);
                    }
                }

                int rpcl = info.getLeftFighter().getRedPCardCount();
                Log.wtf("RPCL", rpcl + "");
                if (rpcl != 0) {
                    for (int i = 0; i < rpcl; i++) {
                        leftPCard.set(CARD_STATE_RED);
                        leftPCard.set(CARD_STATE_YELLOW);
                        Log.wtf("LEFT P CARD", "++" + i);
                    }
                } else {
                    int ypcl = info.getLeftFighter().getYellowPCardCount();
                    if (ypcl != 0) {
                        leftPCard.set(CARD_STATE_YELLOW);
                    } else if (info.getLeftFighter().getmPCard() == CardStatus.CardPStatus_Black) {
                        leftPCard.set(CARD_STATE_BLACK);
                    }
                }

                int rpcr = info.getRightFighter().getRedPCardCount();
                if (rpcr != 0) {
                    for (int i = 0; i < rpcr; i++) {
                        rightPCard.set(CARD_STATE_RED);
                        rightPCard.set(CARD_STATE_YELLOW);
                        Log.wtf("RIGHT P CARD", "++" + i);
                    }
                } else {
                    int ypcr = info.getRightFighter().getYellowPCardCount();
                    if (ypcr != 0) {
                        rightPCard.set(CARD_STATE_YELLOW);
                    } else if (info.getRightFighter().getmPCard() == CardStatus.CardPStatus_Black) {
                        rightPCard.set(CARD_STATE_BLACK);
                    }
                }
                uiHandler.postDelayed(() -> {
                    isSemiInWork.set(false);
                }, 800);
            }
        }, 700);
    }

    private void convertMStoDigits(int milliseconds) {
        if (screenState.get() == SCREEN_MAIN) {
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(milliseconds);
            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
            timerSecUnit.set(seconds % 10);
            timerMinUnit.set(minutes % 10);
            timerSecDec.set(seconds / 10);
            timerMinDec.set(minutes / 10);
        }
        int millis = milliseconds > 10000 ? milliseconds + 999 : milliseconds;
        timeToDisplay.set(TimeUnit.MILLISECONDS.toMinutes(millis) / 10 + "" +
                ((TimeUnit.MILLISECONDS.toMinutes(millis)) % 10) + ":" +
                ((TimeUnit.MILLISECONDS.toSeconds(millis)) % 60) / 10 + "" +
                (TimeUnit.MILLISECONDS.toSeconds(millis) % 60) % 10);
    }

    private void timeNotifySM02() {
        int timeToSend;
        if (timerMode.get() == TIMER_MODE_MAIN) {
            timeToSend = timeMillisecs.get();
//            timeToSend = ((timerMinDec.get() * 10 + timerMinUnit.get()) * 60 + (timerSecDec.get() * 10 + timerSecUnit.get())) * 1000;
        } else if (timerMode.get() == TIMER_MODE_MEDICINE) {
            timeToSend = 300000;
        } else {
            timeToSend = 60000;
        }
        if (timeToSend == 0) {
            core.sendToSM(CommandHelper.setTimer(defaultTime.get(), timerMode.get() - 80));
        } else {
            core.sendToSM(CommandHelper.setTimer(timeToSend, timerMode.get() - 80));
        }
        Log.wtf("TIME NOTIFY", timeToSend + "");

    }

    //View section

    public void onTimerStopClick() {
        if (System.currentTimeMillis() - lastTimerTS > 200) {
            if (timerState.get() == TIMER_STATE_IN_PROGRESS) {
                String filename = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "") + "_" +
                        timeToDisplay.get() + "_" + String.valueOf(period.get());
//                core.sendToCams(TIMER_STOP_UDP + "\0" + filename);
//                isVideoReady.set(false);
            }
            core.vibr();
            //TODO check
//            timerState.set(TIMER_STATE_PAUSED);
//            getActivity().getBinding().titleMain.setVisibility(View.VISIBLE);
            if (timerMode.get() == TIMER_MODE_MAIN) {
                if (isPassive.get()) {
                    isPassiveBtnVisible.set(true);
                }
                core.sendToSM(CommandHelper.startTimer(false));
            } else {
                ConfirmationDialog.show(getActivity(), PAUSE_FINISH_MESSAGE, "",
                        getActivity().getResources().getString(R.string.pause_finish_ask));
            }
            lastTimerTS = System.currentTimeMillis();
        }
    }

    public void onTimerStartClick() {
        if (System.currentTimeMillis() - lastTimerTS > 200) {
            isPeriodFinished = false;
            core.vibr();
            //TODO check
//            timerState.set(TIMER_STATE_IN_PROGRESS);
////        timerMode.set(TIMER_MODE_MAIN);
//            getActivity().getBinding().titleMain.setVisibility(View.GONE);
            core.sendToSM(CommandHelper.startTimer(true));
            isPassiveBtnVisible.set(false);
            lastTimerTS = System.currentTimeMillis();
            isVideoBtnVisible.set(false);
        }
    }

    public void pauseBreak() {
        Log.wtf("PAUSE BREAK", "START");
        core.sendToSM(CommandHelper.startTimer(false));
        timeMillisecs.set(lastTimerBeforePause == 0 ? defaultTime.get() :
                (timerMode.get() == TIMER_MODE_MEDICINE ? lastTimerBeforePause : defaultTime.get()));
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeMillisecs.get());
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeMillisecs.get()) % 60;
        timerSecUnit.set(seconds % 10);
        timerMinUnit.set(minutes % 10);
        timerSecDec.set(seconds / 10);
        timerMinDec.set(minutes / 10);
        if (timerMode.get() == TIMER_MODE_PAUSE) {
            onPeriodNext();
        }
        timerMode.set(TIMER_MODE_MAIN);
        timeNotifySM02();
        lastTimerBeforePause = 0;
        uiHandler.postDelayed(() -> isPeriodFinished = false, 700);
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
        screenState.set(SCREEN_MENU_PENALTIES);
    }

    public void onPBlackLeft() {
        core.vibr();
        leftPCard.set(CARD_STATE_BLACK);
    }

    public void onPBlackRight() {
        core.vibr();
        rightPCard.set(CARD_STATE_BLACK);
    }

    public boolean onPCancelLeft() {
        core.vibr();
        leftPCard.set(CARD_STATE_NONE);
        return true;
    }

    public boolean onPCancelRight() {
        core.vibr();
        rightPCard.set(CARD_STATE_NONE);
        return true;
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

    public boolean onCancelLeft() {
        core.vibr();
        rightCard.set(CARD_STATE_NONE);
        return true;
    }

    public boolean onCancelRight() {
        core.vibr();
        leftCard.set(CARD_STATE_NONE);
        return true;
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
        onCloseBtn();
    }

    public void onDefPTimeAccepted() {
        onCloseBtn();
    }

    public void onMenuPause() {
        core.vibr();
        ConfirmationDialog.show(getActivity(), PAUSE_CALL_MAIN_MESSAGE, "",
                getActivity().getResources().getString(R.string.pause_call_message));
    }

    public void performPause() {
        core.vibr();
        lastTimerBeforePause = timeMillisecs.get();
        timerMode.set(TIMER_MODE_PAUSE);
        timeNotifySM02();
        //TODO check
//        timerState.set(TIMER_STATE_IN_PROGRESS);
//        getActivity().getBinding().titleMain.setVisibility(View.GONE);
        screenState.set(SCREEN_MAIN);
        lastTimerTS = System.currentTimeMillis();
    }

    public void onMenuMedical() {
        core.vibr();
        lastTimerBeforePause = timeMillisecs.get();
        timerMode.set(TIMER_MODE_MEDICINE);
        timeNotifySM02();
        //TODO check
//        timerState.set(TIMER_STATE_IN_PROGRESS);
//        getActivity().getBinding().titleMain.setVisibility(View.GONE);
        screenState.set(SCREEN_MAIN);
        lastTimerTS = System.currentTimeMillis();
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
        if (weapon.get() != WEAPON_SABER) {
//            if (isServerOnline.get()) {
                onPassiveLock();
//            } else {
//                screenState.set(SCREEN_PASSIVE_LAY);
//            }
        }
    }

    public void onMenuPriority() {
        core.vibr();
        if (priority.get() == PERSON_TYPE_NONE) {
            onPriority();
        } else {
            onPriorityNone();
        }
    }

    public void onMenuBCards() {
        core.vibr();
        screenState.set(SCREEN_B_CARDS_LAY);
    }

    public void onMenuDeviceReset() {
        core.vibr();
        ConfirmationDialog.show(getActivity(), RESET_MESSAGE, "",
                getActivity().getResources().getString(R.string.reset_confirm));
    }

    public void onMenuEndBout() {
        //TODO
        onMenuDeviceReset();
    }

    public void onMenuCyrano() {
        core.vibr();
        screenState.set(SCREEN_STATE_CYRANO_CONTROLS);
    }

    public void onMenuProtocol() {
        core.vibr();
        screenState.set(SCREEN_STATE_PROTOCOL);
    }

    public void onMenuPractice() {
        core.vibr();
        fightId = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        SettingsManager.setValue(UNFINISHED_FIGHT, fightId);
        reset();
    }

    public void reset() {
        core.sendToSM(CommandHelper.reset(defaultTime.get()));
        fightId = "";
        core.getFightHandler().resetFightData();
        timeMillisecs.set(defaultTime.get());
        SettingsManager.removeValue(CommonConstants.LAST_FIGHT_ID);
        timeToDisplay.set(String.valueOf(timerDefMinDec.get()) + String.valueOf(timerDefMinUnit.get()) + ":" +
                String.valueOf(timerDefSecDec.get()) + String.valueOf(timerDefSecUnit.get()));
        timerColor.set(COLOR_USUAL);
        leftPCard.set(CARD_STATE_NONE);
        rightPCard.set(CARD_STATE_NONE);
        leftCard.set(CARD_STATE_NONE);
        rightCard.set(CARD_STATE_NONE);
        leftName.set("");
        rightName.set("");
        activity.getBinding().namesLay.leftFighter.setText("");
        activity.getBinding().namesLay.rightFighter.setText("");
        leftScore.set(0);
        rightScore.set(0);
        timerMinDec.set(0);
        timerMinUnit.set(0);
        timerSecDec.set(0);
        timerSecUnit.set(0);
        period.set(1);
        screenState.set(SCREEN_MAIN);
        priority.set(PERSON_TYPE_NONE);
        isNamesLocked.set(false);
        core.getFightHandler().resetFightData();
    }

    public void onMenuWeaponType() {
        core.vibr();
        screenState.set(SCREEN_WEAPON_LAY);
    }

    public void onMenuNames() {
        core.vibr();
        if (isServerOnline.get()) {
            onSwapBtn();
        } else {
            screenState.set(SCREEN_NAMES_LAY);
        }
    }

    public void onMenuDefTime() {
        core.vibr();
        screenState.set(SCREEN_DEF_TIME_LAY);
        getActivity().getBinding().defTimeLay.defTimeTitle
                .setText(getActivity().getResources().getString(R.string.fa_def_time));
    }

    public void onMenuDefPassiveTime() {
        core.vibr();
        screenState.set(SCREEN_DEF_PASSIVE_TIME_LAY);
        getActivity().getBinding().defTimeLay.defTimeTitle
                .setText(getActivity().getResources().getString(R.string.fa_def_p_time));
    }

    public void onMenuVideoReplays() {
        core.vibr();
        screenState.set(SCREEN_STATE_VIDEO);
    }

    public void onMenuDisconnect() {
        core.vibr();
        SettingsManager.removeValue(SM_CODE);
        core.onDisconnect();
        mCodeScanner.startPreview();
    }

    public void onMenuPhrases() {
        //TODO later!!!
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
        core.sendToSM(CommandHelper.setWeapon(weapon));
        screenState.set(SCREEN_MAIN);
    }

    public void onSwapBtn() {
        core.vibr();
        FightData data = core.getFightHandler().getFightData();
        FighterData temp = data.getLeftFighter();
        data.setmLeftFighterData(data.getRightFighter());
        data.setmRightFighterData(temp);
        int tempVideo = data.getmVideoLeft();
        data.setmVideoLeft(data.getmVideoRight());
        data.setmVideoRight(tempVideo);
        int tempPrio = data.getmPriority();
        if (tempPrio == PERSON_TYPE_LEFT) {
            data.setmPriority(PERSON_TYPE_RIGHT);
        } else if (tempPrio == PERSON_TYPE_RIGHT) {
            data.setmPriority(PERSON_TYPE_LEFT);
        }
        restoreFromExisted(data);
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                core.sendToSM(CommandHelper.swap());
            }
        }, 900);
        screenState.set(SCREEN_MAIN);
    }

    public void onPriority() {
        priority.set(new Random().nextInt()%2 == 0 ? PERSON_TYPE_LEFT : PERSON_TYPE_RIGHT);
        timeMillisecs.set(60000);
        timeNotifySM02();
        onCloseBtn();
    }

    public void onPriorityNone() {
        priority.set(PERSON_TYPE_NONE);
    }

    public void onCloseBtn() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (screenState.get() == SCREEN_REPLAYS_LAY) {
            core.sendToSM(CommandHelper.notifyPlayer(CommandsContract.PLAYER_STOP, videoSpeed.get(), videoProgress.get()));
        }
        core.vibr();
        screenState.set(SCREEN_MAIN);
        isSemiInWork.set(false);
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
        //TODO union with end bout method
        core.vibr();
        if (leftScore.get() == rightScore.get() &&
                priority.get() == PERSON_TYPE_NONE) {
            FightCantEndDialog.show(getActivity(), withSEMI.get());
        } else {
            onFightFinishBegin();
        }
    }

    public void onFightFinishBegin() {
//        if (withSEMI.get()) {
//            fightFinishAskHandler.start();
//        } else {
//            fightFinish();
//            goToNewFight();
//        }
        screenState.set(SCREEN_MAIN);
        isSemiInWork.set(false);
        core.sendToSM(new EthernetFinishAskCommand().getBytes());
    }

    public void onPassiveLock() {
        core.vibr();
        core.sendToSM(CommandHelper.passiveState(isPassive.get(), true, defaultPassiveTime.get()));
        screenState.set(SCREEN_MAIN);
    }

    public void onPhraseSelected(int phrase, boolean isLeft) {
        if (isLastAndOutdated) {
            isLastAndOutdated = false;
        }
    }

    public void onDoubleBtn() {
        core.vibr();
        core.vibr();
        leftScore.set(leftScore.get() + 1);
        rightScore.set(rightScore.get() + 1);
        lastScoreTS = System.currentTimeMillis();
    }

    public void onVideoBtn() {
        screenState.set(SCREEN_REPLAYS_LAY);
        //TODO fill temp
        core.sendToSM(CommandHelper.loadFile(""));
        getActivity().getBinding().playerLay.speedSb.smoothSelectIndex(videoSpeed.get()-1);
        getActivity().getBinding().playerLay.speedSb.computeScroll();
        getActivity().getBinding().playerLay.seekSb.smoothSelectIndex(0);
        getActivity().getBinding().playerLay.seekSb.computeScroll();
        uiHandler.postDelayed(() -> {
            getActivity().getBinding().playerLay.speedSb.smoothSelectIndex(videoSpeed.get()-1);
            getActivity().getBinding().playerLay.speedSb.computeScroll();
            getActivity().getBinding().playerLay.seekSb.smoothSelectIndex(0);
            getActivity().getBinding().playerLay.seekSb.computeScroll();
        }, 100);
    }

    public void onPlayBtn() {
        core.vibr();
        core.sendToSM(CommandHelper.notifyPlayer(PLAYER_START, videoSpeed.get(), videoProgress.get()));
    }

    public void onPauseBtn() {
        core.vibr();
        core.sendToSM(CommandHelper.notifyPlayer(PLAYER_PAUSE, videoSpeed.get(), 101));
    }

    public void onDisconnect() {
        SettingsManager.removeValue(SM_CODE);
        SettingsManager.removeValue(SM_IP);
        core.onDisconnect();
    }

    public void onVideoCloseBtn() {
        onCloseBtn();
        if (!isVideoReady.get()) {
            core.sendToSM(CommandHelper.transferAbort());
            videoProgress.set(0);
            videoSpeed.set(4);
            getActivity().getBinding().playerLay.seekSb.selectIndex(videoProgress.get());
            getActivity().getBinding().playerLay.speedSb.selectIndex(videoSpeed.get());
        }
    }

    //TODO FILL METHODS

    public void fightApplyOk() {
        core.vibr();
        screenState.set(SCREEN_MAIN);
    }


    //TODO SWAP 1 AND 0 in NEXTPREVCMD values
    public void fightNext() {
        core.vibr();
        core.sendToSM(new EthNextPrevCommand(1).getBytes());
        isFightFinishInProgress = false;
        screenState.set(SCREEN_STATE_WAITING);
    }

    public void fightPrev() {
        core.vibr();
        core.sendToSM(new EthNextPrevCommand(0).getBytes());
        isFightFinishInProgress = false;
        screenState.set(SCREEN_STATE_WAITING);
    }

    public void fightRemove() {
        core.vibr();
        core.sendToSM(new EthNextPrevCommand(2).getBytes());
        screenState.set(SCREEN_STATE_WAITING);
    }

    public void fightFinish() {
        onMenuDeviceReset();
        SettingsManager.removeValue(UNFINISHED_FIGHT);
    }

    public void goToNewFight() {
        core.vibr();
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        goToNewScreen = true;
        getActivity().startActivity(intent);
        SettingsManager.removeValue(UNFINISHED_FIGHT);
    }

    public void enterCyranoMode() {
        FightFinishedDialog.show(getActivity(), getActivity().getResources().getString(R.string.fight_no_active));
    }

    public void goToNewCyrano() {
        core.vibr();
        screenState.set(SCREEN_STATE_WAITING);
    }

    public void exitCyranoMode() {
        //TODO introduce boolean or use existed
        isFightFinishInProgress = false;
    }

    public void onSyncCancel() {
        core.vibr();
        syncState.set(SYNC_STATE_NONE);
        core.onDisconnect();
        mCodeScanner.startPreview();
        SettingsManager.removeValue(SM_CODE);
        SettingsManager.removeValue(SM_IP);
    }

    public void onBluetooth() {
        core.vibr();
        screenState.set(SCREEN_STATE_BLUETOOTH);
    }

    public void onExitApp() {
        core.vibr();
        ConfirmationDialog.show(getActivity(), EXIT_MESSAGE, "",
                getActivity().getResources().getString(R.string.exit_app_question));
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
    }

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr != null) {
            if (wifiMgr.isWifiEnabled()) {

                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                return wifiInfo.getNetworkId() != -1;
            } else {
                return false;
            }
        }

        return false;
    }

    //Core section

    @Override
    public void messageReceived(byte command, byte[] message) {
        switch (command) {
            case BROADCAST_TCP_CMD:
                    if (message[0] == 0 && isSM01alive.get()) {
                        isSM01alive.set(false);
                    } else {
                        if (message[0] != 0 && !isSM01alive.get()) {
                            isSM01alive.set(true);
                        }
                    }
                        weapon.set(message[0]);
                        //TODO handle flags
                        byte[] time = new byte[4];
                        System.arraycopy(message, 3, time, 0, 4);
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
//                        if (!isPeriodFinished && ByteBuffer.wrap(time).getInt() < 5 && timerMode.get() == TIMER_MODE_MAIN) {
//                            ConfirmationDialog.show(getActivity(), PAUSE_START_MESSAGE, getActivity().getResources().getString(R.string.period_finished),
//                                    getActivity().getResources().getString(R.string.pause_start_ask));
//                            isPeriodFinished = true;
//                        }
                        if (message[7] == 0 && timerState.get() == TIMER_STATE_IN_PROGRESS) {
                            getActivity().runOnUiThread(() -> {
                                timerState.set(TIMER_STATE_PAUSED);
                                getActivity().getBinding().titleMain.setVisibility(View.VISIBLE);
                                String filename = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "") + "_" +
                                        timeToDisplay.get() + "_" + String.valueOf(period.get());
//                        core.sendToCams(TIMER_STOP_UDP + "\0" + filename);
//                                isVideoReady.set(false);
                                if (isPassive.get()) {
                                    isPassiveBtnVisible.set(true);
                                }
                                if (timerMode.get() == TIMER_MODE_PAUSE) {
                                    pauseBreak();
                                }
                            });
                        } else if (message[7] == 1 && timerState.get() != TIMER_STATE_IN_PROGRESS) {
                            getActivity().runOnUiThread(() -> {
                                timerState.set(TIMER_STATE_IN_PROGRESS);
                                getActivity().getBinding().titleMain.setVisibility(View.GONE);
                                isPassiveBtnVisible.set(false);
                            });
                        }
//                }
                break;
            case DISP_RECEIVE_CMD:
                Log.wtf("DISP INIT", Arrays.toString(message));
                if (!isSemiInWork.get()) {
                    Log.wtf("DISP MSG", Arrays.toString(message));
                    //TODO create and fill fight data to store it later
                    byte[] dispBuf = new byte[message[0]&0xFF];
                    System.arraycopy(message, 1, dispBuf, 0, message[0]&0xFF);
                    String disp = new String(dispBuf, Charset.forName("UTF-8"));
                    if (dispHandler.updateFromCommand(disp)) {
                        screenState.set(SCREEN_STATE_WAITING);
                        dispTempData = dispHandler.getEthFightData();
                        dispDataFirst.set(dispTempData.getLeftFighter().getName() + " - "
                                + dispTempData.getRightFighter().getName());
                        dispDataSecond.set(dispTempData.getLeftFighter().getScore() + " : "
                                + dispTempData.getRightFighter().getScore());
                        ethNecessaryInfo = dispHandler.getInfoMain().getPhase() + "|" +
                                dispHandler.getInfoMain().getPoulTab() + "|" +
                                dispHandler.getInfoMain().getMatchNumber() + "|" +
                                dispHandler.getInfoMain().getType() + "|" +
                                dispHandler.getInfoLeft().getId() + "|" +
                                dispHandler.getInfoLeft().getNation() + "|" +
                                dispHandler.getInfoRight().getId() + "|" +
                                dispHandler.getInfoRight().getNation();
                        isSemiInWork.set(true);
                        restoreFromExisted(dispTempData);
                        isNamesLocked.set(true);
                        uiHandler.postDelayed(() ->
                                core.sendToSM(new EthernetApplyFightCommand(ethNecessaryInfo).getBytes()), 900);
                    }
                }

                break;
            case ETH_ACK_NAK:
                if (!isFightFinishInProgress) {
//                    if (message[0] == 1) {
//                        FightFinishedDialog.show(getActivity(), getActivity().getResources().getString(R.string.fight_end_accept));
//                        if (fightFinishAskHandler != null) {
//                            fightFinishAskHandler.finish();
//                        }
//                        isNamesLocked.set(false);
//                    } else
                        if (message[0] == 0) {
                        FightCantEndDialog.show(getActivity(), withSEMI.get());
                        if (fightFinishAskHandler != null) {
                            fightFinishAskHandler.finish();
                        }
                    }
                    isFightFinishInProgress = true;
                }
                break;
            case PASSIVE_MAX:
                core.vibrContiniously();
                break;
            case PAUSE_FINISHED:
                isPeriodFinished = true;
                Log.wtf("PAUSE FIN CMD", "+");
//                activity.runOnUiThread(() -> {
//                    if (timerMode.get() == TIMER_MODE_PAUSE) {
//                        onTimerStopClick();
//                    } else if (timerMode.get() == TIMER_MODE_MEDICINE) {
//                        timeToDisplay.set("00:00");
//                    }
//                    pauseBreak();
//                });
                break;
            case VIDEO_RECEIVED:
                isVideoReady.set(true);
                break;
            case VIDEO_READY:
                //TODO handle video name in msg[2]
//                Log.wtf("VIDEO READY", "++");
//                if (timerState.get() == TIMER_STATE_PAUSED) {
//                    if (!core.getConnectedCameras().isEmpty()) {
                if (isCamConnected) {
                    isVideoBtnVisible.set(true);
                }
//                }
                break;
            case DISCONNECT_TCP_CMD:
                onMenuDisconnect();
                break;
            case ADD_STATE:
                isCamConnected = message[0] == 1;
                if (message[0] != 1) {
                    isVideoBtnVisible.set(false);
                }
                isServerOnline.set(message[1]==1);
                byte[] stateBytes = new byte[message[2]];
                System.arraycopy(message, 3, stateBytes, 0, message[2]);
                ethState.set(new String(stateBytes, StandardCharsets.UTF_8));
                break;
        }
    }

    @Override
    public void connectionLost() {
        //TODO check if need??
        if (!TextUtils.isEmpty(SettingsManager.getValue(SM_CODE, "")) &&
                !TextUtils.isEmpty(SettingsManager.getValue(SM_IP, ""))) {
            syncState.set(SYNC_STATE_SYNCING);
        } else {
            syncState.set(SYNC_STATE_NONE);
            mCodeScanner.startPreview();
        }
    }

    @Override
    public void devicesUpdated(ArrayList<Device> devices) {
        //TODO
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

    @BindingAdapter("app:np_value")
    public static void setValue(NumberPicker picker, int value) {
        picker.setValue(value);
    }

    @InverseBindingAdapter(attribute = "app:np_value")
    public static int getValue(NumberPicker picker) {
        return picker.getValue();
    }

    @BindingAdapter("app:np_valueAttrChanged")
    public static void setListeners(NumberPicker picker, final InverseBindingListener attrChange) {
        picker.setOnValueChangedListener((picker1, oldVal, newVal) -> attrChange.onChange());
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