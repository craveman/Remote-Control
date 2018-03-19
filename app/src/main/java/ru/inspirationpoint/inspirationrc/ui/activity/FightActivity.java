package ru.inspirationpoint.inspirationrc.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.DataManager;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FightActionData;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FightData;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FighterData;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.manager.helpers.JSONHelper;
import ru.inspirationpoint.inspirationrc.manager.helpers.LocaleHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.CommandHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.TCPHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.CommandsContract;
import ru.inspirationpoint.inspirationrc.ui.dialog.CardDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.ChangeScoresDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.ConfirmationDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.LineDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.MessageDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.PeriodDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.PhraseConfirmDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.PhraseDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.PriorityDialog;
import ru.inspirationpoint.inspirationrc.ui.dialog.TimeDialog;
import server.schemas.requests.FightAction;
import server.schemas.requests.FightInput;
import server.schemas.responses.FighterStat;
import server.schemas.responses.SaveFightResult;
import server.schemas.responses.Training;

import static ru.inspirationpoint.inspirationrc.manager.Constants.phrasesEN;
import static ru.inspirationpoint.inspirationrc.manager.Constants.phrasesRU;
import static ru.inspirationpoint.inspirationrc.ui.activity.FightActivity.CardStatus.CardStatus_None;
import static ru.inspirationpoint.inspirationrc.ui.activity.FightActivity.CardStatus.CardStatus_NonePlus;
import static ru.inspirationpoint.inspirationrc.ui.activity.FightActivity.CardStatus.CardStatus_Yellow;
import static ru.inspirationpoint.inspirationrc.ui.activity.FightActivity.CardStatus.CardStatus_YellowPlus;


