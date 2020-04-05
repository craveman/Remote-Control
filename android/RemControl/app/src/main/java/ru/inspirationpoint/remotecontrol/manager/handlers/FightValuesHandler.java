package ru.inspirationpoint.remotecontrol.manager.handlers;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Date;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.requests.FightAction;
import ru.inspirationpoint.remotecontrol.manager.ActionUploadCallback;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FighterData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData.ActionType;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;

import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_LEFT;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_REFEREE;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_RIGHT;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.TIMER_MODE_MAIN;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.TIMER_MODE_PAUSE;

public class FightValuesHandler implements ActionUploadCallback {

    private FightActionsHandler handler;
    private CoreHandler core;

    public FightValuesHandler(FightData fightData, CoreHandler core) {
        FightData fightData1;
        if (fightData == null) {
            fightData1 = new FightData("", new Date(), new FighterData("", ""),
                    new FighterData("", ""), "",
                    SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, ""));
        } else {
            fightData1 = fightData;
        }
        this.core = core;
        handler = new FightActionsHandler(fightData1);
    }

    public void setFightData(FightData data) {
        handler = new FightActionsHandler(data);
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    public FightData getFightData() {
        return handler.getFightData();
    }

    public void resetFightData() {
        handler = new FightActionsHandler(new FightData("", new Date(), new FighterData("", ""),
                new FighterData("", ""), "",
                SettingsManager.getValue(CommonConstants.LAST_USER_NAME_FIELD, "")));
        handler.updateTime(180000);
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    public void updateTime(int time) {
        handler.updateTime(time);
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    public void setName(int person, String name) {
        if (person == PERSON_TYPE_LEFT) {
            handler.setLeftName(name);
        } else if (person == PERSON_TYPE_RIGHT) {
            handler.setRightName(name);
        }
        core.sendToSM(CommandHelper.setName(person, name));
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    public void setId(int person, String id) {
        if (!id.isEmpty()) {
            if (person == PERSON_TYPE_LEFT) {
                handler.setLeftId(id);
            } else if (person == PERSON_TYPE_RIGHT) {
                handler.setRightId(id);
            }
            core.sendToSM(CommandHelper.setId(person, id));
            core.getBackupHelper().backupFight(handler.getFightData());
        }
    }

    public void setScore(long time, int person, int score) {
        core.sendToSM(CommandHelper.setScore(person, score));
        if (person == PERSON_TYPE_LEFT) {
            handler.addAction(FightActionData.createSetScoreLeft(time,
                    handler.getFightData().getmCurrentPeriod(), score, 14), this, null);
        } else {
            handler.addAction(FightActionData.createSetScoreRight(time,
                    handler.getFightData().getmCurrentPeriod(), score, 14), this, null);
        }
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    public void setPriority(long time, int person) {
        core.sendToSM(CommandHelper.setPriority(person));
        if (person == PERSON_TYPE_LEFT) {
            handler.addAction(FightActionData.createSetPriorityLeft(time,
                    handler.getFightData().getmCurrentPeriod()), this, null);
        } else if (person == PERSON_TYPE_RIGHT){
            handler.addAction(FightActionData.createSetPriorityRight(time,
                    handler.getFightData().getmCurrentPeriod()), this, null);
        } else {
            handler.setPriorityNone();
        }
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    public void setCard(long time, int person, int card) {
        core.sendToSM(CommandHelper.setCard(person, card - 19));
        if (person == PERSON_TYPE_LEFT) {
            handler.addAction(FightActionData.createSetCardLeft(time,
                        handler.getFightData().getmCurrentPeriod(), card - 19), this, null);
        } else {
            handler.addAction(FightActionData.createSetCardRight(time,
                        handler.getFightData().getmCurrentPeriod(), card - 19), this, null);
        }
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    public void setPCard(long time, int person, int card) {
        core.sendToSM(CommandHelper.setCard(person, card-15));
        if (person == PERSON_TYPE_LEFT) {
            handler.addAction(FightActionData.createSetPCardLeft(time,
                        handler.getFightData().getmCurrentPeriod(), card - 15), this, null);
        } else {
                handler.addAction(FightActionData.createSetPCardRight(time,
                        handler.getFightData().getmCurrentPeriod(), card - 15), this, null);
        }
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    public void setTime(long time, long estTime, int mode) {
        core.sendToSM(CommandHelper.setTimer(estTime, mode - 80));
        if (mode == TIMER_MODE_MAIN) {
            handler.addAction(FightActionData.createSetTime(time,
                    handler.getFightData().getmCurrentPeriod(), estTime), this, null);
        } else if (mode == TIMER_MODE_PAUSE) {
            handler.addAction(FightActionData.createSetPause(time,
                    handler.getFightData().getmCurrentPeriod()), this, null);
        } else {
            handler.addAction(FightActionData.createSetMedical(time,
                    handler.getFightData().getmCurrentPeriod()), this, null);
        }
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    public void setPeriod (long time, int period, int defTime) {
        core.sendToSM(CommandHelper.setPeriod(period));
//        core.sendToSM(CommandHelper.setTimer(defTime, 0));
        handler.addAction(FightActionData.createSetPeriod(time, period), this, null);
        core.getBackupHelper().backupFight(handler.getFightData());
    }

    @Override
    public void onUpload(FightAction action) {

    }

    @Override
    public void onRefresh(String id) {

    }
}
