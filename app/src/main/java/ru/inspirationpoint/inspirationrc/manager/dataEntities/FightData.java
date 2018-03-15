package ru.inspirationpoint.inspirationrc.manager.dataEntities;

import android.content.Context;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.ui.activity.FightActivity;
import server.schemas.responses.FightAction;
import server.schemas.responses.FightOutput;

public class FightData implements Cloneable, Serializable {
    private String mId;
    private Date mDate;
    private FighterData mLeftFighterData;
    private FighterData mRightFighterData;
    private String mPlace;
    private String mOwner;
    private ArrayList<FightActionData> mActionsList = new ArrayList<>();
    private long mStartTime;
    private long mEndTime;

    public FightData(String id, Date date, FighterData leftFighterData, FighterData rightFighterData, String place, String owner) {
        mId = id;
        mDate = date;
        mLeftFighterData = leftFighterData;
        mRightFighterData = rightFighterData;
        mPlace = place;
        mOwner = owner;
    }

    private FightData(FightOutput fightOutput) {
        this(fightOutput._id, Helper.serverStringToDate(fightOutput.date), new FighterData(fightOutput.leftFighterId, fightOutput.leftFighterName),
                new FighterData(fightOutput.rightFighterId, fightOutput.rightFighterName), fightOutput.address, fightOutput.ownerName);
        for (FightAction fightAction : fightOutput.actions) {
            FightActionData fightActionData = new FightActionData(fightAction);
            addAction(fightActionData);
            switch (fightActionData.getActionType()) {
                case SetScoreLeft:
                    mLeftFighterData.setScore(fightActionData.getScore());
                    break;
                case SetScoreRight:
                    mRightFighterData.setScore(fightActionData.getScore());
                    break;
                case YellowCardLeft:
                    mLeftFighterData.setCard(FightActivity.CardStatus.CardStatus_Yellow);
                    break;
                case YellowCardRight:
                    mRightFighterData.setCard(FightActivity.CardStatus.CardStatus_Yellow);
                    break;
            }
        }
        this.mStartTime = fightOutput.startTime;
        this.mEndTime = fightOutput.endTime;
        mLeftFighterData.applyScoreChanges();
        mRightFighterData.applyScoreChanges();
    }

    @Override
    public FightData clone() {
        FightData newFightData = new FightData(mId, mDate, mLeftFighterData.clone(), mRightFighterData.clone(), mPlace, mOwner);
        for (FightActionData actionData : mActionsList) {
            newFightData.addAction(actionData.clone());
        }
        return newFightData;
    }

    public static ArrayList<FightData> getFightData(FightOutput[] fightOutputArray) {
        ArrayList<FightData> fightArray = new ArrayList<>();
        for (FightOutput fight : fightOutputArray) {
            FightData fightData = new FightData(fight);
            fightArray.add(fightData);
        }
        return fightArray;
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

    public void addAction(FightActionData action) {
        mActionsList.add(action);
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
            if (action.getActionPeriod() != FightActionData.ActionPeriod.Pause) {
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

}
