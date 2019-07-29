package ru.inspirationpoint.remotecontrol.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.handlers.CoreHandler;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.manager.helpers.LocaleHelper;
import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightData;
import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FighterData;
import ru.inspirationpoint.inspirationrc.rc.ui.adapter.FighterListAutoCompleteAdapter;
import ru.inspirationpoint.inspirationrc.rc.ui.adapter.FightersListAdapter;
import ru.inspirationpoint.inspirationrc.rc.ui.adapter.TrainingListAdapter;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.CalendarDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.ConfirmationDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.dialog.MessageDialog;
import ru.inspirationpoint.inspirationrc.rc.ui.view.FightersAutoCompleteTextView;
import server.schemas.responses.DialogOutput;
import server.schemas.responses.GetDialogsResult;
import server.schemas.responses.GetMyTrainingsResult;
import server.schemas.responses.GetUsersAddedMeResult;
import server.schemas.responses.ListUser;
import server.schemas.responses.LogoutResult;
import server.schemas.responses.Training;

//TODO resume to network error dialogs everywhere

public class InfoActivity extends LocalAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        CalendarDialog.DateListener, MessageDialog.Listener, ConfirmationDialog.Listener {

    private RecyclerView mTrainingRecyclerView;
    private RecyclerView mFightersRecyclerView;
    private TrainingListAdapter mTrainingListAdapter;
    private FightersListAdapter mFightersListAdapter;
    private TextView mListIsEmptyTextView;
    private TextView mFightersIsEmpty;
    private ProgressBar mFilterProgressBar;
    private ProgressBar mFriendsProgressBar;
    private ArrayList<Training> list = new ArrayList<>();
    private ArrayList<Training> tempList = new ArrayList<>();
    private DrawerLayout drawer;
    private FightersAutoCompleteTextView fightersFilterView;
    private ArrayList<DialogOutput> outputs;
    private NavigationView navigationView;
    private CoreHandler coreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad  = SettingsManager.getValue(CommonConstants.LANGUAGE_FIELD, "en"); // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_training_list);

        configureToolbar();
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        ImageView dateIcon = findViewById(R.id.iv_search_date);
        dateIcon.setImageDrawable(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getDrawable(R.drawable.ic_search_dark) : getResources().getDrawable(R.drawable.ic_search));

        ImageView fighterIcon = findViewById(R.id.iv_search_fighters);
        fighterIcon.setImageDrawable(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getDrawable(R.drawable.ic_search_dark) : getResources().getDrawable(R.drawable.ic_search));

