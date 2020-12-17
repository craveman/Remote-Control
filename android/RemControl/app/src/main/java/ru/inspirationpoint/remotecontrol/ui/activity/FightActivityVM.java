package ru.inspirationpoint.remotecontrol.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.RequiresApi;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;

import androidx.recyclerview.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.budiyev.android.codescanner.CodeScanner;
import com.google.gson.Gson;
import com.shawnlin.numberpicker.NumberPicker;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.FightersAutoComplConfig;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.AddOptRequestCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.EthNextPrevCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.EthernetApplyFightCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.EthernetFinishAskCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.GetVideosCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.ProtocolRequestCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.SetPisteNumCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.SwitchAutoIncCommand;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.SwitchLangCommand;
import ru.inspirationpoint.remotecontrol.manager.coreObjects.Device;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.manager.handlers.CoreHandler;
import ru.inspirationpoint.remotecontrol.manager.handlers.EthernetCommandsHelpers.EthernetDispHandler;
import ru.inspirationpoint.remotecontrol.manager.handlers.EthernetCommandsHelpers.FightFinishAskHandler;
import ru.inspirationpoint.remotecontrol.manager.helpers.WiFiHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;
import ru.inspirationpoint.remotecontrol.ui.adapter.FightActionsAdapter;
import ru.inspirationpoint.remotecontrol.ui.adapter.SMAdapter;
import ru.inspirationpoint.remotecontrol.ui.dialog.ConfirmationDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightCantEndDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.FightFinishedDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.MessageDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.ReplaysDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.SmOffDialog;

import static android.content.Context.WIFI_SERVICE;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.*;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.*;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.END_FIGHT_MESSAGE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.EXIT_MESSAGE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.FIGHT_FINISHED_OK;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.PAUSE_FINISH_MESSAGE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.REMOVE_FIGHT_MESSAGE;


public class FightActivityVM extends ActivityViewModel<FightActivity> implements
        CoreHandler.CoreServerCallback, FightActionsAdapter.OnItemClickListener,
        SMAdapter.OnSMChooseListener{

    public static final int COLOR_USUAL = R.color.white;
    public static final int COLOR_RED = R.color.redCard;
    public static final int COLOR_YELLOW = R.color.yellowCard;

    public static final int SCREEN_MAIN = 0;
    public static final int SCREEN_MAIN_MENU = 2;
    public static final int SCREEN_STATE_SETTINGS = 3;
    public static final int SCREEN_SYNC_LAY = 4;

    public static final int MENU_MODE_MAIN = 0;
    public static final int MENU_MODE_RESET = 1;
    public static final int MENU_MODE_CYRANO = 2;
    public static final int MENU_MODE_NAMES = 3;
    public static final int MENU_MODE_PAUSE = 4;
    public static final int MENU_MODE_CARDS = 5;
    public static final int MENU_MODE_PRIORITY = 6;
    public static final int MENU_MODE_PERIOD = 7;
    public static final int MENU_MODE_PROTOCOL = 8;
    public static final int MENU_MODE_SCORE = 9;
    public static final int MENU_MODE_TIMER = 10;
    public static final int MENU_MODE_WEAPON = 11;

    public static final int SETTINGS_MODE_MAIN = 0;
    public static final int SETTINGS_MODE_PERIOD_TIME = 1;
    public static final int SETTINGS_MODE_PASSIVE_TIME = 2;
    public static final int SETTINGS_MODE_PISTE_NUM= 3;
    public static final int SETTINGS_MODE_LANGUAGE = 4;

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
    public static final int SYNC_STATE_REVEALED = 43;
    public static final int SYNC_STATE_SYNCED = 42;

    public static final int PERSON_TYPE_NONE = 0;

    public ObservableInt screenState = new ObservableInt(SCREEN_MAIN);
    public ObservableInt settingsState = new ObservableInt(SETTINGS_MODE_MAIN);
    public ObservableInt menuState = new ObservableInt(MENU_MODE_MAIN);
    public ObservableInt timerMode = new ObservableInt(TIMER_MODE_MAIN);
    public ObservableInt timerState = new ObservableInt(TIMER_STATE_NOT_STARTED);
    public ObservableField<String> timeToDisplay = new ObservableField<>("3:00");
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
    public ObservableInt period = new ObservableInt(1);
    public ObservableInt weapon = new ObservableInt(WEAPON_FOIL);
    public ObservableInt syncState = new ObservableInt(SYNC_STATE_NONE);
    public ObservableInt priority = new ObservableInt(PERSON_TYPE_NONE);
    public ObservableInt timerMin = new ObservableInt(3);
    public ObservableInt timerSec = new ObservableInt(0);
    public ObservableInt timerDefMin = new ObservableInt(3);
    public ObservableInt timerDefSec = new ObservableInt(0);
    public ObservableInt timerPassiveMin = new ObservableInt(1);
    public ObservableInt timerPassiveSec = new ObservableInt(0);
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
    public ObservableBoolean isRepeaters = new ObservableBoolean(false);
    public ObservableField<String> ethState = new ObservableField<>("W");
    public ObservableBoolean isCamConnected = new ObservableBoolean(false);
    public ObservableBoolean isAutoIncOn = new ObservableBoolean(false);
    public ObservableField<String> pisteNum = new ObservableField<>(" ");
    public ObservableBoolean isPisteNumOn = new ObservableBoolean(false);
    public ObservableField<String> pisteName = new ObservableField<>("");


    public ObservableField<String> leftName = new ObservableField<>("");
    public ObservableField<String> rightName = new ObservableField<>("");
    private final ObservableField<String> leftId = new ObservableField<>("");
    private final ObservableField<String> rightId = new ObservableField<>("");

    public String fightId;

    public ObservableBoolean withSEMI = new ObservableBoolean(false);

    private final Handler uiHandler = new Handler();
    public CoreHandler core;

    public final FightersAutoComplConfig configLeft = new FightersAutoComplConfig();
    public final FightersAutoComplConfig configRight = new FightersAutoComplConfig();
    public boolean goToNewScreen = false;

    private long lastScoreTS = 0;
    private long lastTimerTS = 0;
    public ObservableBoolean phrasesEnabled = new ObservableBoolean(false);
    private boolean isLastAndOutdated = false;

    private int lastTimerBeforePause = 0;

    private final FightFinishAskHandler fightFinishAskHandler;

    public ObservableInt videoSpeed = new ObservableInt(4);
    public ObservableInt videoProgress = new ObservableInt(0);

    public ObservableBoolean isSM01alive = new ObservableBoolean(true);

    private int backupCounter = 0;

    private final EthernetDispHandler dispHandler = new EthernetDispHandler();

    private FightData dispTempData = null;

    public ObservableBoolean isNamesLocked = new ObservableBoolean(false);

    private FightActionsAdapter adapter;

    private final BroadcastReceiver wifiReceiver;

    private String ethNecessaryInfo = "";

    public ObservableField<String> logTextTemp = new ObservableField<>();

    private String requiredSSID = "";

    private final WiFiHelper wiFiHelper;

    boolean isIndividual = true;

    public ObservableField<CyranoState> cyranoState = new ObservableField<>(CyranoState.Off);

    private ConnectivityManager mConnectivityManager = null;
    private ConnectivityManager.NetworkCallback mNetworkCallback = null;

    private final ArrayList<FightActionData> tempProtocol = new ArrayList<>(300);

    public SMAdapter smAdapter;

    public FightActivityVM(FightActivity activity) {
        super(activity);
        core = InspirationDayApplication.getApplication().getCoreHandler();
        core.setActivity(getActivity());
        core.setServerCallback(this);
        fightFinishAskHandler = new FightFinishAskHandler(core);
        pisteNum.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                onPisteNumSet();
                pisteName.set(getActivity().getResources().getString(R.string.piste) + " " + pisteNum.get());
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
                    screenState.set(SCREEN_MAIN);
                    uiHandler.removeCallbacksAndMessages(null);
                    core.sendToSM(new AddOptRequestCommand().getBytes());
                } else {
                    screenState.set(SCREEN_SYNC_LAY);
                    //TODO resume to select network (now programmatically, not from settings)
                    if (syncState.get() == SYNC_STATE_SYNCING) {
                        if (checkWifiOnAndConnected()) {
                            core.initializeDiscovery();
                            uiHandler.postDelayed(() -> onSyncCancel(), 3000);
                        }
                    } else {
                        uiHandler.removeCallbacksAndMessages(null);
//                        stopDiscovery();
                    }
                }
                if (syncState.get() != SYNC_STATE_REVEALED) {
                    activity.runOnUiThread(() -> smAdapter.clear());
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
        isPassive.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.vibr();
                core.notifyRCVisibility(isVideo.get(), isPhoto.get(), isPassive.get(), isCountry.get());
            }
        });