public class FightActivity extends LocalAppCompatActivity implements PriorityDialog.Listener,
        CardDialog.Listener, TimeDialog.Listener, ChangeScoresDialog.Listener,
        ConfirmationDialog.Listener, LineDialog.Listener, PeriodDialog.Listener, MessageDialog.Listener,
        PhraseDialog.Listener, PhraseConfirmDialog.Listener, TCPHelper.TCPListener{

    private final static long FIGHT_DURATION = 3 * 60 * 1000;
    private final static long PAUSE_DURATION = 60 * 1000;
    private final static long EXTRA_TIME_DURATION = 60 * 1000;
    private final static int EXIT_MESSAGE_ID = 1;
    private final static int WINNER_MESSAGE_ID = 2;
    private final static int THREE_MINS_ID = 3;
    private final static int PAUSE_ID = 4;
    private final static int RESET_ID = 5;
    private final static int END_FIGHT_ID = 6;
    private final static int SWAP_ID = 7;
    private final static int FINISH_PAUSE = 8;
    public final static int PHRASE_SELECT = 9;

    private final Object mStateSync = new Object();

    private Toolbar mToolbar;
    private View mProgressView;
    private View mMainContentView;
    private TextView mPeriodTextView;
    private TextView mDurationTextView;
    private TextView mLeftFighterPriority;
    private TextView mRightFighterPriority;
    private TextView mLeftFighterCount;
    private TextView mRightFighterCount;
    private TextView mCards;
    private ImageButton mLeftCountInc;
    private ImageButton mLeftCountDec;
    private ImageButton mRightCountInc;
    private ImageButton mRightCountDec;
    private ImageButton mTimerStartBtn;
    private ImageButton mLeftCard;
    private ImageButton mRightCard;
    private TextView mTimerMin;
    private TextView mTimerSec;
    private TextView mTimerSecDiv;
    private TextView mTimerSantisec;
    private TextView mLeftFighterName;
    private TextView mRightFighterName;
    private View mLeftFighterCard;
    private View mRightFighterCard;
    private View mStopTimerView;
    private View cardsLay;
    private RelativeLayout mScrollView;

    private boolean mIsOptionsMenuVisible = true;
    private SimpleDateFormat mMin;
    private SimpleDateFormat mSec;
    private SimpleDateFormat mSantisec;
    private TimerState mTimerState = TimerState.NotStarted;
    private long mFightStartTimeMS;
    private long mFightPhaseStartTimeMS;
    private long mPureFightDurationTillPauseMS;
    private long mPureFightDuration;
    private long mInitDurationMS;
    private Date mCurrentTime = new Date();
    private FightActionData.Fighter mPriorityFighter = FightActionData.Fighter.None;
    private boolean mPriorityChanged = false;
    private FightData mFightData;
    private FightActionData.ActionPeriod mActionPeriod = FightActionData.ActionPeriod.Fight;
    private int mLine = 0;
    private int mPeriod = 1;
    private int phraseLeft = 0;
    private int phraseRight = 0;
    private boolean isItFirstClick = false;
    private boolean isLeft = true;
    private ArrayList<Training> cacheTrainings;

    private TCPHelper tcpHelper;

    private final Runnable mTickRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (mStateSync) {
                boolean nextTickRequired = false;
                long systemTime = System.currentTimeMillis();
                switch (mTimerState) {
                    case NotStarted:
                        stopTicks();
                        break;

                    case InProgress:
                        mPureFightDuration = mPureFightDurationTillPauseMS;
                        if (mFightPhaseStartTimeMS != 0) {
                            mPureFightDuration += systemTime - mFightPhaseStartTimeMS;
                        }

                        if (mInitDurationMS <= mPureFightDuration) {
                            mPureFightDuration = mInitDurationMS;
                            goToState(TimerState.InPause);
                            onPeriodFinished();
                        }

                        nextTickRequired = true;
                        break;

                    case InPause:
                        nextTickRequired = true;
                        break;
                }

                update();

                if (nextTickRequired) {
                    scheduleNextTick();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        configureToolbar();

        mToolbar = toolbar;
        tcpHelper = InspirationDayApplication.getApplication().getHelper();
        if (tcpHelper != null) {
            tcpHelper.setListener(this);
        }

        setCustomTitle(R.string.fight);
        mMainContentView = findViewById(R.id.main_content);
        mProgressView = findViewById(R.id.login_progress);

        mLine = SettingsManager.getValue(Constants.LAST_LINE_FIELD, 0);
//        IrSender.instance().setLine(mLine);

        cardsLay = findViewById(R.id.cards_lay);

        mLeftCard = (ImageButton) findViewById(R.id.card_left);
        mLeftCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCardSet(nextStatus(CardStatus.values()[mFightData.getLeftFighter().getCard().ordinal()]),
                        CardStatus.values()[mFightData.getRightFighter().getCard().ordinal()]);
                mCards.setTextColor(getResources().getColor(R.color.textColorSecondary));
                cardsLay.setVisibility(View.INVISIBLE);
            }
        });
        mRightCard = (ImageButton) findViewById(R.id.card_right);
        mRightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCardSet(CardStatus.values()[mFightData.getLeftFighter().getCard().ordinal()],
                        nextStatus(CardStatus.values()[mFightData.getRightFighter().getCard().ordinal()]));
                mCards.setTextColor(getResources().getColor(R.color.textColorSecondary));
                cardsLay.setVisibility(View.INVISIBLE);
            }
        });

        mScrollView = (RelativeLayout) findViewById(R.id.fight_screen);
        mTimerMin = (TextView) findViewById(R.id.timer_min);
        mTimerSec = (TextView) findViewById(R.id.timer_sec);
        mTimerSecDiv = (TextView) findViewById(R.id.timer_sec_divider);
        mTimerSantisec = (TextView) findViewById(R.id.timer_santisec);
        mTimerStartBtn = (ImageButton) findViewById(R.id.timer_start);
        mTimerStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                timerButtonColorSelector();
                isItFirstClick = true;
                scoreButtonsSwitch();
                onTimerButtonClick();
                mStopTimerView.setVisibility(mTimerState == TimerState.InProgress ? View.VISIBLE : View.GONE);

            }
        });

        mPeriodTextView = (TextView) findViewById(R.id.round);
        mDurationTextView = (TextView) findViewById(R.id.duration);
        mLeftFighterName = (TextView) findViewById(R.id.left_fighter_name);
        mRightFighterName = (TextView) findViewById(R.id.right_fighter_name);
        mLeftFighterPriority = (TextView) findViewById(R.id.left_fighter_priority);
        mRightFighterPriority = (TextView) findViewById(R.id.right_fighter_priority);
        mCards = (TextView) findViewById(R.id.cards_btn);
        View cardsView = findViewById(R.id.cards_viev);
        cardsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardsLay.getVisibility() == View.VISIBLE) {
                    cardsLay.setVisibility(View.INVISIBLE);
                    mCards.setTextColor(getResources().getColor(R.color.textColorSecondary));
                } else {
                    switch (CardStatus.values()[mFightData.getLeftFighter().getCard().ordinal()]) {
                        case CardStatus_None:
                        case CardStatus_NonePlus:
                            mLeftCard.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.yellow));
                            break;

                        case CardStatus_Yellow:
                        case CardStatus_YellowPlus:
                            mLeftCard.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red));
                            break;
                    }
                    switch (CardStatus.values()[mFightData.getRightFighter().getCard().ordinal()]) {
                        case CardStatus_None:
                        case CardStatus_NonePlus:
                            mRightCard.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.yellow));
                            break;

                        case CardStatus_Yellow:
                        case CardStatus_YellowPlus:
                            mRightCard.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red));
                            break;
                    }
                    cardsLay.setVisibility(View.VISIBLE);
                    mCards.setTextColor(SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                            getResources().getColor(R.color.whiteCard) :
                            getResources().getColor(R.color.textColorPrimary));
                }
            }
        });

        mLeftCountInc = (ImageButton) findViewById(R.id.left_fighter_score_inc);
        mLeftCountDec = (ImageButton) findViewById(R.id.left_fighter_score_dec);
        mRightCountInc = (ImageButton) findViewById(R.id.right_fighter_score_inc);
        mRightCountDec = (ImageButton) findViewById(R.id.right_fighter_score_dec);

        mLeftCountInc.setOnClickListener(counterListener);
        mLeftCountDec.setOnClickListener(counterListener);
        mRightCountInc.setOnClickListener(counterListener);
        mRightCountDec.setOnClickListener(counterListener);

        mStopTimerView = findViewById(R.id.fight_stop_timer_lay);
        mStopTimerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActionPeriod != FightActionData.ActionPeriod.Pause) {
                    mStopTimerView.setVisibility(View.GONE);
//                timerButtonColorSelector();
                    onTimerButtonClick();
                } else {
                    ConfirmationDialog.show(FightActivity.this, FINISH_PAUSE, "", getResources().getString(R.string.pause_cancel_confirmation));
                }
            }
        });

        mLeftFighterCount = (TextView) findViewById(R.id.left_fighter_score);

        mRightFighterCount = (TextView) findViewById(R.id.right_fighter_score);

        scoreButtonsSwitch();

        mLeftFighterCard = findViewById(R.id.left_fighter_card);
        mRightFighterCard = findViewById(R.id.right_fighter_card);

        mMin = new SimpleDateFormat("mm", Locale.getDefault());
        mSec = new SimpleDateFormat("ss", Locale.getDefault());
        mSantisec = new SimpleDateFormat("SS", Locale.getDefault());

        mFightData = DataManager.instance().getCurrentFight();
        mFightData.setmStartTime(System.currentTimeMillis());

