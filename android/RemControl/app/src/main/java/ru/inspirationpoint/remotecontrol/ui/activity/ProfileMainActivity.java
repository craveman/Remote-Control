package ru.inspirationpoint.remotecontrol.ui.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.Profile;
import ru.inspirationpoint.remotecontrol.ui.adapter.ClubSpinnerAdapter;


public class ProfileMainActivity extends LocalAppCompatActivity
//        implements MessageDialog.Listener,
//        WeaponChooseDialog.Listener, ConfirmationDialog.Listener, FighterSearchDialog.Listener, DatePickerDialog.OnDateSetListener
{

    private static final int CLUBS_LOADING_ERROR_MESSAGE_ID = 1;
    private static final int COACHES_LOADING_ERROR_MESSAGE_ID = 2;
    private static final int FRIENDS_LOADING_ERROR_MESSAGE_ID = 3;
    private static final int PROFILE_LOADING_ERROR_MESSAGE_ID = 4;
    private static final int SAVE_ERROR_MESSAGE_ID = 5;

    private ClubSpinnerAdapter mClubSpinnerAdapter;
    private ProgressBar mProgress;
    private ScrollView mMainContent;
    private EditText mNameEdit;
    private TextView mEmailTextView;
    private TextView mNickTextView;
    private Spinner mClubSpinner;
    private EditText mWeaponEdit;

    private RecyclerView mFriendRecyclerView;
//    private UserListAdapter mFriendListAdapter;
    private TextView mFriendsIsEmty;

    private EditText mBirthdayEdit;

    private ArrayList<Pair<String, String>> mClubList = new ArrayList<>();
    private Profile mProfile = new Profile();

    private String removedUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        configureToolbar();

        setCustomTitle(R.string.profile);

        mProgress = findViewById(R.id.login_progress);
        mMainContent = findViewById(R.id.main_content);
        mNameEdit = findViewById(R.id.name_edit);
        mEmailTextView = findViewById(R.id.email);
        mNickTextView = findViewById(R.id.nick);
        mBirthdayEdit = findViewById(R.id.birthday_edit);
        mBirthdayEdit.setFocusable(false);
        mBirthdayEdit.setInputType(InputType.TYPE_NULL);
        mBirthdayEdit.setOnClickListener(view -> {
//                DatePickerDialog tpd = new DatePickerDialog(ProfileMainActivity.this,
//                        !SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ? AlertDialog.THEME_HOLO_LIGHT : AlertDialog.THEME_HOLO_DARK,
//                        ProfileMainActivity.this, 1990, 0, 1);
//                tpd.show();
        });

        mClubSpinner = findViewById(R.id.club_spinner);
        mWeaponEdit = findViewById(R.id.weapon_edit);
        mWeaponEdit.setFocusable(false);
        mFriendsIsEmty = findViewById(R.id.empty_fighters_list_text);
        mWeaponEdit.setInputType(InputType.TYPE_NULL);
//        mWeaponEdit.setOnClickListener(view -> WeaponChooseDialog.Companion.show(ProfileMainActivity.this));

//        initFriendList();
//
//        loadClubs();
//        updateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.apply, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_apply:
//                attemptToApply();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
//
//    @Override
//    public void onMessageDialogDismissed(int messageId) {
//        if (messageId == PROFILE_LOADING_ERROR_MESSAGE_ID || messageId == CLUBS_LOADING_ERROR_MESSAGE_ID ||
//                messageId == COACHES_LOADING_ERROR_MESSAGE_ID || messageId == FRIENDS_LOADING_ERROR_MESSAGE_ID) {
//            finish();
//        }
//    }
//
//    private void loadClubs() {
//        showProgress(true);
//        DataManager.instance().getClubs("", new DataManager.RequestListener<GetClubsResult>() {
//            @Override
//            public void onSuccess(GetClubsResult result) {
//                mClubList.clear();
//                mClubList.add(new Pair<>("", getString(R.string.not_selected_club)));
//                for (int i = 0; i < result.clubs.length; ++i) {
//                    mClubList.add(new Pair<>(result.clubs[i]._id, result.clubs[i].name));
//                }
//
//                mClubSpinnerAdapter = new ClubSpinnerAdapter(ProfileMainActivity.this, android.R.layout.simple_spinner_item, mClubList);
//                mClubSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                mClubSpinner.setAdapter(mClubSpinnerAdapter);
//                loadFriends();
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                String messageText = getString(R.string.read_clubs_error, message);
//                MessageDialog.show(ProfileMainActivity.this, CLUBS_LOADING_ERROR_MESSAGE_ID, getString(R.string.error), messageText);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//            }
//        });
//    }
//
//    private void loadFriends() {
//        DataManager.instance().getFriends(new DataManager.RequestListener<GetFriendsResult>() {
//            @Override
//            public void onSuccess(GetFriendsResult result) {
//                if (result.users != null) {
//                    ArrayList<ListUser> usersList = new ArrayList<>();
//                    Collections.addAll(usersList, result.users);
//                    mFriendListAdapter.setUserList(usersList);
//                    mFriendRecyclerView.requestLayout();
//                    checkEmpty();
//                }
//                loadProfile();
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                String messageText = getString(R.string.read_friends_error, message);
//                MessageDialog.show(ProfileMainActivity.this, FRIENDS_LOADING_ERROR_MESSAGE_ID, getString(R.string.error), messageText);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//            }
//        });
//    }
//
//    private void checkEmpty() {
//        mFriendRecyclerView.setVisibility(mFriendListAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
//        mFriendsIsEmty.setVisibility(mFriendListAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
//    }
//
//    private void loadProfile() {
//        DataManager.instance().getMyProfile(new DataManager.RequestListener<GetMyProfileResult>() {
//            @Override
//            public void onSuccess(GetMyProfileResult result) {
//                if (result.profile != null) {
//                    mProfile = result.profile;
//                }
//                updateData();
//                showProgress(false);
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                String messageText = getString(R.string.read_profile_error, message);
//                MessageDialog.show(ProfileMainActivity.this, PROFILE_LOADING_ERROR_MESSAGE_ID, getString(R.string.error), messageText);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//            }
//        });
//    }
//
//    private void initFriendList() {
//        mFriendRecyclerView = findViewById(R.id.friends);
//        mFriendRecyclerView.setHasFixedSize(true);
//        RecyclerView.LayoutManager mFriendLayoutManager = new LinearLayoutManager(this);
//        mFriendRecyclerView.setLayoutManager(mFriendLayoutManager);
//
//        mFriendListAdapter = new UserListAdapter(this);
//        mFriendRecyclerView.setAdapter(mFriendListAdapter);
//
//        mFriendListAdapter.setOnUserRemoveListener(new UserListAdapter.OnUserRemoveListener() {
//            @Override
//            public void onUserRemove(int position, ListUser user) {
//                removedUserId = user._id;
//
//                ConfirmationDialog.show(ProfileMainActivity.this, 1234, "", user.name);
//            }
//        });
//
//
//        TextView mAddFriend = findViewById(R.id.add_friend);
//        mAddFriend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FighterSearchDialog.show(ProfileMainActivity.this, 0, getResources().getString(R.string.add_subscriber),
//                        getResources().getString(R.string.enter_name));
//
//            }
//        });
//    }
//
//    private void showProgress(boolean showProgress) {
//        mProgress.setVisibility(showProgress ? View.VISIBLE : View.GONE);
//        mMainContent.setVisibility(showProgress ? View.GONE : View.VISIBLE);
//    }
//
//    private void updateData() {
//
//        mNameEdit.setText(TextUtils.isEmpty(mProfile.name) ? "" : mProfile.name);
//        mEmailTextView.setText(TextUtils.isEmpty(mProfile.email) ? "" : mProfile.email);
//        mNickTextView.setText(TextUtils.isEmpty(mProfile.nick) ? "" : mProfile.nick);
//        mBirthdayEdit.setText(TextUtils.isEmpty(mProfile.birthday) ? getResources().getString(R.string.enter_date) : Helper.dateToString(Helper.serverStringToDate(mProfile.birthday)));
//
//        if (mClubList.size() > 0) {
//            int foundPos = 0;
//            for (int i = 0; i < mClubList.size(); ++i) {
//                if (mClubList.get(i).first.equals(mProfile.clubId)) {
//                    foundPos = i;
//                    break;
//                }
//            }
//
//            mClubSpinner.setSelection(foundPos);
//        }
//
//        if (TextUtils.isEmpty(mProfile.weapon)) {
//            mWeaponEdit.setText("");
//        } else {
//            mWeaponEdit.setText(mProfile.weapon);
//        }
//    }
//
//    private void attemptToApply() {
//        mNameEdit.setError(null);
//
//        String name = mNameEdit.getText().toString();
//        String birthday = mProfile.birthday;
//        String clubId = "";
//        String nick = mProfile.nick;
//        int selected = mClubSpinner.getSelectedItemPosition();
//        if (selected > 0 && selected < mClubSpinnerAdapter.getCount()) {
//            clubId = mClubSpinnerAdapter.getItem(selected).first;
//        }
//        String weapon = mWeaponEdit.getText().toString();
//        String sexType = mProfile.sexType;
//
//        if (TextUtils.isEmpty(name)) {
//            mNameEdit.setError(getString(R.string.error_field_required));
//            mNameEdit.requestFocus();
//            return;
//        }
//
//        showProgress(true);
//        DataManager.instance().saveMyProfile(name, nick, "", birthday, "", clubId, weapon, sexType, new DataManager.RequestListener<SaveMyProfileResult>() {
//            @Override
//            public void onSuccess(SaveMyProfileResult result) {
//                SettingsManager.setValue(CommonConstants.LAST_USER_NAME_FIELD, result.profile.name);
//                SettingsManager.setValue(CommonConstants.LAST_USER_EMAIL_FIELD, result.profile.email);
//                finish();
////                saveFriendList();
////                saveCoachList();
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                showProgress(false);
//                String messageText = getString(R.string.save_profile_error, message);
//                MessageDialog.show(ProfileMainActivity.this, SAVE_ERROR_MESSAGE_ID, getString(R.string.error), messageText);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//
//            }
//        });
//    }
//
////    private void saveCoachList() {
////        ArrayList<ListUser> userList = mCoachListAdapter.getUserList();
////        String[] userIdArray = new String[userList.size()];
////        for (int i = 0; i < userList.size(); ++i) {
////            userIdArray[i] = userList.get(i)._id;
////        }
////        DataManager.instance().saveList(DataManager.COACHES_LIST_NAME, userIdArray, new DataManager.RequestListener<SaveListResult>() {
////            @Override
////            public void onSuccess(SaveListResult result) {
////                saveFriendList();
////            }
////
////            @Override
////            public void onFailed(String error, String message) {
////                showProgress(false);
////                String messageText = getString(R.string.save_coaches_error, message);
////                MessageDialog.show(ProfileActivity.this, SAVE_ERROR_MESSAGE_ID, getString(R.string.error), messageText);
////            }
////
////            @Override
////            public void onStateChanged(boolean inProgress) {
////
////            }
////        });
////    }
//
////    private void saveFriendList() {
////        ArrayList<ListUser> userList = mFriendListAdapter.getUserList();
////        String[] userIdArray = new String[userList.size()];
////        for (int i = 0; i < userList.size(); ++i) {
////            userIdArray[i] = userList.get(i)._id;
////        }
////        DataManager.instance().saveList(DataManager.FRIENDS_LIST_NAME, userIdArray, new DataManager.RequestListener<SaveListResult>() {
////            @Override
////            public void onSuccess(SaveListResult result) {
////                finish();
////            }
////
////            @Override
////            public void onFailed(String error, String message) {
////                showProgress(false);
////                String messageText = getString(R.string.save_friends_error, message);
////                MessageDialog.show(ProfileActivity.this, SAVE_ERROR_MESSAGE_ID, getString(R.string.error), messageText);
////            }
////
////            @Override
////            public void onStateChanged(boolean inProgress) {
////
////            }
////        });
////    }
//
//    @Override
//    public void onWeaponChoosed(String weaponType) {
//        mWeaponEdit.setText(weaponType);
//    }
//
//    @Override
//    public void onConfirmed(int messageId) {
//        showProgress(true);
//        if (messageId == 1234) {
//            DataManager.instance().removeUserFromFriends(removedUserId, new DataManager.RequestListener<RemoveUserFromFriendsResult>() {
//                @Override
//                public void onSuccess(RemoveUserFromFriendsResult result) {
//                    loadFriends();
//                    showProgress(false);
//                }
//
//                @Override
//                public void onFailed(String error, String message) {
//                    showProgress(false);
//                    String messageText = getString(R.string.save_friends_error, message);
//                    MessageDialog.show(ProfileMainActivity.this, SAVE_ERROR_MESSAGE_ID, getString(R.string.error), messageText);
//                }
//
//                @Override
//                public void onStateChanged(boolean inProgress) {
//
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onFighterSelected(int messageId, ListUser user) {
//        showProgress(true);
//        DataManager.instance().addUserToFriends(user._id, new DataManager.RequestListener<AddUserToFriendsResult>() {
//            @Override
//            public void onSuccess(AddUserToFriendsResult result) {
//                showProgress(false);
//                loadFriends();
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                showProgress(false);
//                String messageText = getString(R.string.add_friends_error, message);
//                MessageDialog.show(ProfileMainActivity.this, SAVE_ERROR_MESSAGE_ID, getString(R.string.error), messageText);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//
//            }
//        });
//    }

//    @Override
//    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//        Calendar c = Calendar.getInstance();
//        Log.d("DATE0", i + "|" + i1 + "|" + i2);
//        c.set(i, i1, i2, 0, 0);
//        mProfile.birthday = Helper.dateToServerString(c.getTime());
//        updateData();
//    }
}