//        leftName.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                core.getFightHandler().setName(PERSON_TYPE_LEFT, leftName.get());
//            }
//        });
//        rightName.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                core.getFightHandler().setName(PERSON_TYPE_RIGHT, rightName.get());
//            }
//        });
        timerMin.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                onTimerAccepted();
            }
        });
        timerSec.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                onTimerAccepted();
            }
        });
        leftId.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.getFightHandler().setId(PERSON_TYPE_LEFT, leftId.get());
            }
        });
        rightId.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                core.getFightHandler().setId(PERSON_TYPE_RIGHT, rightId.get());
            }
        });

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
        getActivity().getBinding().mainContent.incLeft.setOnLongClickListener(v -> {
            onDecLeft();
            return false;
        });
        getActivity().getBinding().mainContent.incRight.setOnLongClickListener(v -> {
            onDecRight();
            return false;
        });
        getActivity().getBinding().mainMenuLay.leftCard.setOnLongClickListener(v -> {
            onCancelLeft();
            return false;
        });
        getActivity().getBinding().mainMenuLay.rightCard.setOnLongClickListener(v -> {
            onCancelRight();
            return false;
        });
        getActivity().getBinding().mainMenuLay.leftCardP.setOnLongClickListener(v -> {
            onPCancelLeft();
            return false;
        });
        getActivity().getBinding().mainMenuLay.rightCardP.setOnLongClickListener(v -> {
            onPCancelRight();
            return false;
        });
