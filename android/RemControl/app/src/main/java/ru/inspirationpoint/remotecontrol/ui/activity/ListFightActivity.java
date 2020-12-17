package ru.inspirationpoint.remotecontrol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.ui.DividerItemDecoration;
import ru.inspirationpoint.remotecontrol.ui.adapter.FightListAdapter;

public class ListFightActivity extends LocalAppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mFightRecyclerView;
    private RecyclerView.LayoutManager mFightLayoutManager;
    private FightListAdapter mFightListAdapter;
    private TextView mListIsEmptyTextView;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private String mUserName;
    private String mDate;
    private String mPlace;

    private boolean mFullAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_list);

        mUserName = getIntent().getStringExtra(CommonConstants.USER_ID_FIELD);
        if (mUserName == null) {
            mUserName = "";
        }
        mDate = getIntent().getStringExtra(CommonConstants.DATE_FIELD);
        mPlace = getIntent().getStringExtra(CommonConstants.PLACE_FIELD);

        configureToolbar();


        setCustomTitle(R.string.training_session);

        mListIsEmptyTextView = findViewById(R.id.empty_list_text);

        mSwipeRefreshWidget = findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
//        mSwipeRefreshWidget.setOnRefreshListener(this);
        mSwipeRefreshWidget.setEnabled(false);

        mFightRecyclerView = findViewById(R.id.fight_list);
        mFightRecyclerView.setHasFixedSize(true);
        mFightRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider, DividerItemDecoration.VERTICAL_LIST));
        mFightLayoutManager = new LinearLayoutManager(this);
        mFightRecyclerView.setLayoutManager(mFightLayoutManager);

        mFightListAdapter = new FightListAdapter(this);
        mFightRecyclerView.setAdapter(mFightListAdapter);

        mFightListAdapter.setOnItemClickListener(new FightListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, FightData fightData) {
//                DataManager.instance().setCurrentFight(fightData);
                Intent intent = new Intent(ListFightActivity.this, FightResultActivity.class);
                startActivity(intent);
            }
        });
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

        loadData();
    }

    private void loadData() {
//        DataManager.instance().getFightsInTraining(mUserName, mDate, mPlace, new DataManager.RequestListener<GetFightsInTrainingResult>() {
//            @Override
//            public void onSuccess(GetFightsInTrainingResult result) {
//                mFightListAdapter.setFightDataList(FightData.getFightData(result.fights));
//                //TODO handle cache
////                ArrayList<FightOutput> temp = (ArrayList<FightOutput>) JSONHelper.importFromJSON(FightListActivity.this, JSONHelper.ItemClass.FightOutput);
////                ArrayList<FightOutput> currentTemp = new ArrayList<>();
////                if (temp == null) {
////                    Log.d("NULL LIST", "+++++");
////                    temp = new ArrayList<>();
////                }
////                for (FightOutput output : result.fights) {
////                    if (!temp.contains(output)) {
////                        temp.add(output);
////                    }
////                }
////                for (FightOutput out : temp) {
////                    if (out.date.equals(mDate) && out.address.equals(mPlace)) {
////                        currentTemp.add(out);
////                    }
////                }
////                mFightListAdapter.setFightDataList(FightData.getFightData(currentTemp.toArray(new FightOutput[currentTemp.size()])));
////                JSONHelper.exportToJSON(FightListActivity.this, temp);
//                checkIsEmpty();
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                Log.d("GET FIGHTS", error + " + " + message);
//                loadFromCache();
//                checkIsEmpty();
//                String messageText = getString(R.string.read_trainings_error, message);
//                MessageDialog.show(ListFightActivity.this, 0, getString(R.string.error), messageText);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//                mSwipeRefreshWidget.setRefreshing(inProgress);
//            }
//        });
    }

    private void loadFromCache() {
//        ArrayList<FightOutput> cacheList = (ArrayList<FightOutput>) JSONHelper.importFromJSON(this, JSONHelper.ItemClass.FightOutput);
//        if (cacheList != null) {
//            ArrayList<FightOutput> selected = new ArrayList<>();
//            if (mUserName.equals("")) {
//                for (FightOutput out : cacheList) {
//                    if (out.date.equals(mDate) && out.address.equals(mPlace)) {
//                        selected.add(out);
//                    }
//                }
//                mFightListAdapter.setFromName(false);
//            } else {
//                selected = new ArrayList<>();
//                for (FightOutput output : cacheList) {
//                    if (output.leftFighterName.equals(mUserName) || output.rightFighterName.equals(mUserName)) {
//                        selected.add(output);
//                    }
//                }
//                mFightListAdapter.setFromName(true);
//            }
//            mFightListAdapter.setFightDataList(FightData.getFightData(selected.toArray(new FightOutput[selected.size()])));
//        } else mFightListAdapter.setFightDataList(null);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    private void checkIsEmpty() {
        if (mFightListAdapter.hasFightData()) {
            mListIsEmptyTextView.setVisibility(View.GONE);
            mFightRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mListIsEmptyTextView.setVisibility(View.VISIBLE);
            mFightRecyclerView.setVisibility(View.GONE);
        }
    }
}
