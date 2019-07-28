package ru.inspirationpoint.inspirationrc.manager.constants.commands;

public interface CommandsContract {

    int HEADER_LENGTH = 6;
    byte PROTOCOL_VERSION = (byte)0x01;

    byte PING_TCP_CMD = (byte)0x01;
    byte SETNAME_TCP_CMD = (byte) 0x02;
    byte SETSCORE_TCP_CMD = (byte) 0x03;
    byte SETCARD_TCP_CMD = (byte) 0x04;
    byte SETPRIORITY_TCP_CMD = (byte) 0x05;
    byte SETPERIOD_TCP_CMD = (byte) 0x06;
    byte SETWEAPON_TCP_CMD = (byte) 0x07;
    byte SETTIMER_TCP_CMD = (byte) 0x08;
    byte STARTTIMER_TCP_CMD = (byte) 0x09;
    byte BROADCAST_TCP_CMD = (byte) 0x0B;
    byte SWAP_FIGHTERS_TCP_CMD = (byte) 0x0A;
    byte LOAD_FILE_TCP_CMD = (byte) 0x0C;
    byte PLAYER_TCP_CMD = (byte) 0x0D;
    byte HELLO_TCP_CMD = (byte) 0x0E;
    byte DISCONNECT_TCP_CMD = (byte) 0x0F;
    byte ETH_SERVER_START = (byte) 0x10;
    byte ETH_SERVER_STOP = (byte) 0x11;
    byte PASSIVE_MAX = (byte) 0x12;
    byte DISP_RECEIVE_CMD = (byte) 0x13;
    byte ETH_APPLY_FIGHT = (byte) 0x14;
    byte ETH_NEXT = (byte) 0x15;
    byte ETH_PREV = (byte) 0x16;
    byte SEMI_EXIT = (byte) 0x17;
    byte FINISH_FIGHT = (byte) 0x18;
    byte VISIBILITY_OPTIONS_CMD = (byte) 0x19;
    byte ETH_ACK = (byte) 0x1A;
    byte ETH_NAK = (byte) 0x1B;
    byte FINISH_FIGHT_ASK = (byte) 0x1C;
    byte VIDEO_COUNTER_CMD = (byte) 0x1D;
    byte PASSIVE_LOCK_CHANGE = (byte) 0x1E;
    byte PASSIVE_SHOW = (byte) 0x1F;
    byte ETH_SEMI_ACTIVE = (byte) 0x20;
    byte SET_DEF_TIMER_CMD = (byte) 0x21;
    byte PAUSE_FINISHED = (byte) 0x22;
    byte COMPETITION_SET = (byte) 0x23;
    byte RESTORE_FOR_ETH = (byte) 0x24;//
    byte SET_DEF_PASSIVE = (byte) 0x25;
    byte RESET_TCP_CMD = (byte) 0x26;
    byte FLAG_TCP_CMD = (byte) 0x27;

    int PERSON_TYPE_LEFT = 1;
    int PERSON_TYPE_RIGHT = 2;
    int PERSON_TYPE_REFEREE = 3;

    int WEAPON_TYPE_FOIL = 1;
    int WEAPON_TYPE_EPEE = 2;
    int WEAPON_TYPE_SABER = 3;
    int WEAPON_TYPE_OFF = 0;

    int PLAYER_STOP = 0;
    int PLAYER_START = 1;
    int PLAYER_PAUSE = 2;
    int PLAYER_INIT = 3;

    int DEV_TYPE_SM = 1;
    int DEV_TYPE_RC = 2;
    int DEV_TYPE_CAM = 3;
    int DEV_TYPE_REP = 4;
    int DEV_TYPE_CP = 5;

    byte CARD_STATUS_NONE = (byte) 0x01;
    byte CARD_STATUS_YELLOW = (byte) 0x02;
    byte CARD_STATUS_RED = (byte) 0x03;
    byte CARD_STATUS_BLACK = (byte) 0x04;
    byte CARD_P_STATUS_NONE = (byte) 0x05;
    byte CARD_P_STATUS_YELLOW = (byte) 0x06;
    byte CARD_P_STATUS_RED = (byte) 0x07;
    byte CARD_P_STATUS_BLACK = (byte) 0x08;

}
