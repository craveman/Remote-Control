package ru.inspirationpoint.remotecontrol.manager.dataEntities;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import java.io.Serializable;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.CardStatus.CardPStatus_None;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.CardStatus.CardStatus_None;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.CardStatus.CardStatus_Red;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.CardStatus.CardStatus_Yellow;


public class FighterData implements Cloneable, Serializable {
    private String mId = "";
    private String mName;
    private int mScore;
    private int yellowCardCount;
    private int redCardCount;
    private int yellowPCardCount;
    private int redPCardCount;
    private CommonConstants.CardStatus mCard = CardStatus_None;
    private CommonConstants.CardStatus mPCard = CardPStatus_None;

    public FighterData(String id, String name) {
        mId = id;
        mName = name;
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
    }

    public CommonConstants.CardStatus getCard() {
        return mCard;
    }

    public void setCard(CommonConstants.CardStatus card) {
        switch (card) {
            case CardStatus_Yellow:
                yellowCardCount ++;
                mCard = card;
                break;
            case CardStatus_Red:
                redCardCount ++;
                mCard = card;
                break;
            case CardStatus_None:
                mCard = card;
                redCardCount = 0;
                yellowCardCount = 0;
                break;
            case CardStatus_Black:
                mCard = card;
                break;
            case CardPStatus_Yellow:
                yellowPCardCount ++;
                mPCard = card;
                break;
            case CardPStatus_Red:
                redPCardCount ++;
                mPCard = card;
                break;
            case CardPStatus_None:
                mPCard = card;
                redPCardCount = 0;
                yellowPCardCount = 0;
                break;
            case CardPStatus_Black:
                mPCard = card;
                break;
        }
    }

    public int getYellowPCardCount() {
        return yellowPCardCount;
    }

    public void setYellowPCardCount(int yellowPCardCount) {
        this.yellowPCardCount = yellowPCardCount;
    }

    public int getRedPCardCount() {
        return redPCardCount;
    }

    public void setRedPCardCount(int redPCardCount) {
        this.redPCardCount = redPCardCount;
    }

    public CommonConstants.CardStatus getmCard() {
        return mCard;
    }

    public void setmCard(CommonConstants.CardStatus mCard) {
        this.mCard = mCard;
    }

    public CommonConstants.CardStatus getmPCard() {
        return mPCard;
    }

    public void setmPCard(CommonConstants.CardStatus mPCard) {
        this.mPCard = mPCard;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public int getYellowCardCount() {
        return yellowCardCount;
    }

    public void setYellowCardCount(int yellowCardCount) {
        this.yellowCardCount = yellowCardCount;
    }

    public int getRedCardCount() {
        return redCardCount;
    }

    public void setRedCardCount(int redCardCount) {
        this.redCardCount = redCardCount;
    }
}
