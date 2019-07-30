package ru.inspirationpoint.remotecontrol.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;

import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;


import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.ListUser;
import ru.inspirationpoint.remotecontrol.manager.FightersAutoComplConfig;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.SetNameCommand;
import ru.inspirationpoint.remotecontrol.manager.coreObjects.Device;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FighterData;
import ru.inspirationpoint.remotecontrol.manager.handlers.CoreHandler;
import ru.inspirationpoint.remotecontrol.manager.helpers.ClockRefreshHelper;
import ru.inspirationpoint.remotecontrol.manager.helpers.Helper;
import ru.inspirationpoint.remotecontrol.manager.helpers.MetricsHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;
import ru.inspirationpoint.remotecontrol.ui.adapter.FightersAutoCompleteAdapter;
import ru.inspirationpoint.remotecontrol.ui.dialog.MessageDialog;
import ru.inspirationpoint.remotecontrol.ui.dialog.SyncDialog;

import static android.content.Context.LOCATION_SERVICE;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEV_STRING_TYPE_RC;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEV_TYPE_RC;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.PHRASES_METRIC;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_LEFT;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_RIGHT;


public class NewFightActivityVM extends ActivityViewModel<NewFightActivity> implements CoreHandler.CoreServerCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    public static final int SYNC_STATE_NONE = 2;
    public static final int SYNC_STATE_SYNCING = 3;
    public static final int SYNC_STATE_SYNCED = 4;
    public final FightersAutoComplConfig configLeft = new FightersAutoComplConfig();
    public final FightersAutoComplConfig configRight = new FightersAutoComplConfig();
    public ObservableInt syncState = new ObservableInt(2);
    public ObservableField<String> ip = new ObservableField<>();
    public ObservableBoolean isDark = new ObservableBoolean();
    public ObservableField<String> date = new ObservableField<>();
    public ObservableField<String> time = new ObservableField<>();
    public ObservableBoolean phrasesChecked = new ObservableBoolean();
    //    public ObservableBoolean commandsChecked = new ObservableBoolean();
    public ObservableField<String> place = new ObservableField<>();
    private ListUser leftFighter = new ListUser();
    private ListUser rightFighter = new ListUser();
    private String ownerName = "";
    private SyncDialog dialog;
    private boolean accessLocationPermitted;
    private LocationManager locationManager;
    private Location location;
    private ClockRefreshHelper clockRefreshHelper;
//    public CamerasAdapter adapter;
    private MetricsHelper metricsHelper;
    private HashSet<String> metrics = new HashSet<>();
    public ObservableBoolean isReferee = new ObservableBoolean(false);
    public ObservableField<String> refereeName = new ObservableField<>();
    public CoreHandler core;
    private boolean isGoToFA = false;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            NewFightActivityVM.this.location = location;
            updatePlace();
            requestAddress();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            try {
                NewFightActivityVM.this.location = locationManager.getLastKnownLocation(provider);
                updatePlace();
                requestAddress();
            } catch (SecurityException ignored) {

            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public NewFightActivityVM(final NewFightActivity activity) {
        super(activity);
        InspirationDayApplication.getApplication().setMode(DEV_TYPE_RC);
        InspirationDayApplication.getApplication().setModeString(DEV_STRING_TYPE_RC);
        core = InspirationDayApplication.getApplication().getCoreHandler();
        core.setActivity(getActivity());
        core.setServerCallback(this);
//        adapter = new CamerasAdapter();
//        adapter.setListener((camera) -> getActivity().onCamClick(camera));
//        RecyclerView camRecycler = getActivity().findViewById(R.id.cameras_rv);
//        camRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        camRecycler.setAdapter(adapter);
//        adapter.setCameras(core.getConnectedCameras());
        clockRefreshHelper = new ClockRefreshHelper(getActivity(), getActivity().getBinding().time);
        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        //TODO switch to metrics
//        phrasesChecked.set(SettingsManager.getValue(CommonConstants.IS_PHRASES_ENABLED, false));
//        commandsChecked.set(SettingsManager.getValue(IS_COMMANDS_ENABLED, false));
//        commandsChecked.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                if (!SettingsManager.getValue(IS_COMMANDS_ENABLED, false) && commandsChecked.get()) {
//                    getActivity().askForCommands();
//                }
//            }
//        });
        date.set(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date()));
        time.set(Helper.timeToString(new Date()));
        isDark.set(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false));
        configLeft.setIndicator(activity.findViewById(R.id.left_fighter_progress_bar));
        configLeft.setAdapter(new FightersAutoCompleteAdapter(activity, true));
        configLeft.setListener((adapterView, view, position, id) -> {
            ListUser listUser = (ListUser) adapterView.getItemAtPosition(position);
            activity.getBinding().leftFighter.setText(listUser.name);
            activity.getBinding().leftFighter.setTextColor(activity.getResources().getColor(R.color.textColorSecondary));
            leftFighter = listUser;
            configRight.getAdapter().setExcludeUser(listUser);
        });
        configLeft.setWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                configRight.getAdapter().setExcludeUser(null);
                leftFighter = new ListUser();
                leftFighter.name = charSequence.toString();
                leftFighter._id = "";
                core.sendToSM(CommandHelper.setName(PERSON_TYPE_LEFT, charSequence.toString()));
                activity.getBinding().leftFighter.setTextColor(!isDark.get()
                        ? activity.getResources().getColor(R.color.textColorPrimary) : activity.getResources().getColor(R.color.whiteCard));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        configRight.setIndicator(activity.findViewById(R.id.right_fighter_progress_bar));
        configRight.setAdapter(new FightersAutoCompleteAdapter(activity, true));
        configRight.setListener((adapterView, view, position, id) -> {
            ListUser listUser = (ListUser) adapterView.getItemAtPosition(position);
            activity.getBinding().rightFighter.setText(listUser.name);
            activity.getBinding().rightFighter.setTextColor(activity.getResources().getColor(R.color.textColorSecondary));
            rightFighter = listUser;
            configLeft.getAdapter().setExcludeUser(listUser);
        });
        configRight.setWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                configLeft.getAdapter().setExcludeUser(null);
                rightFighter = new ListUser();
                rightFighter.name = charSequence.toString();
                rightFighter._id = "";
                core.sendToSM(new SetNameCommand(PERSON_TYPE_RIGHT, charSequence.toString()).getBytes());
                activity.getBinding().rightFighter.setTextColor(!isDark.get()
                        ? activity.getResources().getColor(R.color.textColorPrimary) : activity.getResources().getColor(R.color.whiteCard));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
