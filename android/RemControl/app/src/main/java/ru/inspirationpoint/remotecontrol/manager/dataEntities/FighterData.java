package ru.inspirationpoint.remotecontrol.manager.dataEntities;

import java.io.Serializable;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.CardStatus.CardStatus_None;


public class FighterData implements Cloneable, Serializable {
    private String mId = "";
    private String mName = "";
    private int mScore = 0;
    private boolean mIsScoreIncreased = false;
    private boolean mIsScoreSet = false;
    private CommonConstants.CardStatus mCard = CardStatus_None;

    public FighterData(String id, String name) {
        mId = id;
        mName = name;
    }

    @Override
    public FighterData clone() {
        FighterData newFighterData = new FighterData(mId, mName);
        newFighterData.mScore = mScore;
        newFighterData.mIsScoreIncreased = mIsScoreIncreased;
        newFighterData.mIsScoreSet = mIsScoreSet;
        newFighterData.mCard = mCard;
        return newFighterData;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
        mIsScoreSet = true;
    }

    public void toggleScore(boolean isLeft) {
        mIsScoreIncreased = !mIsScoreIncreased;
        mScore += mIsScoreIncreased ? 1 : -1;
        if (InspirationDayApplication.getApplication().getHelper() != null) {
            tcpScore(isLeft);
        }
    }

    public void tcpScore(boolean isLeft) {
        InspirationDayApplication.getApplication().getHelper().send(CommandHelper.setScore(isLeft ?
                CommandsContract.PERSON_TYPE_LEFT : CommandsContract.PERSON_TYPE_RIGHT, mScore));
    }

    public void increaseScore(boolean isLeft) {
        mIsScoreIncreased = true;
        mIsScoreSet = true;
        mScore += 1;
        if (InspirationDayApplication.getApplication().getHelper() != null) {
            tcpScore(isLeft);
        }
    }

    public void decreaseScore(boolean isLeft) {
        mIsScoreSet = !mIsScoreSet;
        mIsScoreIncreased = false;
        mScore -= 1;
        if (InspirationDayApplication.getApplication().getHelper() != null) {
            tcpScore(isLeft);
        }
    }

    public boolean isScoreIncreased() {
        return mIsScoreIncreased;
    }

    public boolean isScoreChanged() {
        return mIsScoreIncreased || mIsScoreSet;
    }

    public void applyScoreChanges() {
        if (mIsScoreIncreased) {
            mIsScoreIncreased = false;
        }
        mIsScoreSet = false;
    }

    public CommonConstants.CardStatus getCard() {
        return mCard;
    }

    public void setCard(CommonConstants.CardStatus card) {
        mCard = card;
    }

    public void setName(String name) {
        mName = name;
    }
}
