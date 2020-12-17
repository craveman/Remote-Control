package ru.inspirationpoint.remotecontrol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.manager.helpers.Helper;
import ru.inspirationpoint.remotecontrol.ui.DividerItemDecoration;
import ru.inspirationpoint.remotecontrol.ui.adapter.FightActionsAdapter;


public class FightResultActivity extends LocalAppCompatActivity implements FightActionsAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private TextView mPlaceTextView;
    private TextView mStartTimeTextView;
    private TextView mFullTimeTextView;
    private TextView mPureTimeTextView;
    private TextView mLeftFighterTextView;
    private TextView mRightFighterTextView;
    private TextView mLeftScore;
    private TextView mRightScore;
    private LinearLayout mPairLay;
    private RecyclerView mFightActions;
    private FightActionsAdapter adapter;
    private View progressLay;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isFromFight;

    private FightData mFightData;
    private Handler handler;
    private final Runnable swipeRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
            handler.postDelayed(this, 1500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_result);

        configureToolbar();

        handler = new Handler();

        swipeRefreshLayout = findViewById(R.id.swipe_lay);
        swipeRefreshLayout.setOnRefreshListener(this);

        isFromFight = getIntent().getBooleanExtra("fight", false);

        mPlaceTextView = findViewById(R.id.place);

        mLeftFighterTextView = findViewById(R.id.left_fighter);
        mRightFighterTextView = findViewById(R.id.right_fighter);

        progressLay = findViewById(R.id.pb_lay);

        mLeftScore = findViewById(R.id.left_score);
        mRightScore = findViewById(R.id.right_score);

        mFightActions = findViewById(R.id.fight_result_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mFightActions.setLayoutManager(llm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mFightActions.getContext(),
                R.drawable.divider, DividerItemDecoration.VERTICAL_LIST);
        mFightActions.addItemDecoration(dividerItemDecoration);
        mFightActions.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        // TODO Auto-generated method stub
                        //super.onScrollStateChanged(recyclerView, newState);
                        try {
                            int firstPos = llm.findFirstCompletelyVisibleItemPosition();
                            if (firstPos > 0) {
                                swipeRefreshLayout.setEnabled(false);
                            } else {
                                swipeRefreshLayout.setEnabled(true);
                                if (mFightActions.getScrollState() == 1)
                                    if (swipeRefreshLayout.isRefreshing())
                                        mFightActions.stopScroll();
                            }

                        } catch (Exception e) {
                            Log.e("FRA", "Scroll Error : " + e.getLocalizedMessage());
                        }
                    }
                });
        adapter = new FightActionsAdapter(this, false);
        adapter.setOnItemClickListener(this);
        mFightActions.setAdapter(adapter);

        mStartTimeTextView = findViewById(R.id.start_time);
        mPureTimeTextView = findViewById(R.id.pure_time);
        mFullTimeTextView = findViewById(R.id.full_time);
        mPairLay = findViewById(R.id.pair_lay);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(swipeRefreshRunnable, 1500);
    }

    @Override
    protected void onStop() {
        handler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mFightData = DataManager.instance().getCurrentFight();
        if (mFightData == null || mFightData.getActionsNumber() == 0) {
            finish();
            return;
        }
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();
        update();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("fight", mFightData);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        if (mFightData == DataManager.instance().getCurrentFight()) {
//            DataManager.instance().setCurrentFight((FightData) savedInstanceState.getSerializable("fight"));
//        }
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
    public void onBackPressed() {
        if (isFromFight) {
            Intent intent = new Intent(FightResultActivity.this, InfoActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void update() {
        setCustomTitle(getResources().getString(R.string.my_diary) + " - "
                + new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(mFightData.getDate()));

        mPairLay.setVisibility(View.GONE);
        adapter.setData(mFightData.getActionsList());

        mLeftScore.setText(String.valueOf(mFightData.getLeftFighter().getScore()));
        mRightScore.setText(String.valueOf(mFightData.getRightFighter().getScore()));

        mStartTimeTextView.setText(Helper.timeToString(mFightData.getTime()));
        mFullTimeTextView.setText(Helper.timeToString(new Date(mFightData.getmEndTime() - mFightData.getmStartTime())));
        mPureTimeTextView.setText(Helper.timeToString(new Date(mFightData.getPureTime())));

        Date currentTime = new Date();
        currentTime.setTime(mFightData.getDuration());

        String address = mFightData.getPlace();
        if (TextUtils.isEmpty(address)) {
            address = getString(R.string.not_detected);
        }
        mPlaceTextView.setText(address);

        mLeftFighterTextView.setText(mFightData.getLeftFighter().getName());
        mRightFighterTextView.setText(mFightData.getRightFighter().getName());

        if (mFightData.getActionsNumber() == 0) {
            return;
        }

    }

    @Override
    public void onRefresh() {
//        DataManager.instance().getFightById(mFightData.getId(), new DataManager.RequestListener<GetFightResult>() {
//            @Override
//            public void onSuccess(GetFightResult result) {
//                mFightData = new FightData(result.fight);
//                update();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//
//            }
//        });
    }

    @Override
    public void onItemClick(View view, FightActionData fightData) {
        //        Intent intent = new Intent(this, VideoActivity.class);
//        intent.putExtra("url", CommonConstants.CLOUD_STORAGE + fightData.getVideoUrl());
//        startActivityForResult(intent, 543);
    }
}