//        getActivity().getBinding().competLay.competName.setText(SettingsManager.getValue("COMPETITION", ""));
//        getActivity().getBinding().competLay.competName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                core.sendToSM(CommandHelper.setCompetition(s.toString()));
//                SettingsManager.setValue("COMPETITION", s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        videoProgress.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable observable, int i) {
//                core.sendToSM(CommandHelper.notifyPlayer(PLAYER_PAUSE, videoSpeed.get(), videoProgress.get()));
//            }
//        });
//        videoSpeed.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable observable, int i) {
//                Log.wtf("SPEED", videoSpeed.get() + "");
//                core.sendToSM(CommandHelper.notifyPlayer(PLAYER_START, videoSpeed.get(), videoProgress.get()));
//            }
//        });
//        final List<String> items = new ArrayList<>();
//        for (int i = 0; i <= 100; i++) {
//            items.add(String.valueOf(i));
//        }
//        getActivity().getBinding().playerLay.seekSb.setItems(items);
//        getActivity().getBinding().playerLay.seekSb.setMinSelectableIndex(0);
//        getActivity().getBinding().playerLay.seekSb.setMaxSelectableIndex(100);
//        getActivity().getBinding().playerLay.seekSb.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
//            @Override
//            public void onWheelItemChanged(WheelView wheelView, int position) {
//
//            }
//
//            @Override
//            public void onWheelItemSelected(WheelView wheelView, int position) {
//                videoProgress.set(position);
//            }
//        });
//        final List<String> items2 = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            items2.add(String.valueOf(i));
//        }
//        getActivity().getBinding().playerLay.speedSb.setItems(items2);
//        getActivity().getBinding().playerLay.speedSb.setMinSelectableIndex(0);
//        getActivity().getBinding().playerLay.speedSb.setMaxSelectableIndex(9);
//        getActivity().getBinding().playerLay.speedSb.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
//            @Override
//            public void onWheelItemChanged(WheelView wheelView, int position) {
//
//            }
//
//            @Override
//            public void onWheelItemSelected(WheelView wheelView, int position) {
//                videoSpeed.set(position + 1);
//            }
//        });
//        configLeft.setIndicator(activity.findViewById(R.id.left_fighter_progress_bar));
//        configLeft.setAdapter(new FightersAutoCompleteAdapter(activity, true));
//        configLeft.setListener((adapterView, view, position, id) -> {
//            ListUser listUser = (ListUser) adapterView.getItemAtPosition(position);
//            activity.getBinding().namesLay.leftFighter.setText(listUser.name);
//            configRight.getAdapter().setExcludeUser(listUser);
//            saveFightData();
//        });
//        configLeft.setWatcher(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                configRight.getAdapter().setExcludeUser(null);
//                leftName.set(charSequence.toString());
//                saveFightData();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        configRight.setIndicator(activity.findViewById(R.id.right_fighter_progress_bar));
//        configRight.setAdapter(new FightersAutoCompleteAdapter(activity, true));
//        configRight.setListener((adapterView, view, position, id) -> {
//            ListUser listUser = (ListUser) adapterView.getItemAtPosition(position);
//            activity.getBinding().namesLay.rightFighter.setText(listUser.name);
//            configLeft.getAdapter().setExcludeUser(listUser);
//            saveFightData();
//        });
//        configRight.setWatcher(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                configLeft.getAdapter().setExcludeUser(null);
//                rightName.set(charSequence.toString());
//                saveFightData();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
//                getActivity().getBinding().protocolLay.fightResultRecycler.getContext(),
//                R.color.white, DividerItemDecoration.VERTICAL_LIST);
//        getActivity().getBinding().protocolLay.fightResultRecycler.addItemDecoration(dividerItemDecoration);
//        getActivity().getBinding().protocolLay.fightResultRecycler.setLayoutManager(llm);
//        adapter = new FightActionsAdapter(getActivity(), false);
//        adapter.setOnItemClickListener(this);
//        getActivity().getBinding().protocolLay.fightResultRecycler.setAdapter(adapter);
        wiFiHelper = new WiFiHelper(getActivity());
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (action != null && action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (info != null && info.isConnected()) {
                        Log.wtf("NET CHNG", "+");
                        if (checkWifiOnAndConnected() && core.getConnectedSm() == null) {
                            Log.wtf("WIFI ON", "+");
                            if (((WifiManager) Objects.requireNonNull(getActivity()
                                    .getApplicationContext().getSystemService(WIFI_SERVICE)))
                                    .getConnectionInfo().getSSID()
                                    .equals("\"" + requiredSSID + "\"")) {
                                core.initializeDiscovery();
                            } else {
                                Log.wtf("SSID", ((WifiManager) Objects.requireNonNull(getActivity()
                                        .getApplicationContext().getSystemService(WIFI_SERVICE)))
                                        .getConnectionInfo().getSSID() + "|||" + ("\"" + requiredSSID + "\""));

                            }
                        } else {
                            Log.wtf("WIFI NOT READY", "+");
                        }
                    }
                }
            }
        };
//        SwitchCompat camTarget = getActivity().getBinding().videoLay.videoTargetSwitch;
//        camTarget.setChecked(false);
//        camTarget.setOnCheckedChangeListener((compoundButton, b) -> core.sendToSM(new SetCamTargetCommand(b).getBytes()));
//        core.startUSB();
        smAdapter = new SMAdapter(getActivity());
        smAdapter.setClickListener(this);
        getActivity().getBinding().syncLay.smRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        getActivity().getBinding().syncLay.smRecycler.setAdapter(smAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        switch (screenState.get()) {
            case SCREEN_SYNC_LAY:
//                mCodeScanner.startPreview();
                break;
//            case SCREEN_SCORE_LAY:
//                getActivity().getBinding().scoreLay.scoreLeft.setValue(leftScore.get());
//                getActivity().getBinding().scoreLay.scoreRight.setValue(rightScore.get());
//                break;
//            case SCREEN_MENU_FIGHT_ACTIONS:
//                getActivity().getBinding().menuFightActions.timersDecMinT.setValue(timerMinDec.get());
//                getActivity().getBinding().menuFightActions.timersUnitMinT.setValue(timerMinUnit.get());
//                getActivity().getBinding().menuFightActions.timersDecSecT.setValue(timerSecDec.get());
//                getActivity().getBinding().menuFightActions.timersUnitSecT.setValue(timerSecUnit.get());
//                getActivity().getBinding().menuFightActions.periodNpTimers.setValue(period.get());
//                break;
        }
//        getActivity().getBinding().bluetoothLay.btSwitch.setChecked(false);
//        getActivity().getBinding().bluetoothLay.btSwitch.addSwitchObserver
//                ((switchView, isChecked) -> core.sendToSM(new SetBluetoothCommand(isChecked ? 1 : 0).getBytes()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getNetworkSsid().isEmpty()) {
            routeNetworkRequestsThroughWifi(getNetworkSsid());
        }
        if (!core.isUSBMode.get()) {
            if (core.getConnectedSm() != null) {
                Log.wtf("SYNCED", "+");
                syncState.set(SYNC_STATE_SYNCED);
            } else {
                screenState.set(SCREEN_SYNC_LAY);
                syncState.set(SYNC_STATE_SYNCING);
//                if (
////                        !TextUtils.isEmpty(SettingsManager.getValue(SM_CODE, "")) &&
//                        !TextUtils.isEmpty(SettingsManager.getValue(SM_IP, ""))) {
//                    Log.wtf("Resume","SYNCing");
//                    core.tryToConnect();
//                } else {
                    Log.wtf("RESUME", "No SM IP");
                    uiHandler.postDelayed(() -> {
                        syncState.set(SYNC_STATE_NONE);
                    }, 3000);
//                }
            }
            IntentFilter wifiFilter = new IntentFilter();
            wifiFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
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
        core.getBackupHelper().copyFight(leftName.get() + "_" +
                rightName.get() + "_" +
                new SimpleDateFormat("HH_mm", Locale.getDefault()).format(Calendar.getInstance().getTime()));
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
        tempProtocol.clear();
        core.sendToSM(new ProtocolRequestCommand().getBytes());
    }

    public void restoreFromExisted(FightData info) {
//            timeMillisecs.set((int) info.getmCurrentTime());
//        reset();
        core.isInRestore = true;
        core.getFightHandler().resetFightData();
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fightId = SettingsManager.getValue(UNFINISHED_FIGHT, "");
                leftScore.set(info.getLeftFighter().getScore());
                rightScore.set(info.getRightFighter().getScore());
                period.set(info.getmCurrentPeriod());
//                core.sendToSM(CommandHelper.setTimer(info.getmCurrentTime(), 0));
                rightName.set(info.getRightFighter().getName());
                leftName.set(info.getLeftFighter().getName());
                core.getFightHandler().setName(PERSON_TYPE_LEFT, leftName.get());
                core.getFightHandler().setName(PERSON_TYPE_RIGHT, rightName.get());
                priority.set(info.getmPriority());
                leftId.set(info.getLeftFighter().getId());
                rightId.set(info.getRightFighter().getId());
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
                    } else {
                        leftCard.set(CARD_STATE_NONE);
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
                    } else {
                        rightCard.set(CARD_STATE_NONE);
                    }
                }
