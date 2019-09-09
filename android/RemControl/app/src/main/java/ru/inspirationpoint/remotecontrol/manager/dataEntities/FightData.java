package ru.inspirationpoint.remotecontrol.manager.dataEntities;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.manager.ActionUploadCallback;
import ru.inspirationpoint.remotecontrol.manager.Camera;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;

public class FightData implements Cloneable, Serializable {

    @SerializedName("mId")
    private String mId;
    @SerializedName("mDate")
    private Date mDate;
    @SerializedName("mLeftFighterData")
    private FighterData mLeftFighterData;
    @SerializedName("mRightFighterData")
    private FighterData mRightFighterData;
    @SerializedName("mPlace")
    private String mPlace;
    @SerializedName("mOwner")
    private String mOwner;
    @SerializedName("mActionsList")
    private ArrayList<FightActionData> mActionsList = new ArrayList<>();
    @SerializedName("mStartTime")
    private long mStartTime;
    @SerializedName("mEndTime")
    private long mEndTime;
    @SerializedName("mCurrentTime")
    private long mCurrentTime;
    @SerializedName("mCurrentPeriod")
    private int mCurrentPeriod = 1;
    @SerializedName("mPriority")
    private int mPriority = 0;
    @SerializedName("mVideoLeft")
    private int mVideoLeft = 2;
    @SerializedName("mVideoRight")
    private int mVideoRight = 2;

    public FightData() {
    }

    public FightData(String id, Date date, FighterData leftFighterData, FighterData rightFighterData, String place, String owner) {
        mId = id;
        mDate = date;
        mLeftFighterData = leftFighterData;
        mRightFighterData = rightFighterData;
        mPlace = place;
        mOwner = owner;
    }

//    public FightData(FightOutput fightOutput) {
//        this(fightOutput._id, Helper.serverStringToDate(fightOutput.date), new FighterData(fightOutput.leftFighterId, fightOutput.leftFighterName),
//                new FighterData(fightOutput.rightFighterId, fightOutput.rightFighterName), fightOutput.address, fightOutput.ownerName);
//        Log.wtf("FD", (fightOutput.actions != null) + "");
//        if (fightOutput.actions != null) {
//            for (FightAction fightAction : fightOutput.actions) {
//                FightActionData fightActionData = new FightActionData(fightAction);
//                mActionsList.add(fightActionData);
//                switch (fightActionData.getActionType()) {
//                    case SetScoreLeft:
//                        mLeftFighterData.setScore(fightActionData.getScore());
//                        break;
//                    case SetScoreRight:
//                        mRightFighterData.setScore(fightActionData.getScore());
//                        break;
//                    case YellowCardLeft:
//                        mLeftFighterData.setCard(CardStatus.CardStatus_Yellow);
//                        break;
//                    case YellowCardRight:
//                        mRightFighterData.setCard(CardStatus.CardStatus_Yellow);
//                        break;
//                }
//            }
//        }
//        this.mStartTime = fightOutput.startTime;
//        this.mEndTime = fightOutput.endTime;
//        mLeftFighterData.applyScoreChanges();
//        mRightFighterData.applyScoreChanges();
//    }

//    public static ArrayList<FightData> getFightData(FightOutput[] fightOutputArray) {
//        ArrayList<FightData> fightArray = new ArrayList<>();
//        for (FightOutput fight : fightOutputArray) {
//            FightData fightData = new FightData(fight);
//            fightArray.add(fightData);
//        }
//        return fightArray;
//    }