//        activity.getBinding().eventsCheck.setButtonDrawable(isDark.get() ? R.drawable.checkbox_selector_dark : R.drawable.checkbox_selector_light);

        metricsHelper = new MetricsHelper();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (core.getConnectedSm() != null) {
            syncState.set(SYNC_STATE_SYNCED);
        } else {
            syncState.set(SYNC_STATE_NONE);
        }
    }

    public void onStartBtnClick(boolean withSEMI) {
        isGoToFA = true;
        Intent intent = new Intent(getActivity(), FightActivity.class);
        if (withSEMI) {
            intent.putExtra("SEMI", true);
        } else {
            String left = leftFighter.name;
            if (left == null)
                left = "";
            if (left.startsWith(" ")) {
                left = left.substring(1);
            } else if (left.endsWith(" ")) {
                left = left.substring(0, left.length() - 1);
            }
            String right = rightFighter.name;
            if (right == null)
                right = "";
            if (right.startsWith(" ")) {
                right = right.substring(1);
            } else if (right.endsWith(" ")) {
                right = right.substring(0, right.length() - 1);
            }
            //TODO resume
//            if (TextUtils.isEmpty(left) || TextUtils.isEmpty(right)) {
//                MessageDialog.show(getActivity(), 0, getActivity().getString(R.string.error), getActivity().getString(R.string.enter_users_name));
//                return;
//            }
//            if (phrasesChecked.get()) {
//                metrics.add(PHRASES_METRIC);
//            }
//            metricsHelper.applyMetrics(metrics);
            FightData fightData = new FightData("", new Date(), new FighterData(leftFighter._id, left), new FighterData(rightFighter._id, right),
                    place.get(), SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
//            DataManager.instance().setCurrentFight(fightData);
            intent.putExtra("SEMI", false);
            intent.putExtra("LEFT", left);
            intent.putExtra("RIGHT", right);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }

    public void onSyncReset() {
        //TODO disconnect SM from core
        getActivity().onSyncReset();
    }

    public void onSyncCodeSet(final int code) {
        //TODO remove (also from activity)
    }

    public void onSyncCancel() {
        //TODO remove (also from activity)
    }

    public void onSyncRequired() {
        getActivity().onQRScanNeed();
    }

    public void onQRScanned(String scanResult) {
        //TODO use SM02 code to authorise as single RC
    }

    public void onPhraseChanged(boolean check) {
        phrasesChecked.set(check);
    }

    public void onRefereeDisconnectClick() {
        refereeName.set("");
        isReferee.set(false);
        //TODO disconnect referee from core
    }

    public void onStartClick() {
        String left = leftFighter.name;
        if (left == null)
            left = "";
        if (left.startsWith(" ")) {
            left = left.substring(1);
        } else if (left.endsWith(" ")) {
            left = left.substring(0, left.length() - 1);
        }
        String right = rightFighter.name;
        if (right == null)
            right = "";
        if (right.startsWith(" ")) {
            right = right.substring(1);
        } else if (right.endsWith(" ")) {
            right = right.substring(0, right.length() - 1);
        }
        if (TextUtils.isEmpty(left) || TextUtils.isEmpty(right)) {
            MessageDialog.show(getActivity(), 0, getActivity().getString(R.string.error), getActivity().getString(R.string.enter_users_name));
            return;
        }
        if (phrasesChecked.get()) {
            metrics.add(PHRASES_METRIC);
        }
        metricsHelper.applyMetrics(metrics);
        FightData fightData = new FightData("", new Date(), new FighterData(leftFighter._id, left), new FighterData(rightFighter._id, right),
                place.get(), SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
//        DataManager.instance().setCurrentFight(fightData);
        //TODO schedule referee, repeaters & cameras
//            if (!selectedRefereeIp.equals("")) {
//                new Thread(() -> {
//                    for (Camera camera : InspirationDayApplication.getApplication().getCameras()) {
//                        try {
//                            udp.sendTargetMessage("REFRST" + "\0" + selectedRefereeIp + "\08080",
//                                    camera.ip.get());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    try {
//                        udp.sendTargetMessage("FIBGN" + "\0" + fightData.getOwner() + "\0"
//                                        + fightData.getPlace() + "\0" + fightData.getLeftFighter().getName()
//                                        + "\0" + fightData.getRightFighter().getName(),
//                                selectedRefereeIp);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }).start();
//            }
        //TODO resume to it when FA will be ready
//        Intent intent = new Intent(getActivity(), FightActionActivity.class);
//        getActivity().startActivity(intent);
//        finish();
    }

    //TODO make timer set to non-zero instead of btns etc.

    public void onLogoutClick() {
//        DataManager.instance().logout(new DataManager.RequestListener<LogoutResult>() {
//            @Override
//            public void onSuccess(LogoutResult result) {
//                SettingsManager.removeValue(CommonConstants.LAST_USER_ID_FIELD);
//                SettingsManager.removeValue(CommonConstants.LAST_USER_NAME_FIELD);
//                SettingsManager.removeValue(CommonConstants.LAST_USER_EMAIL_FIELD);
//
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                getActivity().startActivity(intent);
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                MessageDialog.show(getActivity(), 0, getActivity().getResources().getString(R.string.error), message);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//
//            }
//        },SettingsManager.getValue(CommonConstants.SESSION_ID_FIELD, ""));
    }

    @Override
    public void onResume() {
        super.onResume();
        InspirationDayApplication.getApplication().resetRepeaters();
        InspirationDayApplication.getApplication().setSMVideoTarget(true);
        clockRefreshHelper.startRefresh();
        ownerName = SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, "");
//        if (core.getConnectedSm() != null) {
//            syncState.set(SYNC_STATE_SYNCED);
//        } else {
//            syncState.set(SYNC_STATE_NONE);
//        }
        if (SettingsManager.getValue(CommonConstants.LOCALE_CHANGED_FIELD, true)) {
            SettingsManager.setValue(CommonConstants.LOCALE_CHANGED_FIELD, false);
            getActivity().recreate();
        }
        if (SettingsManager.getValue(CommonConstants.IS_THEME_CHANGED, true)) {
            SettingsManager.setValue(CommonConstants.IS_THEME_CHANGED, false);
            getActivity().recreate();
        }
        checkPermissions();
        updatePlace();
//        ((WifiManager) Objects.requireNonNull(getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE))).setWifiEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        clockRefreshHelper.finishRefresh();
    }

//    @Override
//    public void onDestroy() {
//        Log.wtf("ON DESTROY", "++");
//        if (Build.VERSION.SDK_INT >= 21)
//            getActivity().finishAndRemoveTask();
//        else
//            finish();
//        System.exit(0);
//        super.onDestroy();
//    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            accessLocationPermitted = false;
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_LOCATION);
        } else {
            accessLocationPermitted = true;
            requestLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_LOCATION) {
            accessLocationPermitted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        requestLocationUpdates();
    }

    private synchronized void requestLocationUpdates() {
        if (accessLocationPermitted) {
            try {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            } catch (SecurityException ignored) {

            }

            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 10, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 5, 10, locationListener);
            } catch (SecurityException ignored) {

            }
            updatePlace();
            if (location != null) {
                requestAddress();
            }
        }
    }

    private void updatePlace() {
        if (accessLocationPermitted) {
            if (TextUtils.isEmpty(place.get())) {
                place.set(getActivity().getString(R.string.detect_location));
            }
        } else {
            place.set(getActivity().getString(R.string.location_not_permitted));
        }
    }

    private void requestAddress() {
        if (location != null) {
//            DataManager.instance().getAddressByLocation(location.getLatitude(), location.getLongitude(), new DataManager.RequestListener<GetAddressByLocationResult>() {
//                @Override
//                public void onSuccess(GetAddressByLocationResult result) {
//                    place.set(result.address);
//                    updatePlace();
//                }
//
//                @Override
//                public void onFailed(String error, String message) {
//
//                }
//
//                @Override
//                public void onStateChanged(boolean inProgress) {
//
//                }
//            });
        }
    }

    public void onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.mode_diary:
//                Intent intent4 = new Intent(getActivity(), TrainingListActivity.class);
//                getActivity().startActivity(intent4);
//                getActivity().finish();
//                break;
//            case R.id.reserve_ref:
//                Intent intent5 = new Intent(getActivity(), RefereeStartActivity.class);
//                getActivity().startActivity(intent5);
//                getActivity().finish();
//                break;
            case R.id.mode_camera:
                break;
            case R.id.diary:
                Intent intent0 = new Intent(getActivity(), InfoActivity.class);
                getActivity().startActivity(intent0);
            case R.id.foil:
                //TODO send cmd to SM
                break;
            case R.id.epee:
                //TODO send cmd to SM
                break;
            case R.id.saber:
                //TODO send cmd to SM
                break;
            case R.id.off:
                //TODO send cmd to SM
                break;
            case R.id.settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.about_app:
                Intent intent2 = new Intent(getActivity(), AboutAppActivity.class);
                getActivity().startActivity(intent2);
                break;
                //TODO resume and check
//            case R.id.nav_item_lang:
//                if (LocaleHelper.getLanguage(getActivity()).equals("ru")) {
//                    LocaleHelper.setLocale(getActivity(), "en");
//                    ((SwitchCompat) getActivity().getBinding().navViewTraining.findViewById(R.id.drawer_switch))
//                            .setChecked(true);
//                    getActivity().getBinding().navViewTraining.getMenu().getItem(9).setTitle(getActivity().getResources().getString(R.string.en_language));
//                } else {
//                    LocaleHelper.setLocale(getActivity(), "ru");
//                    ((SwitchCompat) getActivity().getBinding().navViewTraining.findViewById(R.id.drawer_switch))
//                            .setChecked(false);
//                    getActivity().getBinding().navViewTraining.getMenu().getItem(9).setTitle(getActivity().getResources().getString(R.string.ru_language));
//                }
//                getActivity().recreate();
//                break;
        }
        getActivity().getBinding().drawerLayoutTrainings.closeDrawer(GravityCompat.START);
    }

    @Override
    public void messageReceived(byte command, byte[] message) {
        switch (command) {
            case CommandsContract.BROADCAST_TCP_CMD:
                BitSet bitSet = BitSet.valueOf(new byte[]{message[0]});
                final Byte weapon = Byte.parseByte("000000" + String.valueOf(bitSet.get(1) ? 1 : 0) + String.valueOf(bitSet.get(0) ? 1 : 0), 2);
                Log.wtf("WEAPON TYPE", weapon.intValue() + "|" + String.valueOf(bitSet.get(1) ? 1 : 0) + String.valueOf(bitSet.get(0) ? 1 : 0));
                SettingsManager.setValue(CommonConstants.WEAPON_TYPE, weapon.intValue());
                getActivity().runOnUiThread(() -> getActivity().changeSelectedWeapon(weapon.intValue()));
                break;
            case CommandsContract.SETWEAPON_TCP_CMD:
                SettingsManager.setValue(CommonConstants.WEAPON_TYPE, message[0]);
                getActivity().runOnUiThread(() -> getActivity().changeSelectedWeapon(message[0]));
                break;
            //TODO send initial fight data to SM
            case CommandsContract.SETNAME_TCP_CMD:
                break;
            case CommandsContract.SETPERIOD_TCP_CMD:
                break;
            case CommandsContract.SETSCORE_TCP_CMD:
                break;
            case CommandsContract.SETTIMER_TCP_CMD:
//                //TODO notify cameras from core & mb move it to more successful place
//                Intent intent = new Intent(getActivity(), FightActionActivity.class);
//                if (leftFighter != null) {
//                    intent.putExtra("leftServer", leftFighter._id.equals(""));
//                }
//                if (rightFighter != null) {
//                    intent.putExtra("rightServer", rightFighter._id.equals(""));
//                }
//                getActivity().startActivity(intent);
//                finish();
                break;
        }
    }

    @Override
    public void connectionLost() {
                syncState.set(SYNC_STATE_NONE);
                MessageDialog.show(getActivity(), 987, getActivity().getString(R.string.connection_lost_title),
                        getActivity().getString(R.string.connection_lost_out_fight));
    }

    @Override
    public void devicesUpdated(ArrayList<Device> devices) {
        //TODO
    }
}