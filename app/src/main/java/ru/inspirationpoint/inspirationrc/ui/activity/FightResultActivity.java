package ru.inspirationpoint.inspirationrc.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.DataManager;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FightData;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FighterData;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.manager.helpers.JSONHelper;
import ru.inspirationpoint.inspirationrc.ui.adapter.FightActionsAdapter;
import server.schemas.requests.GroupResult;

public class FightResultActivity extends LocalAppCompatActivity {

    private TextView mPlaceTextView;
//    private TextView mRefereeTextView;
    private TextView mStartTimeTextView;
    private TextView mFullTimeTextView;
    private TextView mPureTimeTextView;
//    private TextView mRefereeTitleTextView;
    private TextView mLeftFighterTextView;
    private TextView mRightFighterTextView;
    private TextView mLeftScore;
    private TextView mRightScore;
    private TextView mNextPair;
    private LinearLayout mPairLay;
    private RecyclerView mFightActions;
    private FightActionsAdapter adapter;
    private View progressLay;

    private boolean isFromFight;

    private FightData mFightData;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_result);

        configureToolbar();

        isFromFight = getIntent().getBooleanExtra("fight", false);

        mPlaceTextView = (TextView) findViewById(R.id.place);

        mLeftFighterTextView = (TextView) findViewById(R.id.left_fighter);
        mRightFighterTextView = (TextView) findViewById(R.id.right_fighter);

        progressLay = findViewById(R.id.pb_lay);

        mLeftScore = (TextView) findViewById(R.id.left_score);
        mRightScore = (TextView) findViewById(R.id.right_score);

        mFightActions = (RecyclerView) findViewById(R.id.fight_result_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mFightActions.setLayoutManager(llm);
        adapter = new FightActionsAdapter(this, false);
        mFightActions.setAdapter(adapter);

        mStartTimeTextView = (TextView) findViewById(R.id.start_time);
        mPureTimeTextView = (TextView) findViewById(R.id.pure_time);
        mFullTimeTextView = (TextView) findViewById(R.id.full_time);
        mNextPair = (TextView) findViewById(R.id.pair);
        mPairLay = (LinearLayout) findViewById(R.id.pair_lay);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFightData = DataManager.instance().getCurrentFight();
        if (mFightData == null || mFightData.getActionsNumber() == 0) {
            finish();
            return;
        }

        update();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.pdf_menu, menu);
