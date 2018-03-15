package ru.inspirationpoint.inspirationrc.manager.dataEntities;

import java.io.Serializable;

import ru.inspirationpoint.inspirationrc.ui.activity.FightActivity;

import static ru.inspirationpoint.inspirationrc.ui.activity.FightActivity.CardStatus.CardStatus_None;


public class FighterData implements Cloneable, Serializable {
    private String mId = "";
    private String mName = "";
    private int mScore = 0;
    private boolean mIsScoreIncreased = false;
    private boolean mIsScoreSet = false;
    private FightActivity.CardStatus mCard = CardStatus_None;

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

    public void toggleScore() {
        mIsScoreIncreased = !mIsScoreIncreased;
        mScore += mIsScoreIncreased ? 1 : -1;
    }

    public void increaseScore() {
        mIsScoreIncreased = true;
        mScore += 1;
    }

    public void decreaseScore() {
        mIsScoreIncreased = false;
        mScore -= 1;
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

    public FightActivity.CardStatus getCard() {
        return mCard;
    }

    public void setCard(FightActivity.CardStatus card) {
        mCard = card;
    }
}
