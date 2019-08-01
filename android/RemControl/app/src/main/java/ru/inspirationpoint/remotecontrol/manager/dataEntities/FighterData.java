package ru.inspirationpoint.remotecontrol.manager.dataEntities;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import java.io.Serializable;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.CardStatus.CardStatus_None;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.CardStatus.CardStatus_Red;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.CardStatus.CardStatus_Yellow;


public class FighterData implements Cloneable, Serializable {
    private String mId = "";
    private String mName;
    private int mScore;
    private int yellowCardCount;
    private int redCardCount;
    private CommonConstants.CardStatus mCard = CardStatus_None;

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
        mCard = card;
        if (card == CardStatus_Yellow) {
            yellowCardCount ++;
        } else if (card == CardStatus_Red) {
            redCardCount ++;
        }
    }

    public void setName(String name) {
        mName = name;
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
