package ru.inspirationpoint.remotecontrol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.Training;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.helpers.Helper;
import ru.inspirationpoint.remotecontrol.ui.adapter.TrainingListAdapter;
import ru.inspirationpoint.remotecontrol.ui.dialog.CalendarDialog;

public class UserDispFightActivity extends LocalAppCompatActivity implements CalendarDialog.DateListener {

    private RecyclerView mTrainingRecyclerView;
    private TrainingListAdapter mTrainingListAdapter;
    private RecyclerView.LayoutManager mTrainingLayoutManager;
    private TextView mListIsEmptyTextView;
    private ProgressBar mFilterProgressBar;
    private ArrayList<Training> list = new ArrayList<>();
    private ArrayList<Training> tempList = new ArrayList<>();
    private EditText filterView;
    private String userId;
    private ArrayList<Date> dates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fight_list);

        configureToolbar();

        userId = getIntent().getStringExtra(CommonConstants.USER_ID_FIELD);

        ImageView dateIcon = findViewById(R.id.iv_search_date);
        dateIcon.setImageDrawable(SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                getResources().getDrawable(R.drawable.ic_search_dark) : getResources().getDrawable(R.drawable.ic_search));
        setCustomTitle(R.string.my_diary);

        filterView = findViewById(R.id.filter);
        filterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarDialog.show(UserDispFightActivity.this, true);
            }
        });
        filterView.setInputType(InputType.TYPE_NULL);

        mListIsEmptyTextView = findViewById(R.id.empty_list_text);
        mFilterProgressBar = findViewById(R.id.filter_progress_bar);

        mTrainingRecyclerView = findViewById(R.id.training_list);
        mTrainingRecyclerView.setHasFixedSize(true);
//        mTrainingRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider, DividerItemDecoration.VERTICAL_LIST));
        mTrainingLayoutManager = new LinearLayoutManager(this);
        mTrainingRecyclerView.setLayoutManager(mTrainingLayoutManager);

        mTrainingListAdapter = new TrainingListAdapter(this, 10);
        mTrainingRecyclerView.setAdapter(mTrainingListAdapter);

        mTrainingListAdapter.setOnItemClickListener(new TrainingListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, Training training) {
                Intent intent = new Intent(UserDispFightActivity.this, ListFightActivity.class);
                Bundle params = new Bundle();
                params.putString(CommonConstants.USER_ID_FIELD, userId);
                params.putString(CommonConstants.DATE_FIELD, training.date);
                params.putString(CommonConstants.PLACE_FIELD, training.address);
                intent.putExtras(params);
                startActivity(intent);
            }
        });
        loadData();


        dates = (ArrayList<Date>)getIntent().getSerializableExtra("list");

        checkIsEmpty();
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
        Intent intent = new Intent(UserDispFightActivity.this, InfoActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadData() {
//        DataManager.instance().getTrainings(userId, "", "", new DataManager.RequestListener<GetTrainingsResult>() {
//
//            @Override
//            public void onSuccess(GetTrainingsResult result) {
//                if (result.trainings.length != 0) {
//                    mTrainingListAdapter.setTrainingArray(result.trainings);
//                }
//                if (result.trainings.length > 11) {
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                    int height = metrics.heightPixels;
//                    ViewGroup.LayoutParams params = mTrainingRecyclerView.getLayoutParams();
//                    params.height = height*7/10;
//                    mTrainingRecyclerView.setLayoutParams(params);
//                }
//                Collections.addAll(list, result.trainings);
//                Log.d("SUCCESS", result.trainings.length + "");
//                checkIsEmpty();
//                if (dates != null && dates.size() != 0) {
//                    List<CalendarDay> days = new ArrayList<>();
//                    for (Date date : dates) {
//                        days.add(CalendarDay.from(date));
//                    }
//                    onDatesSet(days);
//                }
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                checkIsEmpty();
//                Log.d("FAILED", message);
//                String messageText = getString(R.string.read_trainings_error, message);
//                MessageDialog.show(UserDispFightActivity.this, 0, getString(R.string.error), messageText);
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

    private void checkIsEmpty() {
        if (mTrainingListAdapter.hasTrainings()) {
            mListIsEmptyTextView.setVisibility(View.GONE);
            mTrainingRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mListIsEmptyTextView.setVisibility(View.VISIBLE);
            mTrainingRecyclerView.setVisibility(View.GONE);
        }
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
                    mTrainingListAdapter.setTrainingArray(tempList.toArray(new Training[tempList.size()]));
                    if (tempList.size() > 11) {
                        ViewGroup.LayoutParams params = mTrainingRecyclerView.getLayoutParams();
                        params.height = 600;
                        mTrainingRecyclerView.setLayoutParams(params);
                    }
                    StringBuilder sb = new StringBuilder();
                    for (CalendarDay calendarDay : days) {
                        sb.append(calendarDay.getDay()).append(".").append(calendarDay.getMonth()+1).append(", ");
                    }
                    sb.deleteCharAt(sb.length()-1);
                    sb.deleteCharAt(sb.length()-1);
                    filterView.setText(sb.toString());
                }
            }
        }
    }
}