//        createMenu();

        if (SettingsManager.getValue(Constants.CURRENT_PAIR, 21) == 21) {
            mLeftFighterName.setText(mFightData.getLeftFighter().getName());
            mRightFighterName.setText(mFightData.getRightFighter().getName());
        } else {
            mLeftFighterName.setText(String.format("#%s %s",
                    String.valueOf(Constants.PAIRS[SettingsManager.getValue(Constants.CURRENT_PAIR, 0)].first),
                    mFightData.getLeftFighter().getName()));
            mRightFighterName.setText(String.format("#%s %s",
                    String.valueOf(Constants.PAIRS[SettingsManager.getValue(Constants.CURRENT_PAIR, 0)].second),
                    mFightData.getRightFighter().getName()));
        }

        initTime(FightActionData.ActionPeriod.Fight, 0);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_fight;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsOptionsMenuVisible) {
            getMenuInflater().inflate(R.menu.fight, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_three_mins:
                actionThreeMins();
                break;

            case R.id.action_pause:
                actionPause();
                break;

            case R.id.action_time:
                actionTime();
                break;

            case R.id.action_period:
                actionPeriod();
                break;

            case R.id.action_priority:
                actionPriority();
                break;

            case R.id.action_reset:
                actionReset();
                break;

            case R.id.action_change_score:
                actionChangeScore();
                break;

            case R.id.action_end_fight:
                actionEndFight();
                break;

            case R.id.action_swap_fighters:
                actionSwap();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopTicks();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mTimerState != TimerState.NotStarted) {
            scheduleNextTick();
        }
    }

    @Override
    public void onBackPressed() {
        ConfirmationDialog.show(this, EXIT_MESSAGE_ID, getString(R.string.warning), getString(R.string.exit_conformation));
    }

    private void scoreButtonsSwitch() {
        if (isItFirstClick) {
            mLeftCountInc.setClickable(true);
            mLeftCountDec.setClickable(true);
            mRightCountInc.setClickable(true);
            mRightCountDec.setClickable(true);
        } else {
            mLeftCountInc.setClickable(false);
            mLeftCountDec.setClickable(false);
            mRightCountInc.setClickable(false);
            mRightCountDec.setClickable(false);
        }
    }

    private void goToState(TimerState newState) {
        synchronized (mStateSync) {
            long time = System.currentTimeMillis();
            switch (mTimerState) {
                case NotStarted:
                    if (newState == TimerState.InProgress) {
                        mPureFightDurationTillPauseMS = 0;
                        mFightStartTimeMS = time;
                        mFightPhaseStartTimeMS = time;
                        onStartTimer(time);
                        scheduleNextTick();
                    }
                    break;

                case InProgress:
                    if (newState == TimerState.InPause) {
                        mPureFightDurationTillPauseMS += time - mFightPhaseStartTimeMS;
                        mFightPhaseStartTimeMS = 0;
                        onStopTimer(time);
                    }
                    break;

                case InPause:
                    if (newState == TimerState.InProgress) {
                        mFightPhaseStartTimeMS = time;
                        onStartTimer(time);
                    }
                    break;
            }
            mTimerState = newState;
        }
    }

    private void initTime(FightActionData.ActionPeriod timePeriod, long otherDurationMS) {
        FightActionData.ActionPeriod tempPeriod = mActionPeriod;
        synchronized (mStateSync) {
            mActionPeriod = timePeriod;
            switch (timePeriod) {
                case Fight:
                    mInitDurationMS = FIGHT_DURATION;
                    mTimerStartBtn.setClickable(true);
                    mTimerStartBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                    break;

                case Pause:
                    mInitDurationMS = PAUSE_DURATION;
                    mTimerStartBtn.setClickable(true);
                    mTimerStartBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                    break;

                case Extra:
                    mInitDurationMS = EXTRA_TIME_DURATION;
                    if (mPriorityFighter == FightActionData.Fighter.None) {
                        mPriorityFighter = new Random().nextBoolean() ? FightActionData.Fighter.Left : FightActionData.Fighter.Right;
                        mPriorityChanged = true;
                    }
                    mTimerStartBtn.setClickable(true);
                    mTimerStartBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                    break;

                case Other:
                    mInitDurationMS = otherDurationMS;
                    if (otherDurationMS != 0) {
                        mTimerStartBtn.setClickable(true);
                        mTimerStartBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                    }
                    break;
            }
            mFightPhaseStartTimeMS = 0;
            mPureFightDurationTillPauseMS = 0;
            mPureFightDuration = 0;

            update();
        }
        if (timePeriod == FightActionData.ActionPeriod.Pause) {
            if (tempPeriod != FightActionData.ActionPeriod.Pause) {
                mTimerStartBtn.performClick();
            } else {
                synchronized (mStateSync) {
                    long time = System.currentTimeMillis();
                    mFightPhaseStartTimeMS = time;
                    onStartTimer(time);
                }
            }
        } else if (timePeriod == FightActionData.ActionPeriod.Fight || timePeriod == FightActionData.ActionPeriod.Other) {
            if (tempPeriod == FightActionData.ActionPeriod.Pause && mTimerState == TimerState.InProgress) {
                synchronized (mStateSync) {
                    goToState(TimerState.InPause);
                    mTimerStartBtn.performClick();
                }
            }
        }
    }

    private void update() {
        synchronized (mStateSync) {
            boolean isDark = SettingsManager.getValue(Constants.IS_DARK_THEME, false);
            boolean enabledUI = mTimerState != TimerState.InProgress;
            int backColor = ContextCompat.getColor(this, !isDark ?
                    (enabledUI ? R.color.enabledBack : R.color.disabledBack) : R.color.colorPrimaryThemeDark);
            int primaryTextColor = ContextCompat.getColor(this, !isDark ?
                    (enabledUI ? R.color.textColorPrimary : R.color.textColorSecondary) : (enabledUI ? R.color.whiteCard : R.color.textColorSecondaryDarkTheme));
            if (Build.VERSION.SDK_INT > 21) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), /*enabledUI ?*/ R.drawable.ic_arrow_back_normal /*: R.drawable.ic_arrow_back_disabled*/));
                }
            }

            mIsOptionsMenuVisible = enabledUI;
            invalidateOptionsMenu();

            mTimerStartBtn.setImageDrawable(!isDark ? (getResources().getDrawable(R.drawable.button_square)) :
                    (mTimerState == TimerState.InProgress || mInitDurationMS == 0 ?
                            getResources().getDrawable(R.drawable.sq_button_inactive) :
                            getResources().getDrawable(R.drawable.sq_button_dark)));

            mTimerStartBtn.setClickable(! (mActionPeriod == FightActionData.ActionPeriod.Pause || mInitDurationMS == 0));

            mToolbar.setTitleTextColor(backColor);
//            mToolbar.setBackgroundColor(backColor);

            mScrollView.setBackgroundColor(backColor);

//            mLineTextView.setText(getString(R.string.line, mLine + 1));
            switch (mActionPeriod) {
                case Fight:
                case Pause:
                    mPeriodTextView.setText(String.valueOf(mPeriod));
                    break;

                case Extra:
                    mPeriodTextView.setText(getString(R.string.extra_round));
                    break;

                case Other:
                    mPeriodTextView.setText(String.valueOf(mPeriod));
                    break;
            }

            mLeftFighterPriority.setVisibility(mPriorityFighter == FightActionData.Fighter.Left ? View.VISIBLE : View.INVISIBLE);
            mLeftFighterPriority.setTextColor(primaryTextColor);

            mRightFighterPriority.setVisibility(mPriorityFighter == FightActionData.Fighter.Right ? View.VISIBLE : View.INVISIBLE);
            mRightFighterPriority.setTextColor(primaryTextColor);

            mLeftFighterCount.setText(String.valueOf(mFightData.getLeftFighter().getScore()));
//            int color = mFightData.getLeftFighter().isScoreIncreased() ? R.color.textColorLight : (enabledUI ? R.color.colorPrimary : R.color.disabledPrimaryDark);
            int drawable = !isDark ? (mFightData.getLeftFighter().isScoreIncreased() ? R.drawable.pull : R.drawable.up) :
                    (mFightData.getLeftFighter().isScoreIncreased() ? R.drawable.push_up_dark : R.drawable.up_dark);
