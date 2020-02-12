package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public interface CommandsContract {

    int HEADER_LENGTH = 4;
    byte PROTOCOL_VERSION = (byte)0x01;

    byte SETNAME_TCP_CMD = (byte) 0x01;
    byte SETSCORE_TCP_CMD = (byte) 0x03;
    byte SETCARD_TCP_CMD = (byte) 0x04;
    byte SETPRIORITY_TCP_CMD = (byte) 0x05;
    byte SETPERIOD_TCP_CMD = (byte) 0x06;
    byte SETWEAPON_TCP_CMD = (byte) 0x07;
    byte SETTIMER_TCP_CMD = (byte) 0x08;
    byte STARTTIMER_TCP_CMD = (byte) 0x09;
    byte SWAP_FIGHTERS_TCP_CMD = (byte) 0x0A;
    byte BROADCAST_TCP_CMD = (byte) 0x0B;
    byte VISIBILITY_OPTIONS_CMD = (byte) 0x0C;
    byte VIDEO_COUNTER_CMD = (byte) 0x0D;
    byte HELLO_TCP_CMD = (byte) 0x0E;
    byte DISCONNECT_TCP_CMD = (byte) 0x0F;
    byte PASSIVE_STATE = (byte) 0x10;
    byte PASSIVE_MAX = (byte) 0x11;
    byte PAUSE_FINISHED = (byte) 0x12;
    byte SET_DEF_TIMER_CMD = (byte) 0x13;
    byte COMPETITION_SET = (byte) 0x14;
    byte VIDEO_ROUTES = (byte) 0x15;
    byte LOAD_FILE_TCP_CMD = (byte) 0x16;
    byte PLAYER_TCP_CMD = (byte) 0x17;
    byte RECORD_TCP_CMD = (byte) 0x18;
    byte DEV_LIST_REQUEST = (byte) 0x19;
    byte DEV_LIST = (byte) 0x1A;
    byte VIDEO_READY = (byte) 0x1B;
    byte VIDEO_RECEIVED = (byte) 0x1C;
    byte RESET_TCP_CMD = (byte) 0x1D;
    byte ETH_NEXT_PREV = (byte) 0x1E;
    byte ETH_APPLY_FIGHT = (byte) 0x1F;
    byte FINISH_FIGHT_ASK = (byte) 0x20;
    byte DISP_RECEIVE_CMD = (byte) 0x21;
    byte ETH_ACK_NAK = (byte) 0x22;
    byte AUTH_TCP_CMD = (byte) 0x23;
    byte AUTH_RESPONSE = (byte) 0x24;
    byte VIDEO_TRANSFER_ABORT = (byte) 0x25;
    byte RESPONSE_CMD = (byte) 0xAA;

    byte SET_BLUETOOTH_MODE = (byte) 0xB0;

    byte PING_IN = (byte) 0xF1;
    byte PING_OUT = (byte) 0xF2;

    //TODO REMOVE TEMP AND ASK FOR DEVICES

    byte TCP_OK = (byte) 0x01;
    byte CODE_INCORRECT_AUTH = (byte) 0x81;
    byte RC_EXISTS_AUTH = (byte) 0x82;

    byte ADD_STATE = (byte) 0x66;

    byte END_USB_MODE = (byte) 0x90;

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