//        return true;
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("fight", mFightData);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mFightData == DataManager.instance().getCurrentFight()) {
            DataManager.instance().setCurrentFight((FightData) savedInstanceState.getSerializable("fight"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
//            case R.id.action_pdf:
//                try {
//                    int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//                    if (permission != PackageManager.PERMISSION_GRANTED) {
//                        // We don't have permission so prompt the user
//                        ActivityCompat.requestPermissions(
//                                this,
//                                PERMISSIONS_STORAGE,
//                                REQUEST_EXTERNAL_STORAGE
//                        );
//                    } else {
//                        progressLay.setVisibility(View.VISIBLE);
//                        PDFCreator creator = new PDFCreator(mFightData.getLeftFighter().getName() + ": " +
//                                mFightData.getLeftFighter().getScore() + " - " +
//                                mFightData.getRightFighter().getScore() + " :" +
//                                mFightData.getRightFighter().getName() + "  " +
//                                new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault()).format(mFightData.getmStartTime()), this,
//                                mFightData.getActionsList().get(0).getComment());
//                        Uri uri = creator.createLogPDF(mFightData);
//                        Intent intentOpenFile = new Intent(Intent.ACTION_VIEW);
//                        intentOpenFile.setDataAndType(uri, "application/pdf");
//                        intentOpenFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intentOpenFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        startActivity(intentOpenFile);
//                    }
//                } finally {
//                    progressLay.setVisibility(View.GONE);
//                }
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if ((grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//            progressLay.setVisibility(View.VISIBLE);
//            PDFCreator creator = new PDFCreator(mFightData.getLeftFighter().getName() + ": " +
//                    mFightData.getLeftFighter().getScore() + " - " +
//                    mFightData.getRightFighter().getScore() + " :" +
//                    mFightData.getRightFighter().getName() + "  " +
//                    new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault()).format(mFightData.getmStartTime()), this,
//                    mFightData.getActionsList().get(0).getComment());
//            Uri uri = creator.createLogPDF(mFightData);
//            Intent intentOpenFile = new Intent(Intent.ACTION_VIEW);
//            intentOpenFile.setDataAndType(uri, "application/pdf");
//            intentOpenFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intentOpenFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(intentOpenFile);
//        }
//    }

    @Override
    public void onBackPressed() {
        if (isFromFight) {
            int currentPairNum = SettingsManager.getValue(Constants.CURRENT_PAIR, 21);
            if (currentPairNum == 21) {
                //TODO !!!!!!
//                Intent intent = new Intent(FightResultActivity.this, TrainingListActivity.class);
//                startActivity(intent);
            } else if (currentPairNum == 20) {
//                ArrayList<GroupResult> results = (ArrayList<GroupResult>) JSONHelper.importFromJSON(this, JSONHelper.ItemClass.GroupResult);
//                if (results == null) {
//                    results = new ArrayList<>();
//                }
//                results.add(new GroupResult(SettingsManager.getLongValue(this, Constants.GROUP_ID, 0),
//                        Constants.PAIRS[SettingsManager.getValue(Constants.CURRENT_PAIR, 0)].first,
//                        Constants.PAIRS[SettingsManager.getValue(Constants.CURRENT_PAIR, 0)].second,
//                        mFightData.getLeftFighter().getScore(), mFightData.getRightFighter().getScore()));
//                Log.d("RESULTS", results.get(results.size() - 1).numberFirst + " " + results.get(results.size() - 1).numberSecond + " "
//                        + results.get(results.size() - 1).scoreFirst + " " + results.get(results.size() - 1).scoreSecond);
//                JSONHelper.exportToJSON(this, results);
//                SettingsManager.setValue(Constants.CURRENT_PAIR, SettingsManager.getValue(Constants.CURRENT_PAIR, 0) + 1);
//                Intent intent = new Intent(FightResultActivity.this, GroupResultActivity.class);
//                startActivity(intent);
            } else {
                SettingsManager.setValue(Constants.CURRENT_PAIR, SettingsManager.getValue(Constants.CURRENT_PAIR, 0) + 1);
                Pair<Integer, Integer> currentPair = Constants.PAIRS[currentPairNum + 1];
                FighterData left = null;
                switch (currentPair.first) {
                    case 1:
                        left = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_ONE, ""));
                        break;
                    case 2:
                        left = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_TWO, ""));
                        break;
                    case 3:
                        left = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_THREE, ""));
                        break;
                    case 4:
                        left = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_FOUR, ""));
                        break;
                    case 5:
                        left = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_FIVE, ""));
                        break;
                    case 6:
                        left = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_SIX, ""));
                        break;
                    case 7:
                        left = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_SEVEN, ""));
                        break;
                }
                FighterData right = null;
                switch (currentPair.second) {
                    case 1:
                        right = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_ONE, ""));
                        break;
                    case 2:
                        right = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_TWO, ""));
                        break;
                    case 3:
                        right = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_THREE, ""));
                        break;
                    case 4:
                        right = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_FOUR, ""));
                        break;
                    case 5:
                        right = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_FIVE, ""));
                        break;
                    case 6:
                        right = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_SIX, ""));
                        break;
                    case 7:
                        right = new FighterData("", SettingsManager.getValue(Constants.FIGHTER_SEVEN, ""));
                        break;
                }

                ArrayList<GroupResult> results = (ArrayList<GroupResult>) JSONHelper.importFromJSON(this, JSONHelper.ItemClass.GroupResult);
                if (results == null) {
                    results = new ArrayList<>();
                }
                results.add(new GroupResult(SettingsManager.getLongValue(this, Constants.GROUP_ID, 0),
                        Constants.PAIRS[SettingsManager.getValue(Constants.CURRENT_PAIR, 0) - 1].first,
                        Constants.PAIRS[SettingsManager.getValue(Constants.CURRENT_PAIR, 0) - 1].second,
                        mFightData.getLeftFighter().getScore(), mFightData.getRightFighter().getScore()));
                Log.d("RESULTS", results.get(results.size() - 1).numberFirst + " " + results.get(results.size() - 1).numberSecond + " "
                        + results.get(results.size() - 1).scoreFirst + " " + results.get(results.size() - 1).scoreSecond);
                JSONHelper.exportToJSON(this, results);

                FightData fightData = new FightData("", new Date(), left, right, "", "");
                DataManager.instance().setCurrentFight(fightData);

                Intent intent = new Intent(FightResultActivity.this, FightActivity.class);
                startActivity(intent);

                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void update() {
        setCustomTitle(getResources().getString(R.string.my_diary) + " - "
                + new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(mFightData.getDate()));

        if (SettingsManager.getValue(Constants.CURRENT_PAIR, 21) != 21) {
            mPairLay.setVisibility(View.VISIBLE);
            if (SettingsManager.getValue(Constants.CURRENT_PAIR, 21) != 20) {
                mNextPair.setText(String.format("#%s - #%s",
                        String.valueOf(Constants.PAIRS[SettingsManager.getValue(Constants.CURRENT_PAIR, 0) + 1].first),
                        String.valueOf(Constants.PAIRS[SettingsManager.getValue(Constants.CURRENT_PAIR, 0) + 1].second)));
            } else mNextPair.setText(getResources().getString(R.string.no));
        } else mPairLay.setVisibility(View.GONE);
        adapter.setData(mFightData.getActionsList());

        mLeftScore.setText(String.valueOf(mFightData.getLeftFighter().getScore()));
        mRightScore.setText(String.valueOf(mFightData.getRightFighter().getScore()));

        mStartTimeTextView.setText(Helper.timeToString(mFightData.getTime()));
//        Date fullTime = new Date();
//        fullTime.setTime(mFightData.getEndTime().getTime() - mFightData.getTime().getTime());
//        mFullTimeTextView.setText(new SimpleDateFormat("mm:ss", Locale.getDefault()).format(fullTime));
        mFullTimeTextView.setText(Helper.timeToString(new Date(mFightData.getmEndTime() - mFightData.getmStartTime())));
        mPureTimeTextView.setText(Helper.timeToString(new Date(mFightData.getPureTime())));

        Date currentTime = new Date();
        currentTime.setTime(mFightData.getDuration());

        String address = mFightData.getPlace();
        if (TextUtils.isEmpty(address)) {
            address = getString(R.string.not_detected);
        }
        mPlaceTextView.setText(address);

//        String owner = mFightData.getOwner();
//        if (TextUtils.isEmpty(owner)) {
//            mRefereeTextView.setVisibility(View.GONE);
//            mRefereeTitleTextView.setVisibility(View.GONE);
//        } else {
//            mRefereeTextView.setVisibility(View.VISIBLE);
//            mRefereeTitleTextView.setVisibility(View.VISIBLE);
//            mRefereeTextView.setText(owner);
//        }

        mLeftFighterTextView.setText(mFightData.getLeftFighter().getName());
        mRightFighterTextView.setText(mFightData.getRightFighter().getName());

        if (mFightData.getActionsNumber() == 0) {
            return;
        }

    }
}