//            mLeftFighterCount.setTextColor(ContextCompat.getColor(this, color));
            mLeftCountInc.setBackground(ContextCompat.getDrawable(this, drawable));
            mLeftCountInc.setEnabled(enabledUI && !mFightData.getLeftFighter().isScoreIncreased());
            mLeftCountDec.setEnabled(enabledUI && mFightData.getLeftFighter().isScoreIncreased());
            mLeftCountDec.setBackground(isDark ? getResources().getDrawable(R.drawable.down_dark) :
                    getResources().getDrawable(R.drawable.down));

            mRightFighterCount.setText(String.valueOf(mFightData.getRightFighter().getScore()));
//            color = mFightData.getRightFighter().isScoreIncreased() ? R.color.textColorLight : (enabledUI ? R.color.colorPrimary : R.color.disabledPrimaryDark);
//            mRightFighterCount.setTextColor(ContextCompat.getColor(this, color));
            drawable = !isDark ? (mFightData.getRightFighter().isScoreIncreased() ? R.drawable.pull : R.drawable.up) :
                    (mFightData.getRightFighter().isScoreIncreased() ? R.drawable.push_up_dark : R.drawable.up_dark);
            mRightCountInc.setBackground(ContextCompat.getDrawable(this, drawable));
            mRightCountInc.setEnabled(enabledUI && !mFightData.getRightFighter().isScoreIncreased());
            mRightCountDec.setEnabled(enabledUI && mFightData.getRightFighter().isScoreIncreased());
            mRightCountDec.setBackground(isDark ? getResources().getDrawable(R.drawable.down_dark) :
                    getResources().getDrawable(R.drawable.down));

            switch (mFightData.getLeftFighter().getCard()) {
                case CardStatus_None:
                    mLeftFighterCard.setVisibility(View.GONE);
                    break;

                case CardStatus_NonePlus:
                case CardStatus_Yellow:
                    mLeftFighterCard.setVisibility(View.VISIBLE);
                    mLeftFighterCard.setBackground(ContextCompat.getDrawable(this, R.drawable.yellow));
                    mLeftFighterCard.setEnabled(enabledUI);
                    break;

                case CardStatus_YellowPlus:
                    mLeftFighterCard.setVisibility(View.VISIBLE);
                    mLeftFighterCard.setBackground(ContextCompat.getDrawable(this, R.drawable.red));
                    mLeftFighterCard.setEnabled(enabledUI);
                    break;
            }

            switch (mFightData.getRightFighter().getCard()) {
                case CardStatus_None:
                    mRightFighterCard.setVisibility(View.GONE);
                    break;

                case CardStatus_NonePlus:
                case CardStatus_Yellow:
                    mRightFighterCard.setVisibility(View.VISIBLE);
                    mRightFighterCard.setBackground(ContextCompat.getDrawable(this, R.drawable.yellow));
                    mRightFighterCard.setEnabled(enabledUI);
                    break;

                case CardStatus_YellowPlus:
                    mRightFighterCard.setVisibility(View.VISIBLE);
                    mRightFighterCard.setBackground(ContextCompat.getDrawable(this, R.drawable.red));
                    mRightFighterCard.setEnabled(enabledUI);
                    break;
            }

            if (mInitDurationMS > mPureFightDuration) {
                mCurrentTime.setTime(mInitDurationMS - mPureFightDuration);
                if (mCurrentTime.getTime()/1000 < 10) {
                    mTimerSantisec.setVisibility(View.VISIBLE);
                    mTimerSecDiv.setVisibility(View.VISIBLE);
                    mTimerSantisec.setText(mSantisec.format(mCurrentTime));
                    mTimerSec.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    mTimerMin.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    mTimerMin.setTextColor(getResources().getColor(R.color.textColorRed));
                    mTimerSec.setTextColor(getResources().getColor(R.color.textColorRed));
                    mTimerSantisec.setTextColor(getResources().getColor(R.color.textColorRed));
                } else {
                    mTimerSantisec.setVisibility(View.GONE);
                    mTimerSecDiv.setVisibility(View.GONE);
                    mTimerSec.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    mTimerMin.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    mTimerMin.setTextColor(!isDark ? getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
                    mTimerSec.setTextColor(!isDark ? getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
                    mTimerSantisec.setTextColor(!isDark ? getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
                }
                if (mTimerState == TimerState.InProgress) {
                    if (!mSec.format(mCurrentTime).equals(mTimerSec.getText().toString())) {
                        beep(mCurrentTime.getTime() / 100 > 100 ? 50 : 100);
                    }
                }
                mTimerMin.setText(mMin.format(mCurrentTime));
                mTimerSec.setText(mSec.format(mCurrentTime));
            } else {
                mCurrentTime.setTime(0);
                if (mCurrentTime.getTime()/1000 < 10) {
                    mTimerSantisec.setVisibility(View.VISIBLE);
                    mTimerSecDiv.setVisibility(View.VISIBLE);
                    mTimerSantisec.setText(mSantisec.format(mCurrentTime));
                    mTimerSec.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    mTimerMin.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    mTimerMin.setTextColor(getResources().getColor(R.color.textColorRed));
                    mTimerSec.setTextColor(getResources().getColor(R.color.textColorRed));
                    mTimerSantisec.setTextColor(getResources().getColor(R.color.textColorRed));
                } else {
                    mTimerSantisec.setVisibility(View.GONE);
                    mTimerSecDiv.setVisibility(View.GONE);
                    mTimerSec.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    mTimerMin.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    mTimerMin.setTextColor(!isDark ? getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
                    mTimerSec.setTextColor(!isDark ? getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
                    mTimerSantisec.setTextColor(!isDark ? getResources().getColor(R.color.textColorPrimary) : getResources().getColor(R.color.whiteCard));
                }
                mTimerMin.setText(mMin.format(mCurrentTime));
                mTimerSec.setText(mSec.format(mCurrentTime));
                if (mTimerState == TimerState.InProgress) {
                    if (!mSec.format(mCurrentTime).equals(mTimerSec.getText().toString())) {
                        beep(mCurrentTime.getTime() / 100 > 100 ? 50 : 100);
                    }
                }
            }

            if (mFightStartTimeMS == 0) {
                mCurrentTime.setTime(0);
            } else {
                mCurrentTime.setTime(System.currentTimeMillis() - mFightStartTimeMS);
            }
        }
    }

    private void onTimerButtonClick() {
        beep(500);
        synchronized (mStateSync) {
            if (tcpHelper != null) {
                try {
                    tcpHelper.send(CommandHelper.startTimer(mTimerState != TimerState.InProgress));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            goToState(mTimerState == TimerState.InProgress ? TimerState.InPause : TimerState.InProgress);
        }
    }

    private View.OnClickListener counterListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.left_fighter_score_inc:
                    onLeftFighterCounterClick(true);
                    break;
                case R.id.left_fighter_score_dec:
                    onLeftFighterCounterClick(false);
                    break;
                case R.id.right_fighter_score_inc:
                    onRightFighterCounterClick(true);
                    break;
                case R.id.right_fighter_score_dec:
                    onRightFighterCounterClick(false);
                    break;
            }
        }
    };

    private void onLeftFighterCounterClick(boolean inc) {
        synchronized (mStateSync) {
            if (mFightData.getLeftFighter().isScoreIncreased() && mFightData.getRightFighter().getCard() == CardStatus_YellowPlus) {
                mFightData.getRightFighter().setCard(CardStatus_Yellow);
            }
            mFightData.getLeftFighter().toggleScore(true);
            if (inc && SettingsManager.getValue(Constants.IS_PHRASES_ENABLED, false)) {
                PhraseDialog.show(this, true);
                isLeft = true;
            }
            update();
        }
    }

    private void onRightFighterCounterClick(boolean inc) {
        synchronized (mStateSync) {
            if (mFightData.getRightFighter().isScoreIncreased() && mFightData.getLeftFighter().getCard() == CardStatus_YellowPlus) {
                mFightData.getLeftFighter().setCard(CardStatus_Yellow);
            }
            mFightData.getRightFighter().toggleScore(false);
            if (inc && SettingsManager.getValue(Constants.IS_PHRASES_ENABLED, false)) {
                isLeft = false;
                PhraseDialog.show(this, false);
            }
            update();
        }
    }

    private void onStartTimer(long time) {
        if (mFightData.getLeftFighter().getCard() == CardStatus_NonePlus) {
            mFightData.getLeftFighter().setCard(CardStatus_Yellow);
            mFightData.addAction(FightActionData.createSetCardLeft(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0
                    , mPeriod, FightActionData.Fighter.Left, true, System.currentTimeMillis()));
        } else if (mFightData.getLeftFighter().getCard() == CardStatus_YellowPlus) {
            mFightData.getLeftFighter().setCard(CardStatus_Yellow);
            mFightData.addAction(FightActionData.createSetCardLeft(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0
                    , mPeriod, FightActionData.Fighter.Left, false, System.currentTimeMillis()));
        }

        if (mFightData.getRightFighter().getCard() == CardStatus_NonePlus) {
            mFightData.getRightFighter().setCard(CardStatus_Yellow);
            mFightData.addAction(FightActionData.createSetCardRight(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0
                    , mPeriod, FightActionData.Fighter.Right, true, System.currentTimeMillis()));
        } else if (mFightData.getRightFighter().getCard() == CardStatus_YellowPlus) {
            mFightData.getRightFighter().setCard(CardStatus_Yellow);
            mFightData.addAction(FightActionData.createSetCardRight(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0
                    , mPeriod, FightActionData.Fighter.Right, false, System.currentTimeMillis()));
        }

        //TODO
        if (mFightData.getLeftFighter().isScoreChanged()) {
            mFightData.getLeftFighter().applyScoreChanges();
            if (mFightData.getLeftFighter().getScore() != 0) {
                mFightData.addAction(FightActionData.createSetScoreLeft(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0
                        , mPeriod, FightActionData.Fighter.Left, mFightData.getLeftFighter().getScore(), System.currentTimeMillis(), phraseLeft));
            }
            phraseLeft = 0;
        }

        if (mFightData.getRightFighter().isScoreChanged()) {
            mFightData.getRightFighter().applyScoreChanges();
            if (mFightData.getRightFighter().getScore() != 0) {
                mFightData.addAction(FightActionData.createSetScoreRight(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0
                        , mPeriod, FightActionData.Fighter.Right, mFightData.getRightFighter().getScore(), System.currentTimeMillis(), phraseRight));
            }
            phraseRight = 0;
        }

        if (mPriorityChanged) {
            mPriorityChanged = false;
            if (mPriorityFighter == FightActionData.Fighter.Left) {
                mFightData.addAction(FightActionData.createSetPriorityLeft(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0
                        , mPeriod, mPriorityFighter, System.currentTimeMillis()));
            } else {
                mFightData.addAction(FightActionData.createSetPriorityRight(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0
                        , mPeriod, mPriorityFighter, System.currentTimeMillis()));
            }
        }

        mFightData.addAction(FightActionData.createStartRound(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0, mPeriod, mActionPeriod, System.currentTimeMillis(),
                mFightData.getActionsList().size() == 0 ? String.valueOf(SettingsManager.getValue(Constants.IS_PHRASES_ENABLED, false)) : ""));
    }

    private void onStopTimer(long time) {
        mFightData.addAction(FightActionData.createStopRound(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0
                , mPeriod, mActionPeriod, System.currentTimeMillis()));
    }

    private void onPeriodFinished() {
        beep(500);

        mFightPhaseStartTimeMS = 0;
        mPureFightDurationTillPauseMS = 0;
        mPureFightDuration = 0;

        if (mActionPeriod == FightActionData.ActionPeriod.Pause) {
            initTime(FightActionData.ActionPeriod.Fight, 0);
        } else {
            initTime(FightActionData.ActionPeriod.Other, 0);
        }
    }

    private void actionTime() {
        TimeDialog.show(this, Integer.parseInt(mTimerMin.getText().toString()),
                Integer.parseInt(mTimerSec.getText().toString()),
                mTimerSantisec.getVisibility() == View.VISIBLE ? (Integer.parseInt(mTimerSantisec.getText().toString())) : 0);
    }

    private void actionPeriod() {
        PeriodDialog.show(this, mPeriod);
    }

    private void actionPriority() {
        PriorityDialog.show(this, mFightData.getLeftFighter().getName(), mFightData.getRightFighter().getName(), mPriorityFighter);
    }

//    private void actionAddComment() {
//        CommentDialog.show(this, 0, "");
//    }
//
    private void actionChangeScore() {
        ChangeScoresDialog.show(this, mFightData.getLeftFighter().getName(), mFightData.getRightFighter().getName(), mFightData.getLeftFighter().getScore(), mFightData.getRightFighter().getScore());
    }

    private void actionThreeMins() {
        ConfirmationDialog.show(this, THREE_MINS_ID, "", getResources().getString(R.string.reset_to_three_mins_confirm));
    }

    private void actionPause(){
        ConfirmationDialog.show(this, PAUSE_ID, "", getResources().getString(R.string.pause_confirmation));
    }

    private void actionReset() {
        ConfirmationDialog.show(this, RESET_ID, "", getResources().getString(R.string.reset_confirm));
    }

    private void actionSwap() {
        ConfirmationDialog.show(this, SWAP_ID, "", getResources().getString(R.string.swap_fighters) + "?");
    }

    private void actionEndFight() {
        onStartTimer(System.currentTimeMillis());

        if (mTimerState == TimerState.NotStarted) {
            MessageDialog.show(this, WINNER_MESSAGE_ID, getString(R.string.not_started_fight_title), getString(R.string.not_started_fight));
            return;
        }

        if (mFightData.getLeftFighter().getScore() == mFightData.getRightFighter().getScore() && mPriorityFighter == FightActionData.Fighter.None) {
            MessageDialog.show(this, WINNER_MESSAGE_ID, getString(R.string.winner_not_detected_title), getString(R.string.winner_not_detected));
            return;
        }

        ConfirmationDialog.show(this, END_FIGHT_ID, "", getResources().getString(R.string.end_fight_confirm));
    }

    void beep(int soundDuration) {
        boolean soundOn = SettingsManager.getValue(Constants.SOUND_FIELD, true);
        boolean vibrationOn = SettingsManager.getValue(Constants.VIBRATION_FIELD, true);
        Helper.beep(FightActivity.this, soundOn ? soundDuration : 0, vibrationOn ? soundDuration : 0);
    }

    private void scheduleNextTick() {
        mDurationTextView.postDelayed(mTickRunnable, 10);
    }

    private void stopTicks() {
        mDurationTextView.removeCallbacks(mTickRunnable);
    }

    @Override
    public void onPrioritySet(FightActionData.Fighter fighter) {
        if (mPriorityFighter != fighter) {
            mPriorityFighter = fighter;
            mPriorityChanged = true;
            update();
        }
    }

    @Override
    public void onCardSet(CardStatus leftFighter, CardStatus rightFighter) {
        phraseLeft = 10;
        phraseRight = 10;
        if (mFightData.getLeftFighter().getCard() != leftFighter) {
            mFightData.getLeftFighter().setCard(leftFighter);
            switch (leftFighter) {
                case CardStatus_None:
                case CardStatus_NonePlus:
                    break;

                case CardStatus_Yellow:
                    if (mFightData.getRightFighter().isScoreIncreased()) {
                        mFightData.getRightFighter().toggleScore(false);
                    }
                    phraseRight = 10;
                    break;

                case CardStatus_YellowPlus:
                    if (!mFightData.getRightFighter().isScoreIncreased()) {
                        mFightData.getRightFighter().toggleScore(false);
                    }
                    phraseRight = 10;
                    break;
            }
        }

        if (mFightData.getRightFighter().getCard() != rightFighter) {
            mFightData.getRightFighter().setCard(rightFighter);
            switch (rightFighter) {
                case CardStatus_None:
                case CardStatus_NonePlus:
                    break;

                case CardStatus_Yellow:
                    if (mFightData.getLeftFighter().isScoreIncreased()) {
                        mFightData.getLeftFighter().toggleScore(true);
                    }
                    phraseLeft = 10;
                    break;

                case CardStatus_YellowPlus:
                    if (!mFightData.getLeftFighter().isScoreIncreased()) {
                        mFightData.getLeftFighter().toggleScore(true);
                    }
                    phraseLeft = 10;
                    break;
            }
        }

        update();
    }

    @Override
    public void onDurationSet(FightActionData.ActionPeriod actionPeriod, long otherDuration) {
        switch (actionPeriod) {
            case Fight:
                mFightData.addAction(FightActionData.createSetTime(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0, mPeriod, FIGHT_DURATION, System.currentTimeMillis()));
                if (tcpHelper != null) {
                    try {
                        tcpHelper.send(CommandHelper.setTimer(FIGHT_DURATION));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Other:
                mFightData.addAction(FightActionData.createSetTime(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0, mPeriod, otherDuration, System.currentTimeMillis()));
                if (tcpHelper != null) {
                    try {
                        tcpHelper.send(CommandHelper.setTimer(otherDuration));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Pause:
            case Extra:
                break;
        }
        initTime(actionPeriod, otherDuration);
    }

    @Override
    public void onScoreChanged(int leftScore, final int rightScore) {
        mFightData.getLeftFighter().setScore(leftScore);
        mFightData.getRightFighter().setScore(rightScore);

        if (tcpHelper != null) {
            tcpHelper.setListener(new TCPHelper.TCPListener() {
                @Override
                public void onReceive(byte[] message) {
                    if (CommandHelper.getCommand(message) == CommandsContract.SETSCORE_TCP_CMD &&
                            CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_LEFT) {
                        try {
                            tcpHelper.send(CommandHelper.setScore(CommandsContract.PERSON_TYPE_RIGHT, rightScore));
                            tcpHelper.setListener(FightActivity.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            try {
                tcpHelper.send(CommandHelper.setScore(CommandsContract.PERSON_TYPE_LEFT, leftScore));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        update();
    }

    @Override
    public void onConfirmed(int messageId) {
        switch (messageId) {
            case FINISH_PAUSE:
                onTimerButtonClick();
                initTime(FightActionData.ActionPeriod.Fight, 0);
                if (tcpHelper != null) {
                    tcpHelper.setListener(new TCPHelper.TCPListener() {
                        @Override
                        public void onReceive(byte[] message) {
                            if (CommandHelper.getCommand(message) == CommandsContract.SETTIMER_TCP_CMD) {
                                try {
                                    tcpHelper.send(CommandHelper.startTimer(true));
                                    tcpHelper.setListener(FightActivity.this);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    try {
                        tcpHelper.send(CommandHelper.setTimer(180000));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mFightData.addAction(FightActionData.createSetTime(mInitDurationMS > mPureFightDuration ?
                        mInitDurationMS - mPureFightDuration : 0, mPeriod, FIGHT_DURATION, System.currentTimeMillis()));
                break;
            case EXIT_MESSAGE_ID:
                //TODO !!!!!
//                mFightData.setmEndTime(System.currentTimeMillis());
//                SettingsManager.setValue(Constants.CURRENT_PAIR, 21);
//                Intent intent = new Intent(FightActivity.this, TrainingListActivity.class);
//                startActivity(intent);
//                finish();
                break;
            case THREE_MINS_ID:
                onDurationSet(FightActionData.ActionPeriod.Fight, 0);
                if (tcpHelper != null) {
                    try {
                        tcpHelper.send(CommandHelper.setTimer(FIGHT_DURATION));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PAUSE_ID:
                //TODO handle in TCP
                onDurationSet(FightActionData.ActionPeriod.Pause, 0);
                mFightData.addAction(FightActionData.createSetPause(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0, mPeriod, System.currentTimeMillis()));
                break;
            case RESET_ID:
                mFightData.addAction(FightActionData.createResetAction(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0, mPeriod, System.currentTimeMillis()));
                mTimerState = TimerState.NotStarted;
                mPureFightDuration = 0;
                mCurrentTime = new Date();
                mPriorityFighter = FightActionData.Fighter.None;
                mPriorityChanged = false;
                mFightData.getRightFighter().setCard(CardStatus_None);
                mFightData.getLeftFighter().setCard(CardStatus_None);
                mFightData.getLeftFighter().setScore(0);
                mFightData.getRightFighter().setScore(0);
                mActionPeriod = FightActionData.ActionPeriod.Fight;
                if (tcpHelper != null) {
                    tcpHelper.setListener(new TCPHelper.TCPListener() {
                        @Override
                        public void onReceive(byte[] message) {
                            if (CommandHelper.getCommand(message) == CommandsContract.SETPERIOD_TCP_CMD) {
                                try {
                                    tcpHelper.send(CommandHelper.setTimer(FIGHT_DURATION));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (CommandHelper.getCommand(message) == CommandsContract.SETTIMER_TCP_CMD) {
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
                                    tcpHelper.send(CommandHelper.setPriority(CommandsContract.PERSON_TYPE_LEFT, false));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (CommandHelper.getCommand(message) == CommandsContract.SETPRIORITY_TCP_CMD &&
                                    CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_LEFT) {
                                try {
                                    tcpHelper.send(CommandHelper.setPriority(CommandsContract.PERSON_TYPE_RIGHT, false));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (CommandHelper.getCommand(message) == CommandsContract.SETPRIORITY_TCP_CMD &&
                                    CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_RIGHT) {
                                try {
                                    tcpHelper.send(CommandHelper.setCard(CommandsContract.PERSON_TYPE_LEFT, false));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (CommandHelper.getCommand(message) == CommandsContract.SETCARD_TCP_CMD &&
                                    CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_LEFT) {
                                try {
                                    tcpHelper.send(CommandHelper.setCard(CommandsContract.PERSON_TYPE_RIGHT, false));
                                    tcpHelper.setListener(FightActivity.this);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    try {
                        tcpHelper.send(CommandHelper.setPeriod(1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mPeriod = 1;
                isItFirstClick = false;
                initTime(FightActionData.ActionPeriod.Fight, 0);
                break;
            case END_FIGHT_ID:
                mFightData.setmEndTime(System.currentTimeMillis());
                final FightInput fight = new FightInput();
                fight._id = "";
                fight.date = Helper.dateToServerString(mFightData.getDate());
                fight.address = mFightData.getPlace();
                fight.leftFighterId = mFightData.getLeftFighter().getId();
                fight.leftFighterName = mFightData.getLeftFighter().getName();
                fight.rightFighterId = mFightData.getRightFighter().getId();
                fight.rightFighterName = mFightData.getRightFighter().getName();
                fight.actions = new FightAction[mFightData.getActionsNumber()];
                fight.startTime = mFightData.getmStartTime();
                fight.endTime = mFightData.getmEndTime();
                for (int i = 0; i < mFightData.getActionsNumber(); ++i) {
                    FightActionData actionData = mFightData.getAction(i);
                    fight.actions[i] = new FightAction();
                    fight.actions[i].type = actionData.getStringActionType();
                    Log.d("IN FIGHT", actionData.getStringActionType());
                    fight.actions[i].timestamp = actionData.getTime();
                    fight.actions[i].fighter = actionData.getStringFighter();
                    fight.actions[i].period = String.valueOf(actionData.getFightPeriod());
                    fight.actions[i].score = actionData.getScore();
                    fight.actions[i].fightPeriod = actionData.getFightPeriod();
                    fight.actions[i].comment = i == 0 ? actionData.getComment() : String.valueOf(actionData.getSystemTime());
                    fight.actions[i].establishedTime = actionData.getEstablishedTime();
                    fight.actions[i].phrase = actionData.getPhrase();
                }

                ArrayList<FighterStat> statArrayList = (ArrayList<FighterStat>) JSONHelper.importFromJSON(this, JSONHelper.ItemClass.FighterStat);
                if (statArrayList == null) {
                    statArrayList = new ArrayList<>();
                }
                FighterStat stat = new FighterStat(fight.leftFighterId, fight.leftFighterName);
                FighterStat stat1 = new FighterStat(fight.rightFighterId, fight.rightFighterName);
                for (FighterStat statistic : statArrayList) {
                    if (statistic.name.equals(stat.name)) {
                        stat = statistic;
                    } else if (statistic.name.equals(stat1.name)) {
                        stat1 = statistic;
                    }
                }
                statArrayList.remove(stat);
                statArrayList.remove(stat1);
                stat.fightCount++;
                stat1.fightCount++;
                statArrayList.add(stat);
                statArrayList.add(stat1);
                Log.d("FIGHT", statArrayList.size() + statArrayList.get(0).name);
                JSONHelper.exportToJSON(this, statArrayList);

//                cacheTrainings = (ArrayList<Training>) JSONHelper.importFromJSON(FightActivity.this, JSONHelper.ItemClass.Training);
//                if (cacheTrainings == null) {
//                    cacheTrainings = new ArrayList<>();
//                }
//                boolean isExist = false;
//                if (cacheTrainings.size() != 0) {
//                    for (Training training : cacheTrainings) {
//                        if (training.date.equals(fight.date) && training.address.equals(fight.address)) {
//                            training.fightsCount = training.fightsCount + 1;
//                            isExist = true;
//                        }
//                    }
//                }
//                if (!isExist) {
//                    Training training = new Training();
//                    training.date = fight.date;
//                    training.address = fight.address;
//                    training.fightsCount = 1;
//                    cacheTrainings.add(training);
//                }
//                JSONHelper.exportToJSON(FightActivity.this, cacheTrainings);

                SettingsManager.setValue(Constants.IS_PHRASES_ENABLED, true);

                DataManager.instance().saveFight(fight, new DataManager.RequestListener<SaveFightResult>() {
                    @Override
                    public void onSuccess(SaveFightResult result) {
                        Log.d("FIGHT RESULT UPL", "++++");
                        mFightData.setId(result.fight._id);
                        Intent intent = new Intent(FightActivity.this, FightResultActivity.class);
                        intent.putExtra("fight", true);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailed(String error, String message) {
                        Log.d("FIGHT RESULT UPL", "ERR:" + error + " " + message);
//                String messageText = getString(R.string.save_fight_error, message);
//                MessageDialog.show(FightActivity.this, 0, getString(R.string.error), messageText);

                        ArrayList<FightInput> dataList = (ArrayList<FightInput>) JSONHelper.importFromJSON(FightActivity.this, JSONHelper.ItemClass.FightInput);

                        if (dataList == null) {
                            dataList = new ArrayList<>();
                        }
                        if (dataList.size() >= 100) {
                            for (int i = 0; i < dataList.size() - 99; i++) {
                                FightInput input = dataList.get(i);
                                for (Training training : cacheTrainings) {
                                    if (training.date.equals(input.date) && training.address.equals(input.address)) {
                                        training.fightsCount --;
                                    }
                                }
                                dataList.remove(input);
                            }
                        }
                        dataList.add(fight);
                        JSONHelper.exportToJSON(FightActivity.this, dataList);
                        Intent intent = new Intent(FightActivity.this, FightResultActivity.class);
                        intent.putExtra("fight", true);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onStateChanged(boolean inProgress) {
                        showProgress(inProgress);
                    }
                });
                break;
            case SWAP_ID:
                FighterData temp = mFightData.getLeftFighter();
                mFightData.setmLeftFighterData(mFightData.getRightFighter());
                mFightData.setmRightFighterData(temp);
                mLeftFighterName.setText(mFightData.getLeftFighter().getName());
                mRightFighterName.setText(mFightData.getRightFighter().getName());
                if (tcpHelper != null) {
                    tcpHelper.setListener(new TCPHelper.TCPListener() {
                        @Override
                        public void onReceive(byte[] message) {
                            //TODO maybe handle another fighters data
                            if (CommandHelper.getCommand(message) == CommandsContract.SETNAME_TCP_CMD &&
                                    CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_RIGHT) {
                                try {
                                    tcpHelper.send(CommandHelper.setName(CommandsContract.PERSON_TYPE_LEFT, mFightData.getLeftFighter().getName()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (CommandHelper.getCommand(message) == CommandsContract.SETNAME_TCP_CMD &&
                                    CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_LEFT) {
                                try {
                                    tcpHelper.send(CommandHelper.setScore(CommandsContract.PERSON_TYPE_LEFT, mFightData.getLeftFighter().getScore()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (CommandHelper.getCommand(message) == CommandsContract.SETSCORE_TCP_CMD &&
                                    CommandHelper.getPerson(message) == CommandsContract.PERSON_TYPE_LEFT) {
                                try {
                                    tcpHelper.send(CommandHelper.setScore(CommandsContract.PERSON_TYPE_RIGHT, mFightData.getRightFighter().getScore()));
                                    tcpHelper.setListener(FightActivity.this);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    try {
                        tcpHelper.send(CommandHelper.setName(CommandsContract.PERSON_TYPE_RIGHT, mFightData.getRightFighter().getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                update();
                break;
        }
    }

    @Override
    public void onLineSet(int line) {
        mLine = line;
        update();
    }

    @Override
    public void onPeriodChanged(final int period) {
        mPeriod = period;
        initTime(FightActionData.ActionPeriod.Fight, 0);
        if (tcpHelper != null) {
            tcpHelper.setListener(new TCPHelper.TCPListener() {
                @Override
                public void onReceive(byte[] message) {
                    if (CommandHelper.getCommand(message) == CommandsContract.SETTIMER_TCP_CMD) {
                        try {
                            tcpHelper.send(CommandHelper.setPeriod(period));
                            tcpHelper.setListener(FightActivity.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            try {
                tcpHelper.send(CommandHelper.setTimer(180000));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mFightData.addAction(FightActionData.createSetPeriod(mInitDurationMS > mPureFightDuration ? mInitDurationMS - mPureFightDuration : 0, period, System.currentTimeMillis()));
        update();
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mMainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onMessageDialogDismissed(int messageId) {

    }

    @Override
    public void onEventSelected(int position, boolean isLeft, boolean cancelled) {
        if (cancelled) {
            if (isLeft) {
                onLeftFighterCounterClick(false);
            } else {
                onRightFighterCounterClick(false);
            }
        } else {
            if (isLeft) phraseLeft = position;
            else phraseRight = position;
            PhraseConfirmDialog.show(this, PHRASE_SELECT,
                    getResources().getString(R.string.confirm_event), (LocaleHelper.getLanguage(this).equals("ru") ? phrasesRU[position] : phrasesEN[position]));
        }
    }

    @Override
    public void onPhraseConfirmed(int messageId) {
        switch (messageId) {
            case PHRASE_SELECT:
                if (isLeft && !mFightData.getLeftFighter().isScoreIncreased()) {
                    mFightData.getLeftFighter().toggleScore(true);
                } else if (!isLeft && !mFightData.getRightFighter().isScoreIncreased()) {
                    mFightData.getRightFighter().toggleScore(false);
                }
                break;
            case 11:
                if (isLeft) {
                    phraseLeft = 0;
                    mFightData.getLeftFighter().toggleScore(true);
                } else {
                    phraseRight = 0;
                    mFightData.getRightFighter().toggleScore(false);
                }
                PhraseDialog.show(this, isLeft);
                break;
        }
    }

    @Override
    public void onReceive(byte[] message) {

    }

    private enum TimerState {
        NotStarted, InProgress, InPause
    }

    public enum CardStatus {
        CardStatus_None, CardStatus_NonePlus, CardStatus_Yellow, CardStatus_YellowPlus
    }

    private CardStatus nextStatus(CardStatus cardStatus) {
        switch (cardStatus) {
            case CardStatus_None:
                return CardStatus_NonePlus;

            case CardStatus_NonePlus:
                return CardStatus_None;

            case CardStatus_Yellow:
                return CardStatus_YellowPlus;
        }
        return CardStatus_Yellow;
    }
}
