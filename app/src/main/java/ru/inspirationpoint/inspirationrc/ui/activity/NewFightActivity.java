package ru.inspirationpoint.inspirationrc.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.DataManager;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FightData;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FighterData;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.manager.helpers.JSONHelper;
import ru.inspirationpoint.inspirationrc.ui.adapter.FightersAutoCompleteAdapter;
import ru.inspirationpoint.inspirationrc.ui.dialog.MessageDialog;
import ru.inspirationpoint.inspirationrc.ui.view.FightersAutoCompleteTextView;
import server.schemas.requests.FightInput;
import server.schemas.responses.GetAddressByLocationResult;
import server.schemas.responses.ListUser;

public class NewFightActivity extends LocalAppCompatActivity implements MessageDialog.Listener{

    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;

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

    boolean mLeftInClick;
    boolean mRightInClick;

    private String mLeftFighterId = "";
    private String mRightFighterId = "";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_fight);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(true);

        configureToolbar();

        setCustomTitle(R.string.new_fight);

        final boolean isDark = SettingsManager.getValue(Constants.IS_DARK_THEME, false);

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
                mLeftInClick = false;
            }
        });
        mLeftFighter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mLeftInClick) {
                    mRightFighterAdapter.setExcludeUser(null);
                    mLeftFighterId = "";
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
                mRightInClick = false;
            }
        });
        mRightFighter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mRightInClick) {
                    mLeftFighterAdapter.setExcludeUser(null);
                    mRightFighterId = "";
                    mRightFighter.setTextColor(!isDark ? getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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
        dateTextView.setText(new SimpleDateFormat("dd.MM.yyyy").format(date));

        TextView timeTextView = (TextView) findViewById(R.id.time);
        timeTextView.setText(Helper.timeToString(date));

        mPlaceTextView = (TextView) findViewById(R.id.place);

        ImageButton startFightButton = (ImageButton) findViewById(R.id.start_fight);
        startFightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FightInput> fightInputs = (ArrayList<FightInput>) JSONHelper.importFromJSON(NewFightActivity.this, JSONHelper.ItemClass.FightInput);
                if (fightInputs != null) {
                    if (fightInputs.size() >= 100) {
                        MessageDialog.show(NewFightActivity.this, 0, "",
                                getResources().getString(R.string.limit_warning));
                    }
                }
                if (fightInputs != null) {
                    if (fightInputs.size() < 100) {
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
                } else {
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
            }
        });

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
        //TODO!!!!!!!!!!
//        Intent intent = new Intent(NewFightActivity.this, TrainingListActivity.class);
//        startActivity(intent);
//        finish();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
}
