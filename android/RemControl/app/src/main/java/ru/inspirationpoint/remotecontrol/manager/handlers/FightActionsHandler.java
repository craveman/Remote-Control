package ru.inspirationpoint.remotecontrol.manager.handlers;

import android.util.Log;

import java.util.Set;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.requests.FightAction;
import ru.inspirationpoint.remotecontrol.manager.ActionUploadCallback;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;

import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_LEFT;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_RIGHT;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.PERSON_TYPE_NONE;

public class FightActionsHandler {

    private final FightData fightData;

    public FightActionsHandler(FightData fightData) {
        this.fightData = fightData;
    }

    public void updateTime(int time) {
        fightData.setmCurrentTime(time);
    }

    public void setLeftName(String name) {
        fightData.getLeftFighter().setName(name);
    }

    public void setRightName(String name) {
        fightData.getRightFighter().setName(name);
    }

    public void setLeftId(String id) {
        fightData.getLeftFighter().setmId(id);
    }

    public void setRightId(String id) {
        fightData.getRightFighter().setmId(id);
    }

    public void setPriorityNone() {
        fightData.setmPriority(PERSON_TYPE_NONE);
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
            case NoneCardLeft:
                fightData.getLeftFighter().setCard(CommonConstants.CardStatus.CardStatus_None);
                break;
            case NoneCardRight:
                fightData.getRightFighter().setCard(CommonConstants.CardStatus.CardStatus_None);
                break;
            case NonePCardLeft:
                fightData.getLeftFighter().setCard(CommonConstants.CardStatus.CardPStatus_None);
                break;
            case NonePCardRight:
                fightData.getRightFighter().setCard(CommonConstants.CardStatus.CardPStatus_None);
                break;
            case RedCardLeft:
                fightData.getLeftFighter().setCard(CommonConstants.CardStatus.CardStatus_Red);
                break;
            case RedCardRight:
                fightData.getRightFighter().setCard(CommonConstants.CardStatus.CardStatus_Red);
                break;
            case BlackCardLeft:
                fightData.getLeftFighter().setCard(CommonConstants.CardStatus.CardStatus_Black);
                break;
            case BlackCardRight:
                fightData.getRightFighter().setCard(CommonConstants.CardStatus.CardStatus_Black);
                break;
            case PCardYellowLeft:
                fightData.getLeftFighter().setCard(CommonConstants.CardStatus.CardPStatus_Yellow);
                break;
            case PCardYellowRight:
                fightData.getRightFighter().setCard(CommonConstants.CardStatus.CardPStatus_Yellow);
                break;
            case PCardRedLeft:
                fightData.getLeftFighter().setCard(CommonConstants.CardStatus.CardPStatus_Red);
                break;
            case PCardRedRight:
                fightData.getRightFighter().setCard(CommonConstants.CardStatus.CardPStatus_Red);
                break;
            case PCardBlackLeft:
                fightData.getLeftFighter().setCard(CommonConstants.CardStatus.CardPStatus_Black);
                break;
            case PCardBlackRight:
                fightData.getRightFighter().setCard(CommonConstants.CardStatus.CardPStatus_Yellow);
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
            case VideoLeft:
                fightData.setmVideoLeft(fightData.getmVideoLeft() > 0 ? fightData.getmVideoLeft() - 1 : 0);
                break;
            case VideoRight:
                fightData.setmVideoRight(fightData.getmVideoRight() > 0 ? fightData.getmVideoRight() - 1 : 0);
                break;
            case SetPriorityLeft:
                fightData.setmPriority(PERSON_TYPE_LEFT);
                break;
            case SetPriorityRight:
                fightData.setmPriority(PERSON_TYPE_RIGHT);
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

    public void setVideo(long definer, String video) {
        for (FightActionData action : fightData.getActionsList()) {
            if (action.getSystemTime() == definer) {
                action.setVideoPreviewUrl(video);
            }
        }
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
