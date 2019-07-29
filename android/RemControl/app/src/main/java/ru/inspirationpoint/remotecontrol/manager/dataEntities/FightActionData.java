package ru.inspirationpoint.remotecontrol.manager.dataEntities;

import java.io.Serializable;

import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class FightActionData implements Cloneable, Serializable {
    private static final String FIGHTER_NONE = "none";
    private static final String FIGHTER_LEFT = "left";
    private static final String FIGHTER_RIGHT = "right";

    private static final String ACTION_TYPE_START = "start";
    private static final String ACTION_TYPE_STOP = "stop";
    private static final String ACTION_TYPE_SET_SCORE_LEFT = "setScoreLeft";
    private static final String ACTION_TYPE_SET_SCORE_RIGHT = "setScoreRight";
    private static final String ACTION_TYPE_YC_LEFT = "yellowCardLeft";
    private static final String ACTION_TYPE_YC_RIGHT = "yellowCardRight";
    private static final String ACTION_TYPE_RC_LEFT = "redCardLeft";
    private static final String ACTION_TYPE_RC_RIGHT = "redCardRight";
    private static final String ACTION_TYPE_SET_PRIORITY_LEFT = "setPriorityLeft";
    private static final String ACTION_TYPE_SET_PRIORITY_RIGHT = "setPriorityRight";
    private static final String ACTION_TYPE_SET_PERIOD = "setPeriod";
    private static final String ACTION_TYPE_SET_PAUSE = "setPause";
    private static final String ACTION_TYPE_SET_TIME = "setTime";
    private static final String ACTION_TYPE_RESET = "reset";

    private static final String ACTION_PERIOD_FIGHT = "fight";
    private static final String ACTION_PERIOD_PAUSE = "pause";
    private static final String ACTION_PERIOD_EXTRA = "extra";
    private static final String ACTION_PERIOD_OTHER = "other";


    private String _id = "";
    private ActionPeriod mActionPeriod = ActionPeriod.pause;
    private ActionType mActionType = ActionType.Start;
    private Fighter mFighter = Fighter.None;
    private int mScore;
    private int fightPeriod;
    private long establishedTime;
    private long mTime;
    private long systemTime;
    private String comment;
    private int phrase;
    private String videoUrl = "";
    private String videoPreviewUrl = "";
    private String fightId;

    private FightActionData() {

    }
//
//    public FightActionData(FightAction action) {
//        this();
//        setActionPeriod(action.period);
//        setActionType(action.type);
//        setFighter(action.fighter);
//        mScore = action.score;
//        fightPeriod = action.fightPeriod;
//        mTime = action.timestamp;
//        if (action.comment != null) {
//            if (!action.comment.equals("")) {
//                try {
//                    systemTime = Long.valueOf(action.comment);
//                } catch (NumberFormatException e) {
//                    comment = action.comment;
//                }
//            }
//        }
//        establishedTime = action.establishedTime;
//        phrase = action.phrase;
//        videoUrl = action.videoUrl;
//        videoPreviewUrl = action.videoPreviewUrl;
//        fightId = action.fightId;
//        _id = action._id;
//    }
//
//    public server.schemas.requests.FightAction getFightAction() {
//        return new server.schemas.requests.FightAction("", getStringActionType(), fightId,
//                mTime, getStringFighter(), getStringActionPeriod(), mScore, fightPeriod, establishedTime, phrase,
//                String.valueOf(systemTime), videoUrl, videoPreviewUrl);
//    }

    @Override
    public FightActionData clone() {
        FightActionData newFightActionData = new FightActionData();
        newFightActionData.mActionPeriod = mActionPeriod;
        newFightActionData.fightId = fightId;
        newFightActionData.mActionType = mActionType;
        newFightActionData.mFighter = mFighter;
        newFightActionData.mScore = mScore;
        newFightActionData.mTime = mTime;
        newFightActionData.systemTime = systemTime;
        newFightActionData.videoUrl = videoUrl;
        newFightActionData.videoPreviewUrl = videoPreviewUrl;
        return newFightActionData;
    }

    public static FightActionData createStartRound(long time, int period, ActionPeriod actionPeriod, long systemTime, String comment) {
        FightActionData action = new FightActionData();
        action.mActionPeriod = actionPeriod;
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = ActionType.Start;
        action.mTime = time;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        action.comment = comment;
        return action;
    }

    public static FightActionData createStopRound(long time, int period, ActionPeriod actionPeriod, long systemTime) {
        FightActionData action = new FightActionData();
        action.mActionPeriod = actionPeriod;
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = ActionType.Stop;
        action.mTime = time;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        return action;
    }

    public static FightActionData createSetScoreLeft(long time, int period, Fighter fighter, int score,
                                                     long systemTime, int phrase) {
        FightActionData action = new FightActionData();
        action.mActionType = ActionType.SetScoreLeft;
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mFighter = fighter;
        action.mScore = score;
        action.mTime = time;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        action.phrase = phrase;
        return action;
    }

    public static FightActionData createSetScoreRight(long time, int period, Fighter fighter, int score,
                                                      long systemTime, int phrase) {
        FightActionData action = new FightActionData();
        action.mActionType = ActionType.SetScoreRight;
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mFighter = fighter;
        action.mScore = score;
        action.mTime = time;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        action.phrase = phrase;
        return action;
    }

    public static FightActionData createSetPriorityLeft(long time, int period, Fighter fighter,
                                                        long systemTime) {
        FightActionData action = new FightActionData();
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = ActionType.SetPriorityLeft;
        action.mFighter = fighter;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        action.mTime = time;
        return action;
    }

    public static FightActionData createSetPriorityRight(long time, int period, Fighter fighter,
                                                         long systemTime) {
        FightActionData action = new FightActionData();
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = ActionType.SetPriorityRight;
        action.mFighter = fighter;
        action.mTime = time;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        return action;
    }

    public static FightActionData createSetCardLeft(long time, int period, Fighter fighter, int score,
                                                    boolean isYellow, long systemTime) {
        FightActionData action = new FightActionData();
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = isYellow ? ActionType.YellowCardLeft : ActionType.RedCardLeft;
        action.mFighter = fighter;
        action.mTime = time;
        action.mScore = score;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        return action;
    }

    public static FightActionData createSetCardRight(long time, int period, Fighter fighter, int score,
                                                     boolean isYellow, long systemTime) {
        FightActionData action = new FightActionData();
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = isYellow ? ActionType.YellowCardRight : ActionType.RedCardRight;
        action.mFighter = fighter;
        action.mTime = time;
        action.mScore = score;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        return action;
    }

    public static FightActionData createSetTime(long time, int period, long estTime, long systemTime) {
        FightActionData action = new FightActionData();
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = ActionType.SetTime;
        action.mTime = time;
        action.fightPeriod = period;
        action.establishedTime = estTime;
        action.systemTime = systemTime;
        return action;
    }

    public static FightActionData createSetPeriod(long time, int period, long systemTime) {
        FightActionData action = new FightActionData();
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = ActionType.SetPeriod;
        action.mTime = time;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        return action;
    }

    public static FightActionData createSetPause(long time, int period, long systemTime) {
        FightActionData action = new FightActionData();
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = ActionType.SetPause;
        action.mTime = time;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        return action;
    }

    public static FightActionData createComment(long time, int period, String comment) {
        FightActionData action = new FightActionData();
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
//        action.mActionType = ActionType.Comment;
        action.mTime = time;
        action.fightPeriod = period;
//        action.systemTime = comment;
        return action;
    }

    public static FightActionData createResetAction(long time, int period, long systemTime) {
        FightActionData action = new FightActionData();
        action.fightId = SettingsManager.getValue(CommonConstants.LAST_FIGHT_ID, "");
        action.mActionType = ActionType.Reset;
        action.mTime = time;
        action.fightPeriod = period;
        action.systemTime = systemTime;
        return action;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static String getFighterNone() {
        return FIGHTER_NONE;
    }

    public int getFightPeriod() {
        return fightPeriod;
    }

    public void setFightPeriod(int fightPeriod) {
        this.fightPeriod = fightPeriod;
    }

    public long getEstablishedTime() {
        return establishedTime;
    }

    public void setEstablishedTime(long establishedTime) {
        this.establishedTime = establishedTime;
    }

    public ActionType getActionType() {
        return mActionType;
    }

    public ActionPeriod getActionPeriod() {
        return mActionPeriod;
    }

    public Fighter getFighter() {
        return mFighter;
    }

    public int getScore() {
        return mScore;
    }

    public long getTime() {
        return mTime;
    }

    public String getComment() {
        return comment;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
    }

    public int getPhrase() {
        return phrase;
    }

    public void setPhrase(int phrase) {
        this.phrase = phrase;
    }


    public void setmScore(int mScore) {
        this.mScore = mScore;
    }

    public static String getFighterLeft() {
        return FIGHTER_LEFT;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoPreviewUrl() {
        return videoPreviewUrl;
    }

    public void setVideoPreviewUrl(String videoPreviewUrl) {
        this.videoPreviewUrl = videoPreviewUrl;
    }

    public String getFightId() {
        return fightId;
    }

    public void setFightId(String fightId) {
        this.fightId = fightId;
    }

    public String getStringFighter() {
        switch (mFighter) {
            case Left:
                return FIGHTER_LEFT;

            case Right:
                return FIGHTER_RIGHT;
        }
        return FIGHTER_NONE;
    }

    public void setFighter(String fighter) {
        if (fighter.equalsIgnoreCase(FIGHTER_LEFT)) {
            mFighter = Fighter.Left;
        } else if (fighter.equalsIgnoreCase(FIGHTER_RIGHT)) {
            mFighter = Fighter.Right;
        } else {
            mFighter = Fighter.None;
        }
    }

    public String getStringActionType() {
        switch (mActionType) {
            case Start:
                return ACTION_TYPE_START;

            case Stop:
                return ACTION_TYPE_STOP;

            case SetScoreLeft:
                return ACTION_TYPE_SET_SCORE_LEFT;

            case SetScoreRight:
                return ACTION_TYPE_SET_SCORE_RIGHT;

            case YellowCardLeft:
                return ACTION_TYPE_YC_LEFT;

            case YellowCardRight:
                return ACTION_TYPE_YC_RIGHT;

            case RedCardLeft:
                return ACTION_TYPE_RC_LEFT;

            case RedCardRight:
                return ACTION_TYPE_RC_RIGHT;

            case SetPriorityLeft:
                return ACTION_TYPE_SET_PRIORITY_LEFT;

            case SetPriorityRight:
                return ACTION_TYPE_SET_PRIORITY_RIGHT;

            case SetPeriod:
                return ACTION_TYPE_SET_PERIOD;

            case SetPause:
                return ACTION_TYPE_SET_PAUSE;

            case SetTime:
                return ACTION_TYPE_SET_TIME;

            case Reset:
                return ACTION_TYPE_RESET;
        }
        return "";
    }

    public void setActionType(String actionType) {
        if (actionType.equalsIgnoreCase(ACTION_TYPE_START)) {
            mActionType = ActionType.Start;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_STOP)) {
            mActionType = ActionType.Stop;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_SET_SCORE_LEFT)) {
            mActionType = ActionType.SetScoreLeft;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_SET_SCORE_RIGHT)) {
            mActionType = ActionType.SetScoreRight;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_YC_LEFT)) {
            mActionType = ActionType.YellowCardLeft;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_YC_RIGHT)) {
            mActionType = ActionType.YellowCardRight;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_RC_LEFT)) {
            mActionType = ActionType.RedCardLeft;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_RC_RIGHT)) {
            mActionType = ActionType.RedCardRight;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_SET_PRIORITY_LEFT)) {
            mActionType = ActionType.SetPriorityLeft;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_SET_PRIORITY_RIGHT)) {
            mActionType = ActionType.SetPriorityRight;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_SET_PERIOD)) {
            mActionType = ActionType.SetPeriod;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_SET_PAUSE)) {
            mActionType = ActionType.SetPause;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_SET_TIME)) {
            mActionType = ActionType.SetTime;
        } else if (actionType.equalsIgnoreCase(ACTION_TYPE_RESET)) {
            mActionType = ActionType.Reset;
        }
    }

    public String getStringActionPeriod() {
        switch (mActionPeriod) {
            case fight:
                return ACTION_PERIOD_FIGHT;

            case pause:
                return ACTION_PERIOD_PAUSE;

            case extra:
                return ACTION_PERIOD_EXTRA;
        }
        return ACTION_PERIOD_OTHER;
    }

    public void setActionPeriod(String actionPeriod) {
        if (actionPeriod.equalsIgnoreCase(ACTION_PERIOD_FIGHT)) {
            mActionPeriod = ActionPeriod.fight;
        } else if (actionPeriod.equalsIgnoreCase(ACTION_PERIOD_PAUSE)) {
            mActionPeriod = ActionPeriod.pause;
        } else if (actionPeriod.equalsIgnoreCase(ACTION_PERIOD_EXTRA)) {
            mActionPeriod = ActionPeriod.extra;
        } else {
            mActionPeriod = ActionPeriod.other;
        }
    }

    public enum Fighter {Left, Right, None}

    public enum ActionType {Start, Stop, SetScoreLeft, SetScoreRight, YellowCardLeft, YellowCardRight,
            RedCardLeft, RedCardRight, SetPriorityLeft, SetPriorityRight, SetPeriod, SetPause,
            SetTime, Reset}

    public enum ActionPeriod {fight, pause, extra, other, medical}
}
