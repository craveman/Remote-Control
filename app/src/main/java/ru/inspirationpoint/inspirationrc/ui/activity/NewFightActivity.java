package ru.inspirationpoint.inspirationrc.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.DataManager;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FightData;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FighterData;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.manager.helpers.JSONHelper;
import ru.inspirationpoint.inspirationrc.manager.helpers.UDPHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.CommandHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.TCPHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.CommandsContract;
import ru.inspirationpoint.inspirationrc.ui.adapter.FightersAutoCompleteAdapter;
import ru.inspirationpoint.inspirationrc.ui.dialog.MessageDialog;
import ru.inspirationpoint.inspirationrc.ui.view.FightersAutoCompleteTextView;
import server.schemas.requests.FightInput;
import server.schemas.responses.GetAddressByLocationResult;
import server.schemas.responses.ListUser;

public class NewFightActivity extends LocalAppCompatActivity implements MessageDialog.Listener, NavigationView.OnNavigationItemSelectedListener,
        TCPHelper.TCPListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    private static final int SYNC_STATE_NONE = 2;
    private static final int SYNC_STATE_SYNCING = 3;
    private static final int SYNC_STATE_SYNCED = 4;

    private LocationManager mLocationManager;
    private boolean mAccessLocationPermitted = false;
    private boolean mUpdateInProgress = false;
    private Location mLocation;
    private String mAddress = "";

    private FightersAutoCompleteTextView mLeftFighter;
    private FightersAutoCompleteTextView mRightFighter;
    private FightersAutoCompleteAdapter mLeftFighterAdapter;
    private FightersAutoCompleteAdapter mRightFighterAdapter;
    private TextView mPlaceTextView;
    private Button btnSync;
    private LinearLayout syncedLay;
    private ProgressBar syncProgress;
    private TextView syncedName;
    private Map<String, String> addresses = Collections.synchronizedMap(new HashMap<String, String>());
    private UDPHelper udp;
    private Pinger pinger;
    private Handler handler;
    private String ipSelected;
    private TCPHelper tcpHelper;
    private int code;
    private Boolean isLeftFromServer = false;
    private Boolean isRightFromServer = false;
    private String left;
    private String right;
    private String ownerName;

    boolean mLeftInClick;
    boolean mRightInClick;

    private String mLeftFighterId = "";
    private String mRightFighterId = "";

    private DrawerLayout drawer;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            updatePlace();
            requestAddress();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            try {
                mLocation = mLocationManager.getLastKnownLocation(provider);
                updatePlace();
                requestAddress();
            } catch (SecurityException ignored) {

            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onReceive(byte[] message) {
        if (CommandHelper.getCommand(message) == CommandsContract.INIT_TCP_CMD) {
            try {
                tcpHelper.send(CommandHelper.setName(CommandsContract.PERSON_TYPE_REFEREE, ownerName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (CommandHelper.getCommand(message) == CommandsContract.SETNAME_TCP_CMD &&
                CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_LEFT) {
            try {
                tcpHelper.send(CommandHelper.setName(CommandsContract.PERSON_TYPE_RIGHT, isRightFromServer ? right : "! " + right + " !"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (CommandHelper.getCommand(message) == CommandsContract.SETNAME_TCP_CMD &&
                CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_RIGHT) {
            try {
                tcpHelper.send(CommandHelper.setPeriod(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (CommandHelper.getCommand(message) == CommandsContract.SETPERIOD_TCP_CMD) {
            try {
                tcpHelper.send(CommandHelper.setScore(CommandsContract.PERSON_TYPE_LEFT, 0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (CommandHelper.getCommand(message) == CommandsContract.SETSCORE_TCP_CMD &&
                CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_LEFT) {
            try {
                tcpHelper.send(CommandHelper.setScore(CommandsContract.PERSON_TYPE_RIGHT, 0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (CommandHelper.getCommand(message) == CommandsContract.SETSCORE_TCP_CMD &&
                CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_RIGHT) {
            try {
                tcpHelper.send(CommandHelper.setTimer(180000));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (CommandHelper.getCommand(message) == CommandsContract.SETTIMER_TCP_CMD) {
            Intent intent = new Intent(NewFightActivity.this, FightActivity.class);
            startActivity(intent);

            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_fight);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        configureToolbar();

        setCustomTitle(R.string.new_fight);

        handler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_trainings);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_training);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView mUserName = (TextView) headerView.findViewById(R.id.user_name);
        mUserName.setTextColor(!SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        mUserName.setText(SettingsManager.getValue(Constants.LAST_USER_NAME_FIELD, ""));

        TextView mUserEmail = (TextView) headerView.findViewById(R.id.user_email);
        mUserEmail.setText(SettingsManager.getValue(Constants.LAST_USER_EMAIL_FIELD, ""));
        mUserEmail.setTextColor(!SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));

        TextView logoutButton = (TextView) navigationView.findViewById(R.id.logout);
        logoutButton.setTextColor(!SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.instance().logout(null);
                finish();

                Intent intent = new Intent(NewFightActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        final boolean isDark = SettingsManager.getValue(Constants.IS_DARK_THEME, false);

        btnSync = (Button) findViewById(R.id.btn_sync);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pinger = new Pinger();
                        pinger.run();
                    }
                });
                t.start();
                changeSyncState(SYNC_STATE_SYNCING, null);
            }
        });

        syncedLay = (LinearLayout) findViewById(R.id.synced_lay);
        syncedName = (TextView) findViewById(R.id.synced_name);
        syncProgress = (ProgressBar) findViewById(R.id.sync_progress);

        mLeftFighterAdapter = new FightersAutoCompleteAdapter(this, true);
        mLeftFighter = (FightersAutoCompleteTextView) findViewById(R.id.left_fighter);
        if (isDark) {
            mLeftFighter.setDropDownBackgroundResource(R.color.colorPrimaryVeryDark);
            mLeftFighter.setTextColor(getResources().getColor(R.color.whiteCard));
        }
        mLeftFighter.setThreshold(1);
        mLeftFighter.setAdapter(mLeftFighterAdapter);
        mLeftFighter.setLoadingIndicator((ProgressBar) findViewById(R.id.left_fighter_progress_bar));
        mLeftFighter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mLeftInClick = true;
                ListUser listUser = (ListUser) adapterView.getItemAtPosition(position);
                mLeftFighter.setText(listUser.name);
                mLeftFighter.setTextColor(getResources().getColor(R.color.textColorSecondary));
                mLeftFighterId = listUser._id;
                mRightFighterAdapter.setExcludeUser(listUser);
                isLeftFromServer = true;
                mLeftInClick = false;
            }
        });
        mLeftFighter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mLeftInClick) {
                    mRightFighterAdapter.setExcludeUser(null);
                    mLeftFighterId = "";
                    isLeftFromServer = false;
                    mLeftFighter.setTextColor(!isDark ? getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mRightFighterAdapter = new FightersAutoCompleteAdapter(this, true);
        mRightFighter = (FightersAutoCompleteTextView) findViewById(R.id.right_fighter);
        if (isDark) {
            mRightFighter.setDropDownBackgroundResource(R.color.colorPrimaryVeryDark);
            mRightFighter.setTextColor(getResources().getColor(R.color.whiteCard));
        }
        mRightFighter.setThreshold(1);
        mRightFighter.setAdapter(mRightFighterAdapter);
        mRightFighter.setLoadingIndicator((ProgressBar) findViewById(R.id.right_fighter_progress_bar));
        mRightFighter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mRightInClick = true;
                ListUser listUser = (ListUser) adapterView.getItemAtPosition(position);
                mRightFighter.setText(listUser.name);
                mRightFighter.setTextColor(!isDark ? getResources().getColor(R.color.textColorSecondary) : getResources().getColor(R.color.whiteCard));
                mRightFighterId = listUser._id;
                mLeftFighterAdapter.setExcludeUser(listUser);
                isRightFromServer = true;
                mRightInClick = false;
            }
        });
        mRightFighter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mRightInClick) {
                    mLeftFighterAdapter.setExcludeUser(null);
                    mRightFighterId = "";
                    isRightFromServer = false;
                    mRightFighter.setTextColor(!isDark ? getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        CheckBox eventsCheck = (CheckBox) findViewById(R.id.events_check);
        eventsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsManager.setValue(Constants.IS_PHRASES_ENABLED, b);
            }
        });
        eventsCheck.setChecked(false);
        eventsCheck.setButtonDrawable(SettingsManager.getValue(Constants.IS_DARK_THEME, false) ? R.drawable.checkbox_selector_dark : R.drawable.checkbox_selector_light);


        Date date = new Date();

        TextView dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date));

        TextView timeTextView = (TextView) findViewById(R.id.time);
        timeTextView.setText(Helper.timeToString(date));

        mPlaceTextView = (TextView) findViewById(R.id.place);

        ownerName = SettingsManager.getValue(Constants.LAST_USER_NAME_FIELD, "");

        ImageButton startFightButton = (ImageButton) findViewById(R.id.start_fight);
        startFightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = mLeftFighter.getText().toString();
                if (left.startsWith(" ")) {
                    left = left.substring(1);
                } else if (left.endsWith(" ")) {
                    left = left.substring(0, left.length() - 1);
                }
                right = mRightFighter.getText().toString();
                if (right.startsWith(" ")) {
                    right = right.substring(1);
                } else if (right.endsWith(" ")) {
                    right = right.substring(0, right.length() - 1);
                }
                if (TextUtils.isEmpty(left) || TextUtils.isEmpty(right)) {
                    MessageDialog.show(NewFightActivity.this, 0, getString(R.string.error), getString(R.string.enter_users_name));
                    return;
                }
                FightData fightData = new FightData("", new Date(), new FighterData(mLeftFighterId, left), new FighterData(mRightFighterId, right), mAddress, ownerName);
                DataManager.instance().setCurrentFight(fightData);
                if (tcpHelper != null) {
                    try {
                        tcpHelper.send(CommandHelper.setName(CommandsContract.PERSON_TYPE_LEFT, isLeftFromServer ? left : "! " + left + " !"));
                        //TODO - weapon
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(NewFightActivity.this, FightActivity.class);
                    startActivity(intent);

                    finish();
                }
            }
        });

        changeSyncState(SYNC_STATE_NONE, null);
        checkPermissions();
        updatePlace();
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
    protected void onResume() {
        super.onResume();

        requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);
        mUpdateInProgress = false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.about_app:
                Intent intent2 = new Intent(this, AboutAppActivity.class);
                startActivity(intent2);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMessageDialogDismissed(int messageId) {
        String left = mLeftFighter.getText().toString();
        if (left.startsWith(" ")) {
            left = left.substring(1);
        } else if (left.endsWith(" ")) {
            left = left.substring(0, left.length() - 1);
        }
        String right = mRightFighter.getText().toString();
        if (right.startsWith(" ")) {
            right = right.substring(1);
        } else if (right.endsWith(" ")) {
            right = right.substring(0, right.length() - 1);
        }
        if (TextUtils.isEmpty(left) || TextUtils.isEmpty(right)) {
            MessageDialog.show(NewFightActivity.this, 0, getString(R.string.error), getString(R.string.enter_users_name));
            return;
        }

        String ownerName = SettingsManager.getValue(Constants.LAST_USER_NAME_FIELD, "");
        FightData fightData = new FightData("", new Date(), new FighterData(mLeftFighterId, left), new FighterData(mRightFighterId, right), mAddress, ownerName);
        DataManager.instance().setCurrentFight(fightData);

        Intent intent = new Intent(NewFightActivity.this, FightActivity.class);
        startActivity(intent);

        finish();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mAccessLocationPermitted = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_LOCATION);
        } else {
            mAccessLocationPermitted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_LOCATION) {
            mAccessLocationPermitted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        requestLocationUpdates();
    }

    private synchronized void requestLocationUpdates() {
        if (mAccessLocationPermitted && !mUpdateInProgress) {
            try {
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (mLocation == null) {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            } catch (SecurityException ignored) {

            }

            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 10, mLocationListener);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 5, 10, mLocationListener);
                mUpdateInProgress = true;
            } catch (SecurityException ignored) {

            }
            updatePlace();
            if (mLocation != null) {
                requestAddress();
            }
        }
    }

    private void updatePlace() {
        if (mAccessLocationPermitted) {
            if (!TextUtils.isEmpty(mAddress)) {
                mPlaceTextView.setText(mAddress);
            } else {
                mPlaceTextView.setText(getString(R.string.detect_location));
            }
        } else {
            mPlaceTextView.setText(getString(R.string.location_not_permitted));
        }
    }

    private void requestAddress() {
        if (mLocation != null) {
            DataManager.instance().getAddressByLocation(mLocation.getLatitude(), mLocation.getLongitude(), new DataManager.RequestListener<GetAddressByLocationResult>() {
                @Override
                public void onSuccess(GetAddressByLocationResult result) {
                    mAddress = result.address;
                    updatePlace();
                }

                @Override
                public void onFailed(String error, String message) {

                }

                @Override
                public void onStateChanged(boolean inProgress) {

                }
            });
        }
    }

    private class Pinger extends Thread {
        private boolean running;

        @Override
        public void run() {
            try {
                udp = new UDPHelper(getApplicationContext(), new UDPHelper.BroadcastListener() {
                    @Override
                    public void onReceive(String msg, final String ip) {
                        Log.d("RECEIVED", "receive message " + msg + " from " + ip);
                        if (!addresses.keySet().contains(ip) && Integer.parseInt(msg) < 10000) {
                            addresses.put(ip, msg);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayout linearLayout = new LinearLayout(NewFightActivity.this);
                                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(NewFightActivity.this, android.R.layout.select_dialog_singlechoice);
                                    for (String key : addresses.keySet()) {
                                        arrayAdapter.add(key);
                                    }
                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(NewFightActivity.this);
                                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            changeSyncState(SYNC_STATE_NONE, null);
                                        }
                                    });
                                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, final int i) {
                                            final String ip = arrayAdapter.getItem(i);
                                            Thread t = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tcpHelper = InspirationDayApplication.getApplication().startTCPHelper(ip);
                                                    tcpHelper.setListener(NewFightActivity.this);
                                                    tcpHelper.run();

                                                }
                                            });
                                            t.start();
                                            dialogInterface.dismiss();
                                            end();
                                            changeSyncState(SYNC_STATE_SYNCED, ip);
                                        }
                                    });
                                    builderSingle.show();
                                }
                            });
                        }
                    }
                });
                udp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            running = true;
            while (running) {
                try {
                    udp.send("PIN");
                    Log.d("sended", "ping sended ....");
                    sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void end() {
            running = false;
            udp.end();
        }
    }

    private void changeSyncState(int state, String ip) {
        switch (state) {
            case SYNC_STATE_NONE:
                syncedLay.setVisibility(View.GONE);
                btnSync.setVisibility(View.VISIBLE);
                syncProgress.setVisibility(View.GONE);
                break;
            case SYNC_STATE_SYNCED:
                syncedLay.setVisibility(View.VISIBLE);
                syncedName.setText(ip);
                try {
                    tcpHelper.send(CommandHelper.init(Integer.parseInt(addresses.get(ip))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                btnSync.setVisibility(View.GONE);
                syncProgress.setVisibility(View.GONE);
                break;
            case SYNC_STATE_SYNCING:
                syncedLay.setVisibility(View.GONE);
                btnSync.setVisibility(View.GONE);
                syncProgress.setVisibility(View.VISIBLE);
                break;
        }
    }
}