    public DataPoint[] getPointArray(int zeroColor, int leftColor, int rightColor) {
        ArrayList<DataPoint> pointArray = new ArrayList<>();
        int leftScore = 0;
        int rightScore = 0;
        long startTime = getmStartTime();
        pointArray.add(new DataPoint(0, 0));
        for (FightActionData action : mActionsList) {

            if (action.getActionType() == FightActionData.ActionType.SetScoreLeft) {
                leftScore = action.getScore();
                double y = leftScore - rightScore;
                int color = y > pointArray.get(pointArray.size() - 1).getY() ? leftColor : rightColor;
                pointArray.add(new DataPoint(action.getSystemTime() - startTime, y));
            } else if (action.getActionType() == FightActionData.ActionType.SetScoreRight) {
                rightScore = action.getScore();
                double y = leftScore - rightScore;
                int color = y > pointArray.get(pointArray.size() - 1).getY() ? leftColor : rightColor;
                pointArray.add(new DataPoint(action.getSystemTime() - startTime, y));
            }
        }

        return pointArray.toArray(new DataPoint[pointArray.size()]);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setmStartTime (long startTime) {
        this.mStartTime = startTime;
    }

    public void setmEndTime (long endTime) {
        this.mEndTime = endTime;
    }

    public Date getEndTime() {
        Date date = new Date();
        if (mEndTime != 0) {
            date.setTime(mEndTime);
        }
        return date;
    }

    public long getmEndTime() {
        return mEndTime;
    }

    public Date getDate() {
        return mDate;
    }

    public Date getTime() {
        Date date = new Date();
        if (mStartTime != 0) {
            date.setTime(mStartTime);
        }
        return date;
    }

    public long getmStartTime() {
        return mStartTime;
    }

    public long getDuration() {
        if (mActionsList.size() > 1) {
            long startTime = mActionsList.get(0).getTime();
            for (int i = mActionsList.size() - 1; i != 0; --i) {
                long endTime = mActionsList.get(i).getTime();
                if (startTime < endTime) {
                    return endTime - startTime;
                }
            }
        }

        return 0;
    }

    public FighterData getLeftFighter() {
        return mLeftFighterData;
    }

    public FighterData getRightFighter() {
        return mRightFighterData;
    }

    public void setmLeftFighterData(FighterData mLeftFighterData) {
        this.mLeftFighterData = mLeftFighterData;
    }

    public void setmRightFighterData(FighterData mRightFighterData) {
        this.mRightFighterData = mRightFighterData;
    }

    public String getPlace() {
        return mPlace;
    }

    public String getOwner() {
        return mOwner;
    }

    public void add(FightActionData action) {
        mActionsList.add(action);
    }

    //TODO do common method instead of one parameter change

    public void refreshActionPhrase(String fightActionId, ActionUploadCallback callback, int phrase) {
//        DataManager.instance().getFightAction(fightActionId, new DataManager.RequestListener<GetFightActionResult>() {
//            @Override
//            public void onSuccess(GetFightActionResult result) {
//                FightAction action = result.action;
//                action.phrase = phrase;
//                DataManager.instance().saveFightAction(action, new DataManager.RequestListener<SaveFightActionResult>() {
//                    @Override
//                    public void onSuccess(SaveFightActionResult result) {
//                        if (callback != null) {
//                            callback.onRefresh(fightActionId);
//                        }
//                        Log.wtf("REFRESHED", fightActionId);
//                    }
//
//                    @Override
//                    public void onFailed(String error, String message) {
//                        Log.wtf("REFRESH", "FAIL " + error + "|" + message);
//                    }
//
//                    @Override
//                    public void onStateChanged(boolean inProgress) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//                Log.wtf("GET", "FAIL " + error + "|" + message);
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//
//            }
//        });
    }

    public int getActionsNumber() {
        return mActionsList.size();
    }

    public FightActionData getAction(int index) {
        return mActionsList.get(index);
    }

    public ArrayList<FightActionData> getActionsList() {
        return mActionsList;
    }


    public long getPureTime() {
        boolean inProgress = false;
        long startTime = getAction(0).getTime();
        long absoluteTime = 0;
        for (int i = 0; i < getActionsNumber(); ++i) {
            FightActionData action = getAction(i);
            if (action.getActionPeriod() != FightActionData.ActionPeriod.pause) {
                switch (action.getActionType()) {
                    case Start:
                        if (!inProgress) {
                            inProgress = true;
                            startTime = action.getTime();
                        }
                        break;

                    case Stop:
                        if (inProgress) {
                            inProgress = false;
                            absoluteTime += startTime - action.getTime();
                        }
                        break;
                }
            }
        }
        return absoluteTime;
    }

    public void removeAction(FightActionData fightActionData) {
        mActionsList.remove(fightActionData);
    }

    public long getmCurrentTime() {
        return mCurrentTime;
    }

    public void setmCurrentTime(long mCurrentTime) {
        this.mCurrentTime = mCurrentTime;
    }

    public int getmCurrentPeriod() {
        return mCurrentPeriod;
    }

    public void setmCurrentPeriod(int mCurrentPeriod) {
        this.mCurrentPeriod = mCurrentPeriod;
    }

    public int getmPriority() {
        return mPriority;
    }

    public void setmPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    public int getmVideoLeft() {
        return mVideoLeft;
    }

    public void setmVideoLeft(int mVideoLeft) {
        this.mVideoLeft = mVideoLeft;
    }

    public int getmVideoRight() {
        return mVideoRight;
    }

    public void setmVideoRight(int mVideoRight) {
        this.mVideoRight = mVideoRight;
    }
}