//                if (isIndividual) {
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
                        } else {
                            leftPCard.set(CARD_STATE_NONE);
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
                        } else {
                            rightPCard.set(CARD_STATE_NONE);
                        }
                    }
//                } else {
//                    core.getFightHandler().restorePCards();
//                }
                core.sendToSM(CommandHelper.confirmNames());
                videosLeft.set(info.getmVideoLeft());
                videosRight.set(info.getmVideoRight());
                isIndividual = info.getEthCompetType().equals("I");
            }
        }, 700);
        uiHandler.postDelayed(() -> core.isInRestore = false, 1200);
    }

    private void timeNotifySM02() {
        int timeToSend;
        if (timerMode.get() == TIMER_MODE_MAIN) {
            timeToSend = timeMillisecs.get();
            Log.wtf("TIME NOTIFY", timeToSend + "");
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

    }

    //View section

    public void onTimerStopClick() {
        if (System.currentTimeMillis() - lastTimerTS > 200) {
            core.vibr();
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
            core.vibr();
            if (!(cyranoState.get() != CyranoState.Off && ethState.get().equals("W"))) {
                core.sendToSM(CommandHelper.startTimer(true));
                isPassiveBtnVisible.set(false);
                lastTimerTS = System.currentTimeMillis();
                isVideoBtnVisible.set(false);
            }
        }
    }

    public void pauseBreak() {
        Log.wtf("PAUSE BREAK", "START");
        core.sendToSM(CommandHelper.startTimer(false));
        int tempTimeMillisecs = (lastTimerBeforePause == 0 ? defaultTime.get() :
                (timerMode.get() == TIMER_MODE_MEDICINE ? lastTimerBeforePause : defaultTime.get()));
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(tempTimeMillisecs);
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(tempTimeMillisecs) % 60;
        timerSec.set(seconds);
        timerMin.set(minutes);
        if (timerMode.get() == TIMER_MODE_PAUSE) {
            onPeriodNext();
        }
        timerMode.set(TIMER_MODE_MAIN);
        core.sendToSM(CommandHelper.setTimer(tempTimeMillisecs, timerMode.get() - 80));
        lastTimerBeforePause = 0;
    }

    public void onDecLeft() {
        if (System.currentTimeMillis() - lastScoreTS > 1000) {
            core.vibr();
            if (!(cyranoState.get() != CyranoState.Off && ethState.get().equals("W"))) {
                leftScore.set(leftScore.get() - 1);
//                getActivity().getBinding().mainContent.decLeft.setImageResource(DEC_SELECTED);
//                uiHandler.postDelayed(() -> getActivity().getBinding().mainContent.decLeft.setImageResource(DEC_USUAL), 1000);
                lastScoreTS = System.currentTimeMillis();
            }
        }
    }

    public void onDecRight() {
        if (System.currentTimeMillis() - lastScoreTS > 1000) {
            core.vibr();
            if (!(cyranoState.get() != CyranoState.Off && ethState.get().equals("W"))) {
                rightScore.set(rightScore.get() - 1);
//                getActivity().getBinding().mainContent.decRight.setImageResource(DEC_SELECTED);
//                uiHandler.postDelayed(() -> getActivity().getBinding().mainContent.decRight.setImageResource(DEC_USUAL), 1000);
                lastScoreTS = System.currentTimeMillis();
            }
        }
    }

    public void onIncLeft() {
        if (System.currentTimeMillis() - lastScoreTS > 1000) {
            core.vibr();
            if (!(cyranoState.get() != CyranoState.Off && ethState.get().equals("W"))) {
                leftScore.set(leftScore.get() + 1);
                lastScoreTS = System.currentTimeMillis();
                if (phrasesEnabled.get()) {
                    getActivity().showPhraseDialog(true);
                    isLastAndOutdated = false;
                }
            }
        }
    }

    public void onIncRight() {
        if (System.currentTimeMillis() - lastScoreTS > 1000) {
            core.vibr();
            if (!(cyranoState.get() != CyranoState.Off && ethState.get().equals("W"))) {
                rightScore.set(rightScore.get() + 1);
                lastScoreTS = System.currentTimeMillis();
                if (phrasesEnabled.get()) {
                    getActivity().showPhraseDialog(false);
                    isLastAndOutdated = false;
                }
            }
        }
    }

    public void onMenuMain(){
        core.vibr();
        screenState.set(SCREEN_MAIN_MENU);
        menuState.set(MENU_MODE_MAIN);
        activity.getBinding().mainMenuLay.mainMenuScroll.fullScroll(ScrollView.FOCUS_UP);
    }

    public void onSettingsBtn() {
        core.vibr();
        screenState.set(SCREEN_STATE_SETTINGS);
        settingsState.set(SETTINGS_MODE_MAIN);
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
        timeMillisecs.set((timerMin.get() * 60 + timerSec.get()) * 1000);
        timeNotifySM02();
    }

    public void onDefTimeAccepted() {
        defaultTime.set((timerDefMin.get() * 60 + timerDefSec.get()) * 1000);
        timeMillisecs.set(defaultTime.get());
        timerMin.set(timerDefMin.get());
        timerSec.set(timerDefSec.get());
        timeNotifySM02();
    }

    public void onDefPTimeAccepted() {
        defaultPassiveTime.set((timerPassiveMin.get()*60 + timerPassiveSec.get()) * 1000);
    }

    public void onMenuPause() {
        core.vibr();
        if (menuState.get() == MENU_MODE_PAUSE) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_PAUSE);
        }
    }

    public void onMenuCards() {
        core.vibr();
        if (menuState.get() == MENU_MODE_CARDS) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_CARDS);
        }
    }

    public void performPause() {
        core.vibr();
        lastTimerBeforePause = timeMillisecs.get();
        timerMode.set(TIMER_MODE_PAUSE);
        timeNotifySM02();
        screenState.set(SCREEN_MAIN);
        lastTimerTS = System.currentTimeMillis();
    }

    public void onMenuMedical() {
        core.vibr();
        lastTimerBeforePause = timeMillisecs.get();
        timerMode.set(TIMER_MODE_MEDICINE);
        timeNotifySM02();
        screenState.set(SCREEN_MAIN);
        lastTimerTS = System.currentTimeMillis();
    }


    public void onMenuPeriod() {
        core.vibr();
        if (menuState.get() == MENU_MODE_PERIOD) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_PERIOD);
        }
    }

    public void onMenuScore() {
        core.vibr();
        if (menuState.get() == MENU_MODE_SCORE) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_SCORE);
            focusOnView();
        }
    }

    public void onMenuTimer() {
        core.vibr();
        if (menuState.get() == MENU_MODE_TIMER) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_TIMER);
            timerSec.set( (int) TimeUnit.MILLISECONDS.toSeconds(timeMillisecs.get()) % 60);
            timerMin.set( (int) TimeUnit.MILLISECONDS.toMinutes(timeMillisecs.get()));
            focusOnView();
        }
    }

    public void onMenuPassive() {
//        if (weapon.get() != WEAPON_SABER) {
//            if (isServerOnline.get()) {
        onPassiveLock();
//            } else {
//                screenState.set(SCREEN_PASSIVE_LAY);
//            }
//        }
    }

    public void onMenuPriority() {
        core.vibr();
        if (menuState.get() == MENU_MODE_PRIORITY) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_PRIORITY);
        }
    }

    public void onMenuDeviceReset() {
        core.vibr();
        if (menuState.get() == MENU_MODE_RESET) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_RESET);
        }
    }

    public void onMenuEndBout() {
        //TODO
        onMenuDeviceReset();
    }

    public void onMenuCyrano() {
        core.vibr();
        if (menuState.get() == MENU_MODE_CYRANO) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_CYRANO);
        }
    }

    public void onMenuWeaponType() {
        core.vibr();
        if (menuState.get() == MENU_MODE_WEAPON) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_WEAPON);
            focusOnView();
        }
    }

    public void onMenuNames() {
        core.vibr();
        if (menuState.get() == MENU_MODE_NAMES) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_NAMES);
        }
    }

    public void onMenuProtocol() {
        core.vibr();
        if (menuState.get() == MENU_MODE_PROTOCOL) {
            menuState.set(MENU_MODE_MAIN);
        } else {
            menuState.set(MENU_MODE_PROTOCOL);
        }
    }

    public void onSettingsPeriodTime() {
        if (settingsState.get() == SETTINGS_MODE_PERIOD_TIME) {
            settingsState.set(SETTINGS_MODE_MAIN);
        } else {
            settingsState.set(SETTINGS_MODE_PERIOD_TIME);
        }
    }

    public void onSettingsPassiveTimer() {
        if (settingsState.get() == SETTINGS_MODE_PASSIVE_TIME) {
            settingsState.set(SETTINGS_MODE_MAIN);
        } else {
            settingsState.set(SETTINGS_MODE_PASSIVE_TIME);
        }
    }

    public void onSettingsPisteNum() {
        if (settingsState.get() == SETTINGS_MODE_PISTE_NUM) {
            settingsState.set(SETTINGS_MODE_MAIN);
        } else {
            settingsState.set(SETTINGS_MODE_PISTE_NUM);
        }
    }

    public void onSettingsLanguage() {
        if (settingsState.get() == SETTINGS_MODE_LANGUAGE) {
            settingsState.set(SETTINGS_MODE_MAIN);
        } else {
            settingsState.set(SETTINGS_MODE_LANGUAGE);
        }
    }

    public void onMenuPractice() {
        core.vibr();
        fightId = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        SettingsManager.setValue(UNFINISHED_FIGHT, fightId);
        reset();
    }

    public void reset() {
        core.vibr();
        core.sendToSM(CommandHelper.reset(defaultTime.get()));
        fightId = "";
        core.getFightHandler().resetFightData();
        timeMillisecs.set(defaultTime.get());
        SettingsManager.removeValue(CommonConstants.LAST_FIGHT_ID);
//        timeToDisplay.set(String.valueOf(timerDefMinDec.get() == 0 ? "" : timerDefMinDec.get()) + String.valueOf(timerDefMinUnit.get()) + ":" +
//                String.valueOf(timerDefSecDec.get()) + String.valueOf(timerDefSecUnit.get()));
        timerColor.set(COLOR_USUAL);
        leftPCard.set(CARD_STATE_NONE);
        rightPCard.set(CARD_STATE_NONE);
        leftCard.set(CARD_STATE_NONE);
        rightCard.set(CARD_STATE_NONE);
        leftName.set("");
        rightName.set("");
        leftId.set("");
        rightId.set("");
//        activity.getBinding().namesLay.leftFighter.setText("");
//        activity.getBinding().namesLay.rightFighter.setText("");
        leftScore.set(0);
        rightScore.set(0);
        timerMin.set(0);
        timerSec.set(0);
        period.set(1);
        screenState.set(SCREEN_MAIN);
        priority.set(PERSON_TYPE_NONE);
        core.getFightHandler().resetFightData();
    }

    public void onMenuDisconnect() {
        core.vibr();
        core.onDisconnect();
    }

    public void onMenuPhrases() {
        //TODO later!!!
    }

    public void onPeriodNext() {
        core.vibr();
        period.set(period.get() + 1);
        timerMin.set(timerDefMin.get());
        timerSec.set(timerDefSec.get());
        timeNotifySM02();
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
        core.sendToSM(CommandHelper.swap());
        screenState.set(SCREEN_MAIN);
    }

    public void onPriority() {
        if (priority.get() == PERSON_TYPE_NONE) {
            priority.set(new Random().nextInt() % 2 == 0 ? PERSON_TYPE_LEFT : PERSON_TYPE_RIGHT);
            timeMillisecs.set(60000);
            timeNotifySM02();
        } else {
            priority.set(PERSON_TYPE_NONE);
        }
        onCloseBtn();
    }

    public void onPriorityNone() {
        priority.set(PERSON_TYPE_NONE);
    }

    public void onNamesConfirmed() {
        core.getFightHandler().setName(PERSON_TYPE_LEFT, leftName.get());
        core.getFightHandler().setName(PERSON_TYPE_RIGHT, rightName.get());
        uiHandler.postDelayed(() -> core.sendToSM(CommandHelper.confirmNames()), 600);
        onCloseBtn();
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
        //TODO implement player stop on any press in menu
//        if (screenState.get() == SCREEN_REPLAYS_LAY) {
//            core.sendToSM(CommandHelper.notifyPlayer(CommandsContract.PLAYER_STOP, videoSpeed.get(), videoProgress.get()));
//        }
        core.vibr();
        screenState.set(SCREEN_MAIN);
        if (cyranoState.get() == CyranoState.FightReceived) {
            cyranoState.set(CyranoState.JustEnabled);
        }
    }

    public void onSettingsCloseButton() {
        core.vibr();
        screenState.set(SCREEN_MAIN_MENU);
        menuState.set(MENU_MODE_MAIN);
    }

    public void passiveCheck() {
        core.vibr();
        isPassive.set(!isPassive.get());
        core.notifyRCVisibility(isVideo.get(), isPhoto.get(), isPassive.get(), isCountry.get());
    }

    public void onVideoChange() {
        isVideo.set(!isVideo.get());
        core.notifyRCVisibility(isVideo.get(), isPhoto.get(), isPassive.get(), isCountry.get());
    }

    public void onFightEndStart() {
        //TODO union with end bout method
        core.vibr();
        if (leftScore.get() == rightScore.get() &&
                priority.get() == PERSON_TYPE_NONE && isIndividual) {
            FightCantEndDialog.show(getActivity(), withSEMI.get());
        } else {
            ConfirmationDialog.show(getActivity(), END_FIGHT_MESSAGE, "", getActivity().getResources().getString(R.string.fight_end_ask));
        }
    }

    public void onFightFinishBegin() {
        cyranoState.set(CyranoState.FightEnded);
        core.sendToSM(new EthernetFinishAskCommand().getBytes());
        core.getBackupHelper().copyFight(leftName.get() + "_" +
                rightName.get() + "_" +
                new SimpleDateFormat("HH_mm", Locale.getDefault()).format(Calendar.getInstance().getTime()));
        if (!isIndividual) {
            core.getFightHandler().backupPCards();
        }
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
        if (!(cyranoState.get() != CyranoState.Off && ethState.get().equals("W"))) {
            core.vibr();
            leftScore.set(leftScore.get() + 1);
            rightScore.set(rightScore.get() + 1);
            lastScoreTS = System.currentTimeMillis();
        }
    }

    public void onVideoBtn() {
        core.sendToSM(new GetVideosCommand().getBytes());
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
        core.onDisconnect();
//        currentSMIp = "";
        requiredSSID = "";
        wiFiHelper.setRequiredSSID(requiredSSID);
    }

    public void onVideoCloseBtn() {
//        if (previousScreen == SCREEN_STATE_PROTOCOL) {
//            onMenuProtocol();
//            core.sendToSM(CommandHelper.notifyPlayer(CommandsContract.PLAYER_STOP, videoSpeed.get(), videoProgress.get()));
//        } else {
//            onCloseBtn();
//        }
        if (!isVideoReady.get()) {
            core.sendToSM(CommandHelper.transferAbort());
            videoProgress.set(0);
            videoSpeed.set(4);
//            getActivity().getBinding().playerLay.seekSb.selectIndex(videoProgress.get());
//            getActivity().getBinding().playerLay.speedSb.selectIndex(videoSpeed.get());
        }
    }

    //TODO FILL METHODS

    public void fightApplyOk() {
        core.vibr();
        screenState.set(SCREEN_MAIN);
        cyranoState.set(CyranoState.FightActive);
        uiHandler.postDelayed(() ->
                core.sendToSM(new EthernetApplyFightCommand(ethNecessaryInfo).getBytes()), 500);
        uiHandler.postDelayed(() -> core.sendToSM(CommandHelper.confirmNames()), 700);
        dispDataFirst.set("");
        dispDataSecond.set("");
    }


    //TODO SWAP 1 AND 0 in NEXTPREVCMD values
    public void fightNext() {
        core.vibr();
        core.sendToSM(new EthNextPrevCommand(1).getBytes());
        cyranoState.set(CyranoState.FightQueried);
        dispDataFirst.set("");
        dispDataSecond.set("");

    }

    public void fightPrev() {
        core.vibr();
        core.sendToSM(new EthNextPrevCommand(0).getBytes());
        cyranoState.set(CyranoState.FightQueried);
        dispDataFirst.set("");
        dispDataSecond.set("");
    }

    public void fightRemove() {
        core.vibr();
        //TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        core.sendToSM(new EthNextPrevCommand(1).getBytes());
//        screenState.set(SCREEN_STATE_WAITING);
    }

    public void fightRemoveStart() {
        core.vibr();
        ConfirmationDialog.show(getActivity(), REMOVE_FIGHT_MESSAGE, "", getActivity().getResources().getString(R.string.wait_bout) + "?");
    }

    public void goToNewFight() {
        core.vibr();
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        goToNewScreen = true;
        getActivity().startActivity(intent);
        SettingsManager.removeValue(UNFINISHED_FIGHT);
    }

    public void goToNewCyrano() {

        //TODO handle button fight on main screen
    }

    public void exitCyranoMode() {
        //TODO introduce boolean or use existed
        cyranoState.set(CyranoState.Off);
    }

    public void onSyncCancel() {
        core.vibr();
        syncState.set(SYNC_STATE_NONE);
        Log.wtf("DISCONNECT", "ON SYNC CANCEL");
        core.onDisconnect();
//        mCodeScanner.startPreview();
    }

    public void onSyncRequested() {
        core.vibr();
        syncState.set(SYNC_STATE_SYNCING);
    }

    public void onExitApp() {
        core.vibr();
        ConfirmationDialog.show(getActivity(), EXIT_MESSAGE, "",
                getActivity().getResources().getString(R.string.exit_app_question));
    }

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr != null) {
            if (wifiMgr.isWifiEnabled()) {

                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                Log.wtf("check wifi INFO: ", wifiInfo.toString());
                return wifiInfo.getNetworkId() != -1;
            } else {
                return false;
            }
        }

        return false;
    }

    private void focusOnView(){
        getActivity().getBinding().mainMenuLay.mainMenuScroll.post(() -> getActivity().getBinding().mainMenuLay.mainMenuScroll.fullScroll(ScrollView.FOCUS_DOWN));
    }

    //Core section

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void messageReceived(byte command, byte[] message) {
        switch (command) {
            case RESTORE_FIGHT_CMD:
                byte[] restoreBuf = new byte[message.length];
                System.arraycopy(message, 0, restoreBuf, 0, message.length);
                String restoreString = new String(restoreBuf, StandardCharsets.UTF_8);
                Log.wtf("RESTORE STRING", restoreString);
                Gson gson = new Gson();
                FightData restoreData = gson.fromJson(restoreString, FightData.class);
                restoreFromExisted(restoreData);
                Log.wtf("DATA STATE", restoreData.getEthStatus());
                cyranoState.set(CyranoState.FightActive);
                break;
            case PROTOCOL_RESPONSE:
                byte[] protocolBuf = new byte[message.length-2];
                System.arraycopy(message, 2, protocolBuf, 0, message.length-2);
                final char[] charBuffer = new char[protocolBuf.length];
                StringBuilder builder = new StringBuilder();
                InputStream stream = new ByteArrayInputStream(protocolBuf);
                try {
                    Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
                    for (;;) {
                        int rsz = in.read(charBuffer, 0, charBuffer.length);
                        if (rsz < 0) break;
                        builder.append(charBuffer, 0, rsz);
                    }
                } catch (UnsupportedEncodingException e1) {

                } catch (IOException e2) {

                }
                String protocolString = builder.toString();
                Gson protocolGson = new Gson();
                FightActionData data = protocolGson.fromJson(protocolString, FightActionData.class);
                Log.wtf("IN PROTO", message[1] + " of " + message[0] + "   || " + protocolString);
                tempProtocol.ensureCapacity(message[0]);
                tempProtocol.add(data);
                if (tempProtocol.size() - 1 == message[0]) {
                    activity.runOnUiThread(() -> adapter.setData(tempProtocol));
                }
//                activity.runOnUiThread(() -> {
//                    adapter.setData(data.getActionsList());
//                    adapter.notifyDataSetChanged();
//
//                    getActivity().getBinding().protocolLay.leftScore.setText(String.valueOf(data.getLeftFighter().getScore()));
//                    getActivity().getBinding().protocolLay.rightScore.setText(String.valueOf(data.getRightFighter().getScore()));
//
//                    getActivity().getBinding().protocolLay.leftFighter.setText(data.getLeftFighter().getName());
//                    getActivity().getBinding().protocolLay.rightFighter.setText(data.getRightFighter().getName());
//                });

                break;
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
                                timeToDisplay.get() + "_" + period.get();
//                        core.sendToCams(TIMER_STOP_UDP + "\0" + filename);
//                                isVideoReady.set(false);
                        if (isPassive.get()) {
                            isPassiveBtnVisible.set(true);
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
                if (cyranoState.get()!=CyranoState.FightActive &&
                cyranoState.get() != CyranoState.FightEnded) {
                    //TODO create and fill fight data to store it later
                    byte[] dispBuf = new byte[message[0] & 0xFF];
                    System.arraycopy(message, 1, dispBuf, 0, message[0] & 0xFF);
                    String disp = new String(dispBuf, StandardCharsets.UTF_8);
                    Log.wtf("DISP", disp);
                    if (dispHandler.updateFromCommand(disp)) {
                        dispTempData = dispHandler.getEthFightData();
                        pisteNum.set(dispHandler.getInfoMain().getPiste());
                        isPisteNumOn.set(true);
                        onPisteNumSet();
                        dispDataFirst.set(dispTempData.getLeftFighter().getName() + " - "
                                + dispTempData.getRightFighter().getName());
//                        dispDataSecond.set(dispTempData.getLeftFighter().getScore() + " : "
//                                + dispTempData.getRightFighter().getScore());
                        ethNecessaryInfo = dispHandler.getInfoMain().getPhase() + "|" +
                                dispHandler.getInfoMain().getPoulTab() + "|" +
                                dispHandler.getInfoMain().getMatchNumber() + "|" +
                                dispHandler.getInfoMain().getType() + "|" +
                                dispHandler.getInfoLeft().getId() + "|" +
                                dispHandler.getInfoLeft().getNation() + "|" +
                                dispHandler.getInfoRight().getId() + "|" +
                                dispHandler.getInfoRight().getNation();
                        if (dispHandler.getInfoMain().getType().equals("I")) {
                            isIndividual = true;
                        } else if (dispHandler.getInfoMain().getType().equals("T")) {
                            isIndividual = false;
                        }
                        cyranoState.set(CyranoState.FightReceived);
                        restoreFromExisted(dispTempData);
                    }
                }

                break;
            case ETH_ACK_NAK:
                if (cyranoState.get() == CyranoState.FightEnded) {
                    if (message[0] == 1) {
                        MessageDialog.show(getActivity(), FIGHT_FINISHED_OK, "", getActivity().getResources().getString(R.string.fight_end_accept));
                        if (fightFinishAskHandler != null) {
                            fightFinishAskHandler.finish();
                        }
                    } else if (message[0] == 0) {
                        FightCantEndDialog.show(getActivity(), withSEMI.get());
                        if (fightFinishAskHandler != null) {
                            fightFinishAskHandler.finish();
                        }
                    }
                    cyranoState.set(CyranoState.FightConfirmed);
                }
                break;
            case PASSIVE_MAX:
                core.vibrContiniously();
                break;
            case PAUSE_FINISHED:
                onPeriodNext();
                timerMode.set(TIMER_MODE_MAIN);
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
                byte[] videoNameBuf = new byte[message[0] & 0xFF];
                System.arraycopy(message, 1, videoNameBuf, 0, message[0] & 0xFF);
                String videoName = new String(videoNameBuf, StandardCharsets.UTF_8);
                if (isCamConnected.get()) {
                    isVideoBtnVisible.set(true);
                }
                Log.wtf("VIDEO READY RECEIVE", videoName);
//                }
                break;
            case DISCONNECT_TCP_CMD:
                onMenuDisconnect();
                break;
            case ADD_STATE:
                isCamConnected.set(message[0] == 1);
                if (message[0] != 1) {
                    isVideoBtnVisible.set(false);
                }
                if (message[1] == 0) {
                    core.getFightHandler().backupPCards();
                    cyranoState.set(CyranoState.Off);
                } else {
                    if (cyranoState.get() == CyranoState.Off) {
                        cyranoState.set(CyranoState.JustEnabled);
                    }
                }
                isNamesLocked.set(message[1] == 1);
                byte[] stateBytes = new byte[message[2]];
                System.arraycopy(message, 3, stateBytes, 0, message[2]);
                ethState.set(new String(stateBytes, StandardCharsets.UTF_8));
                if (message.length > message[2] + 3) {
                    isRepeaters.set(message[message.length - 1] == 1);
                }
                break;
            case LIST_VIDEOS_CMD:
                byte[] listBytes = new byte[message.length];
                System.arraycopy(message, 0, listBytes, 0, message.length);
                String listString = new String(listBytes, StandardCharsets.UTF_8);
                try {
                    JSONArray filesArray = new JSONArray(listString);
                    Log.wtf("JSON", filesArray.toString());
                    ReplaysDialog.show(getActivity(), filesArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ADD_OPT_RESPONSE:
                isAutoIncOn.set(message[0]==1);
                isPisteNumOn.set(message[1]==1);
                byte[] pNBytes = new byte[message[2]];
                System.arraycopy(message, 2, pNBytes, 0, message[2]);
                pisteNum.set(new String(pNBytes, StandardCharsets.UTF_8));
                break;
        }
    }

    void onReplaySelected(String replay) {
        core.sendToSM(CommandHelper.loadFile(replay));
    }

    public void onAutoIncSwitch() {
        core.vibr();
        core.sendToSM(new SwitchAutoIncCommand(isAutoIncOn.get()?0:1).getBytes());
        isAutoIncOn.set(!isAutoIncOn.get());
    }

    public void onLangSwitch(int lang) {
        core.vibr();
        core.sendToSM(new SwitchLangCommand(lang).getBytes());
    }

    public void onPisteNumSet() {
        core.vibr();
        core.sendToSM(new SetPisteNumCommand(Objects.requireNonNull(pisteNum.get()), isPisteNumOn.get()?1:0).getBytes());
    }

    @Override
    public void connectionLost() {

    }

    @Override
    public void devicesUpdated(ArrayList<Device> devices) {
        //TODO
    }

    void connectWPA(String pass) {
        wiFiHelper.connectWPA(pass);
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

    //Network temporary fix attempt

    private String getNetworkSsid() {
        WifiManager mWifiManager = (WifiManager) InspirationDayApplication.getApplication()
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = null;
        if (mWifiManager != null) {
            wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                return wifiInfo.getSSID().replace("\"", "");
            }
        }
        return "";
    }

    private void releaseNetworkRoute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mConnectivityManager.bindProcessToNetwork(null);
        }
    }

    private void createNetworkRoute(Network network) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mConnectivityManager.bindProcessToNetwork(network);
        }
    }

    private void unregisterNetworkCallback(ConnectivityManager.NetworkCallback networkCallback) {
        if (networkCallback != null) {
            try {
                mConnectivityManager.unregisterNetworkCallback(networkCallback);

            } catch (Exception ignored) {
            } finally {
                mNetworkCallback = null;
            }
        }
    }

    private void routeNetworkRequestsThroughWifi(String ssid) {
        mConnectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        unregisterNetworkCallback(mNetworkCallback);

        // new NetworkRequest with WiFi transport type
        NetworkRequest request = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        // network callback to listen for network changes
        ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {

            // on new network ready to use

            @Override
            public void onAvailable(@NonNull Network network) {
                if (getNetworkSsid().equals(ssid)) {
                    releaseNetworkRoute();
                    createNetworkRoute(network);
                } else {
                    releaseNetworkRoute();
                }
            }
        };
        mConnectivityManager.requestNetwork(request, mNetworkCallback);
    }

    @Override
    public void onItemClick(View view, FightActionData fightData) {
        onReplaySelected(fightData.getVideoUrl());
    }

    @Override
    public void onSMChoose(NsdServiceInfo itemName) {
        core.initSMConnection(itemName);
    }
}