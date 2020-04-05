package ru.inspirationpoint.remotecontrol.manager.constants;

public interface ISEMIContract {

    enum ETH_FIELDS_MAIN {
        PROTOCOL,
        COMMAND,
        PISTE,
        COMPETITION,
        PHASE,
        POUL_TAB,
        MATCH,
        ROUND,
        TIME,
        STOPWATCH,
        TYPE,
        WEAPON,
        PRIORITY,
        STATE,
        REF_ID,
        REF_NAME,
        REF_NATION
    }

    enum ETH_FIELDS_FIGHTER {
        ID,
        NAME,
        NATION,
        SCORE,
        STATUS,
        YELLOW_CARD,
        RED_CARD,
        LIGHT,
        WHITE_LIGHT,
        MEDICAL,
        RESERVE
    }

    String ETH_PROTOCOL_NAME = "EFP1";

    String ETH_HELLO_CMD = "HELLO";
    String ETH_DISP_CMD = "DISP";
    String ETH_ACK_CMD = "ACK";
    String ETH_NAK_CMD = "NAK";
    String ETH_INFO_CMD = "INFO";
    String ETH_NEXT_CMD = "NEXT";
    String ETH_PREV_CMD = "PREV";

    String ETH_COMPET_IND = "I";
    String ETH_COMPET_TEAM = "T";

    String ETH_WEAPON_FOIL = "F";
    String ETH_WEAPON_EPEE = "E";
    String ETH_WEAPON_SABER = "S";

    String ETH_PRIORITY_NONE = "N";
    String ETH_PRIORITY_LEFT = "L";
    String ETH_PRIORITY_RIGHT = "R";

    String ETH_SM_STATE_WAITING = "W";
    String ETH_SM_STATE_FENCING = "F";
    String ETH_SM_STATE_HALT = "H";
    String ETH_SM_STATE_PAUSE = "P";
    String ETH_SM_STATE_ENDING = "E";

    enum ETH_INT_STATES {ETH_SM_STATE_WAITING, ETH_SM_STATE_FENCING, ETH_SM_STATE_HALT, ETH_SM_STATE_PAUSE, ETH_SM_STATE_ENDING}

    String ETH_FENCER_STATUS_UNDEFINED = "U";
    String ETH_FENCER_STATUS_VICTORY = "V";
    String ETH_FENCER_STATUS_DEFEAT = "D";
    String ETH_FENCER_STATUS_ABANDON = "A";
    String ETH_FENCER_STATUS_EXCLUDED = "E";

    String ETH_RESERVE_NONE = "N";
    String ETH_RESERVE_THIS = "R";
}