        drawer = findViewById(R.id.drawer_layout_trainings);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        Button newFightBtn = findViewById(R.id.new_fight);
        newFightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(InfoActivity.this, NewFightActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                Intent intent = new Intent(InfoActivity.this, FightActivity.class);
                FightData fightData = new FightData("", new Date(), new FighterData("", "left"), new FighterData("", "right"),
                        "", SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
                intent.putExtra("SEMI", false);
                intent.putExtra("LEFT", "left");
                intent.putExtra("RIGHT", "right");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        loadDialogs();

        navigationView = findViewById(R.id.nav_view_training);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView mUserName = headerView.findViewById(R.id.user_name);
        mUserName.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        mUserName.setText(SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));

        TextView mUserEmail = headerView.findViewById(R.id.user_email);
        mUserEmail.setText(SettingsManager.getValue(CommonConstants.LAST_USER_EMAIL_FIELD, ""));
        mUserEmail.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));

        navigationView.getMenu().getItem(5).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_item_lang).setTitle(SettingsManager.getValue(CommonConstants.LANGUAGE_FIELD, "en").equals("ru") ?
                getResources().getString(R.string.en_language) : getResources().getString(R.string.ru_language));
        navigationView.getMenu().findItem(R.id.profile).setVisible(true);
        TextView logoutButton = navigationView.findViewById(R.id.logout);
        logoutButton.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        logoutButton.setOnClickListener(v -> {
//            DataManager.instance().logout(new DataManager.RequestListener<LogoutResult>() {
//                @Override
//                public void onSuccess(LogoutResult result) {
//                    SettingsManager.removeValue(CommonConstants.LAST_USER_ID_FIELD);
//                    SettingsManager.removeValue(CommonConstants.LAST_USER_NAME_FIELD);
//                    SettingsManager.removeValue(CommonConstants.LAST_USER_EMAIL_FIELD);
//                    Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    InfoActivity.this.startActivity(intent);
//                }
//
//                @Override
//                public void onFailed(String error, String message) {
//                    //TODO RE_CHECK THIS ISSUE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
////                    MessageDialog.show(InfoActivity.this, 0, getResources().getString(R.string.error), message);
//                    SettingsManager.removeValue(CommonConstants.LAST_USER_ID_FIELD);
//                    SettingsManager.removeValue(CommonConstants.LAST_USER_NAME_FIELD);
//                    SettingsManager.removeValue(CommonConstants.LAST_USER_EMAIL_FIELD);
//                    onMessageDialogDismissed(0);
//                    Log.wtf("LOGOUT ERROR", message  + "||" + error);
//                }
//
//                @Override
//                public void onStateChanged(boolean inProgress) {
//
//                }
//            },SettingsManager.getValue(CommonConstants.SESSION_ID_FIELD, ""));
        });

        setCustomTitle(R.string.my_diary);

        TextView filterView = findViewById(R.id.filter);
        filterView.setText(getResources().getString(R.string.prompt_filter));
        filterView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
        fightersFilterView = findViewById(R.id.filter_fighters);
        if (SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false)) {
            fightersFilterView.setDropDownBackgroundResource(R.color.colorPrimaryVeryDark);
            fightersFilterView.setTextColor(getResources().getColor(R.color.whiteCard));
        }
        fightersFilterView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fightersFilterView.setTextColor(!SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                        getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        filterView.setOnClickListener(view -> {
            if (list.size() != 0)
                CalendarDialog.show(InfoActivity.this, true);
        });
        filterView.clearFocus();
        filterView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (list.size() != 0)
                        CalendarDialog.show(InfoActivity.this, true);
                }
            }
        });
        FighterListAutoCompleteAdapter adapter = new FighterListAutoCompleteAdapter(this, false);
        fightersFilterView.setThreshold(1);
        fightersFilterView.setAdapter(adapter);
        fightersFilterView.setOnItemClickListener((adapterView, view, i, l) -> {
            ListUser stat = (ListUser) adapterView.getItemAtPosition(i);
            fightersFilterView.setText(stat.name);
            Intent intent = new Intent(InfoActivity.this, UserDispFightActivity.class);
            Bundle params = new Bundle();
            params.putString(CommonConstants.USER_ID_FIELD, stat._id);
            intent.putExtras(params);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        mListIsEmptyTextView = findViewById(R.id.empty_list_text);
        mFightersIsEmpty = findViewById(R.id.empty_fighters_list_text);

        mFilterProgressBar = findViewById(R.id.filter_progress_bar);
        mFriendsProgressBar = findViewById(R.id.friends_progress_bar);

        mTrainingRecyclerView = findViewById(R.id.training_list);
        mTrainingRecyclerView.setHasFixedSize(true);
//        mTrainingRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider, DividerItemDecoration.VERTICAL_LIST));
        RecyclerView.LayoutManager mTrainingLayoutManager = new LinearLayoutManager(this);
        mTrainingRecyclerView.setLayoutManager(mTrainingLayoutManager);

        mTrainingListAdapter = new TrainingListAdapter(this, 5);
        mTrainingRecyclerView.setAdapter(mTrainingListAdapter);

        mTrainingListAdapter.setOnItemClickListener((view, training) -> {
            Intent intent = new Intent(InfoActivity.this, ListFightActivity.class);
            Bundle params = new Bundle();
            params.putString(CommonConstants.DATE_FIELD, training.date);
            params.putString(CommonConstants.PLACE_FIELD, training.address);
            intent.putExtras(params);
            startActivity(intent);
        });

        mFightersRecyclerView = findViewById(R.id.fighters_list);
        mFightersRecyclerView.setHasFixedSize(true);
//        mFightersRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider, DividerItemDecoration.VERTICAL_LIST));
        mFightersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFightersListAdapter = new FightersListAdapter();
        mFightersRecyclerView.setAdapter(mFightersListAdapter);
        loadFriend();

        checkIsEmpty();

        mFightersListAdapter.setOnItemClickListener((view, stat) -> {
            Intent intent = new Intent(InfoActivity.this, UserDispFightActivity.class);
            Bundle params = new Bundle();
            params.putString(CommonConstants.USER_ID_FIELD, stat._id);
            intent.putExtras(params);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        coreHandler = InspirationDayApplication.getApplication().getCoreHandler();
        coreHandler.setActivity(this);
    }



    private void loadDialogs() {
//        DataManager.instance().getDialogs(new DataManager.RequestListener<GetDialogsResult>() {
//            @Override
//            public void onSuccess(GetDialogsResult result) {
//                outputs = new ArrayList<>(Arrays.asList(result.dialogs));
//
//                int newCount = 0;
//                if (outputs.size() != 0) {
//                    for (DialogOutput output : outputs) {
//                        if (output.unreadCount != 0) {
//                            newCount++;
//                        }
//                    }
//                }
//                if (newCount != 0) {
//                    navigationView.getMenu().findItem(R.id.messages).setTitle(getResources().getString(R.string.nav_messages) + " (" + newCount + ")");
//                }
//                checkIsEmpty();
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
////                MessageDialog.show(InfoActivity.this, 0, error, message);
//                checkIsEmpty();
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        fightersFilterView.setText("");
        navigationView.getMenu().getItem(5).setVisible(false);
//        list = (ArrayList<Training>) JSONHelper.importFromJSON(TrainingListActivity.this, JSONHelper.ItemClass.Training);
        loadData();
        loadFriend();

        if (SettingsManager.getValue(CommonConstants.LOCALE_CHANGED_FIELD, false)) {
            SettingsManager.setValue(CommonConstants.LOCALE_CHANGED_FIELD, false);
            recreate();
        }
        if (SettingsManager.getValue(CommonConstants.IS_THEME_CHANGED, false)) {
            SettingsManager.setValue(CommonConstants.IS_THEME_CHANGED, false);
            recreate();
        }
        coreHandler.startWiFiNetworking();
    }

    private void loadData() {
//        DataManager.instance().getMyTrainings("", new DataManager.RequestListener<GetMyTrainingsResult>() {
//
//            @Override
//            public void onSuccess(GetMyTrainingsResult result) {
//                if (result.trainings != null) {
//                    if (result.trainings.length != 0) {
//                        list.clear();
//                        list.addAll(Arrays.asList(result.trainings));
//                        mTrainingListAdapter.setTrainingArray(result.trainings);
////                        if (result.trainings.length > 5) {
////                            DisplayMetrics metrics = new DisplayMetrics();
////                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
////
////                            int height = metrics.heightPixels;
////                            ViewGroup.LayoutParams params = mTrainingRecyclerView.getLayoutParams();
////                            params.height = height*9/36-15;
////                            mTrainingRecyclerView.setLayoutParams(params);
////                        }
////                    JSONHelper.exportToJSON(TrainingListActivity.this, list);
//                    }
//                }
//                checkIsEmpty();
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                mTrainingListAdapter.setTrainingArray(null);
//                checkIsEmpty();
////                String messageText = getString(R.string.read_trainings_error, message);
////                MessageDialog.show(InfoActivity.this, 0, getString(R.string.error), messageText);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//                if (inProgress) {
//                    mFilterProgressBar.setVisibility(View.VISIBLE);
//                } else {
//                    mFilterProgressBar.setVisibility(View.GONE);
//                }
//            }
//        });
    }
//
//    @Override
//    public void onRefresh() {
//        loadData();
//    }

    private void checkIsEmpty() {
        if (mTrainingListAdapter.hasTrainings()) {
            mListIsEmptyTextView.setVisibility(View.GONE);
            mTrainingRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mListIsEmptyTextView.setVisibility(View.VISIBLE);
            mTrainingRecyclerView.setVisibility(View.GONE);
        }

        if (mFightersListAdapter.hasFighters()) {
            mFightersIsEmpty.setVisibility(View.GONE);
            mFightersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mFightersIsEmpty.setVisibility(View.VISIBLE);
            mFightersRecyclerView.setVisibility(View.GONE);
        }
    }

    public void loadFriend() {
//        DataManager.instance().getUsersAddedMe(new DataManager.RequestListener<GetUsersAddedMeResult>() {
//            @Override
//            public void onSuccess(GetUsersAddedMeResult result) {
//                Log.d("LOAD FRIENDS SUCCESS", "+++++");
//                mFightersListAdapter.setData(new ArrayList<>(Arrays.asList(result.users)));
////                if (result.users.length > 5) {
////                    DisplayMetrics metrics = new DisplayMetrics();
////                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
////
////                    int height = metrics.heightPixels;
////                    ViewGroup.LayoutParams params = mTrainingRecyclerView.getLayoutParams();
////                    params.height = height*9/36-20;
////                    mFightersRecyclerView.setLayoutParams(params);
////                }
//                checkIsEmpty();
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                mFightersListAdapter.setData(null);
//                Log.d("LOAD FRIENDS FAIL", message);
//                checkIsEmpty();
////                String messageText = getString(R.string.read_friends_error, message);
////                MessageDialog.show(InfoActivity.this, 0, getString(R.string.error), messageText);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//                if (inProgress) {
//                    mFriendsProgressBar.setVisibility(View.VISIBLE);
//                } else {
//                    mFriendsProgressBar.setVisibility(View.GONE);
//                }
//            }
//        });
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
//            case R.id.mode_rc:
//                Intent intent4 = new Intent(this, NewFightActivity.class);
//                startActivity(intent4);
//                finish();
//                break;
//            case R.id.mode_camera:
//                Intent intent5 = new Intent(this, CameraStartActivity.class);
//                startActivity(intent5);
//                finish();
//                break;
//            case R.id.reserve_ref:
//                Intent intent6 = new Intent(this, RefereeStartActivity.class);
//                startActivity(intent6);
//                finish();
//                break;
//            case R.id.messages:
//                Intent intent3 = new Intent(this, DialogsActivity.class);
//                startActivity(intent3);
//                finish();
//                break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.about_app:
                Intent intent2 = new Intent(this, AboutAppActivity.class);
                startActivity(intent2);
                break;
            case R.id.profile:
                Intent intent7 = new Intent(this, ProfileMainActivity.class);
                startActivity(intent7);
                break;
//            case R.id.group:
//                Intent intent1 = new Intent(this, GroupStartActivity.class);
//                startActivity(intent1);
//                break;
            case R.id.nav_item_lang:
                if (SettingsManager.getValue(CommonConstants.LANGUAGE_FIELD, "en").equals("ru")) {
                    LocaleHelper.setLocale(this, "en");
                    navigationView.getMenu().getItem(9).setTitle(getResources().getString(R.string.ru_language));
                } else {
                    LocaleHelper.setLocale(this, "ru");
                    navigationView.getMenu().getItem(9).setTitle(getResources().getString(R.string.en_language));
                }
                recreate();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang_code = SettingsManager.getValue(CommonConstants.LANGUAGE_FIELD, "en");
        Context context = LocaleHelper.changeLang(newBase, lang_code);
        super.attachBaseContext(context);
    }

    @Override
    public void onDatesSet(List<CalendarDay> days) {
        if (days != null) {
            if (days.size() != 0) {
                tempList = new ArrayList<>();
                for (Training training : list) {
                    for (CalendarDay calendarDay : days) {
                        if (calendarDay.getDay() == Helper.serverStringToDate(training.date).getDate() &&
                                calendarDay.getMonth() == Helper.serverStringToDate(training.date).getMonth() &&
                                calendarDay.getYear() == Helper.serverStringToDate(training.date).getYear() + 1900) {
                            tempList.add(training);
                        }
                    }
//                    Intent intent = new Intent(TrainingListActivity.this, UserFightListActivity.class);
//                    Bundle params = new Bundle();
//                    params.putString(CommonConstants.USER_ID_FIELD, SettingsManager.getValue(CommonConstants.LAST_USER_ID_FIELD, ""));
//                    ArrayList<Date> list = new ArrayList<>();
//                    for (CalendarDay day : days) {
//                        list.add(day.getDate());
//                    }
//                    params.putSerializable("list", list);
//                    intent.putExtras(params);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                    mTrainingListAdapter.setTrainingArray(tempList.toArray(new Training[tempList.size()]));
                    StringBuilder sb = new StringBuilder();
                    for (CalendarDay calendarDay : days) {
                        sb.append(calendarDay.getDay()).append(".").append(calendarDay.getMonth()+1).append(", ");
                    }
                    sb.deleteCharAt(sb.length()-1);
                    sb.deleteCharAt(sb.length()-1);
                }
            }
        }
    }

    @Override
    public void onMessageDialogDismissed(int messageId) {
        if (messageId == 7585) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } else {
            Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            InfoActivity.this.startActivity(intent);
            finish();
        }
    }

    @Override
    public void onConfirmed(int messageId) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }
}
