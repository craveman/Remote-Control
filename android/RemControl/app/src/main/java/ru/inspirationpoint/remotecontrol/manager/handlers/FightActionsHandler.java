package ru.inspirationpoint.remotecontrol.manager.handlers;

import android.util.Log;

import java.util.Set;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.manager.ActionUploadCallback;
import ru.inspirationpoint.remotecontrol.manager.Camera;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;

public class FightActionsHandler {

    private FightData fightData;

    public FightActionsHandler(FightData fightData) {
        this.fightData = fightData;
    }

    public void addAction(FightActionData action, ActionUploadCallback callback, Set<String> videoUrls) {
        if (videoUrls != null) {
            for (String url : videoUrls) {
                String[] splitted = url.split("_");
                if (splitted[1].equals(String.valueOf(action.getTime())) &&
                        splitted[2].split("\\.")[0].equals(String.valueOf(action.getFightPeriod()))) {
                    action.setVideoUrl(url);
                }

            }
        }
        fightData.add(action);
        switch (action.getActionType()) {
            case SetTime:
                fightData.setmCurrentTime(action.getTime());
                break;
            case SetPeriod:
                fightData.setmCurrentPeriod(action.getFightPeriod());
                break;
            case RedCardLeft:
                fightData.getLeftFighter().setCard(CommonConstants.CardStatus.CardStatus_Red);
                break;
            case RedCardRight:
                fightData.getRightFighter().setCard(CommonConstants.CardStatus.CardStatus_Red);
                break;
            case SetScoreLeft:
                fightData.getLeftFighter().setScore(action.getScore());
                break;
            case SetScoreRight:
                fightData.getRightFighter().setScore(action.getScore());
                break;
            case YellowCardLeft:
                fightData.getLeftFighter().setCard(CommonConstants.CardStatus.CardStatus_Yellow);
                break;
            case YellowCardRight:
                fightData.getRightFighter().setCard(CommonConstants.CardStatus.CardStatus_Yellow);
                break;
        }
//        DataManager.instance().saveFightAction(action.getFightAction(), new DataManager.RequestListener<SaveFightActionResult>() {
//            @Override
//            public void onSuccess(SaveFightActionResult result) {
//                if (callback != null) {
//                    callback.onUpload(result.fight.actions[result.fight.actions.length - 1]);
//                }
//            }
//
//            @Override
//            public void onFailed(String error, String message) {
//            }
//
//            @Override
//            public void onStateChanged(boolean inProgress) {
//
//            }
//        });
    }

    public String refreshAction(String fightActionId, ActionUploadCallback callback, String videoUrl) {
//            DataManager.instance().getFightAction(fightActionId, new DataManager.RequestListener<GetFightActionResult>() {
//                @Override
//                public void onSuccess(GetFightActionResult result) {
//                    FightAction action = result.action;
//                    action.videoUrl = videoUrl;
//                    Log.wtf("REFRESHING", action._id + "|" + action.videoUrl);
//                        DataManager.instance().saveFightAction(action, new DataManager.RequestListener<SaveFightActionResult>() {
//                            @Override
//                            public void onSuccess(SaveFightActionResult result) {
//                                callback.onRefresh(fightActionId);
//                                Log.wtf("REFRESHED", fightActionId);
//                            }
//
//                            @Override
//                            public void onFailed(String error, String message) {
//                                Log.wtf("REFRESH", "FAIL " + error + "|" + message);
//                            }
//
//                            @Override
//                            public void onStateChanged(boolean inProgress) {
//
//                            }
//                        });
//                }
//
//                @Override
//                public void onFailed(String error, String message) {
//                    Log.wtf("GET", "FAIL " + error + "|" + message);
//                }
//
//                @Override
//                public void onStateChanged(boolean inProgress) {
//
//                }
//            });
        return fightActionId;
    }

    public FightData getFightData() {
        return fightData;
    }
}
