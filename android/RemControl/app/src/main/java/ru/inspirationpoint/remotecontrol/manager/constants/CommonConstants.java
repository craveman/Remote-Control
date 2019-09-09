package ru.inspirationpoint.remotecontrol.manager.constants;

import android.util.Pair;

public interface CommonConstants {
    String SESSION_ID_FIELD = "uuid";
    String LAST_USER_NAME_FIELD = "last_name";
    String LAST_USER_EMAIL_FIELD = "last_email";
    String LAST_USER_ID_FIELD = "last_id";
    String LANGUAGE_FIELD = "language";
    String SOUND_FIELD = "sound";
    String VIBRATION_FIELD = "vibration";
    String WEAPON_TYPE = "weapon";
    String LOCALE_CHANGED_FIELD = "locale_changed";
    String USER_ID_FIELD = "user_id";
    String DATE_FIELD = "date";
    String PLACE_FIELD = "place";
    String IS_COACH_FIELD = "is_coach";
    String IS_DARK_THEME = "is_dark";
    String IS_THEME_CHANGED = "is_changed";
    String IS_PHRASES_ENABLED = "is_phrases";
    String IS_COMMANDS_ENABLED = "is_commands";
    String CURRENT_COMMANDS_ID = "comm_id";
    String LAST_CONNECTED_SM_IP = "last_connected_ip";
    String LAST_CONNECTED_SM_CODE = "last_connected_code";
    String LAST_FIGHT_ID = "last_fight_id";
    String CLOUD_TOKEN = "cloud_token";
    String IS_BY_FLAGS_STOP = "by_flags";
    String DEVICE_ID_SETTING = "device_id";
    String UNFINISHED_FIGHT = "unfinished";
    String SM_CODE = "group_address";
    String SM_IP = "sm_ip";


    String[] phrasesRU = {"Простая атака", "Атака с действием на оружие", "Сложная атака",
            "Ответная атака", "Атака на подготовку", "Контратака", "Ремиз", "Парад рипост",
            "Контр рипост", "Прямая рука", "Штрафной укол"};

    String[] phrasesEN = {"Direct attack", "Attack with action on a weapon", "Combined attack",
            "Return attack", "Attack on preparing", "Counter-attack", "Remise", "Parade riposte",
            "Counter riposte", "Direct hand", "Penalty touch"};

    String SAVED_OWNER = "saved_owner";
    String SAVED_LEFT_NAME = "saved_left_name";
    String SAVED_RIGHT_NAME = "saved_right_name";
    String SAVED_LEFT_SCORE = "saved_left_score";
    String SAVED_RIGHT_SCORE = "saved_right_score";

    int WIFI_STATE_CONNECTED = 1;
    int WIFI_STATE_NOT_CONNECTED = 23;
    int WIFI_STATE_OFF = 3;
    byte[] FILE_TRANSFER_START = new byte[]{(byte)0x0a, (byte)0x0b, (byte)0x0c};
    byte[] FILE_TRANSFER_ACCEPT = new byte[]{(byte)0x1d, (byte)0x1e, (byte)0x1f};
    Pair<Integer, Integer>[] PAIRS = new Pair[] {new Pair(1, 4),
            new Pair(2, 5),
            new Pair(3, 6),
            new Pair(7, 1),
            new Pair(5, 4),
            new Pair(2, 3),
            new Pair(6, 7),
            new Pair(5, 1),
            new Pair(4, 3),
            new Pair(6, 2),
            new Pair(5, 7),
            new Pair(3, 1),
            new Pair(4, 6),
            new Pair(7, 2),
            new Pair(3, 5),
            new Pair(1, 6),
            new Pair(2, 4),
            new Pair(7, 3),
            new Pair(6, 5),
            new Pair(1, 2),
            new Pair(4, 7),};
    String FIGHTER_ONE = "fighter_one";
    String FIGHTER_TWO = "fighter_two";
    String FIGHTER_THREE = "fighter_three";
    String FIGHTER_FOUR = "fighter_four";
    String FIGHTER_FIVE = "fighter_five";
    String FIGHTER_SIX = "fighter_six";
    String FIGHTER_SEVEN = "fighter_seven";
    String CURRENT_PAIR = "current_pair";
    String GROUP_ID = "group_id";
    //Metrics
    String METRICS = "metrics";
    String PHRASES_METRIC = "phrases_metric";
    //Video cloud
    String CLOUD_AUTH_URL = "https://api.selcdn.ru/auth/v1.0";
    String CLOUD_STORAGE = "https://api.selcdn.ru/v1/SEL_84713/test/";
    String CLOUD_LOGIN = "84713_Shander";
    String CLOUD_PASS = "Z8iRDmOxV5";
    String CLOUD_HEADER_TOKEN = "X-Auth-Token";
    String CLOUD_HEADER_LOGIN = "X-Auth-User";
    String CLOUD_HEADER_PASS = "X-Auth-Key";
    //WiFi direct
    int DEV_TYPE_SM = 1;
    int DEV_TYPE_RC = 2;
    int DEV_TYPE_CAM = 3;
    int DEV_TYPE_REP = 4;
    int DEV_TYPE_CP = 5;
    int DEV_TYPE_REFEREE = 6;

    String DEV_STRING_TYPE_REFEREE = "RESERVE_REF_";
    String DEV_STRING_TYPE_SM_02 = "SM02_";
    String DEV_STRING_TYPE_RC = "REM_CONTROL_";
    String DEV_STRING_TYPE_REPEATER = "REPEATER_";
    String DEV_STRING_TYPE_CAMERA = "CAMERA_";
    String DEV_STRING_TYPE_CONTROL_PANEL = "CTRL_PANEL_";
    enum TimerState {
        NotStarted, InProgress, InPause
    }
    enum CardStatus {
        CardStatus_None, CardStatus_Yellow, CardStatus_Red, CardStatus_Black,
        CardPStatus_None, CardPStatus_Yellow, CardPStatus_Red, CardPStatus_Black
    }
    enum TimerMode {
        Usual, Pause, Medical
    }

    String[] CHARS = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    interface UDPCommands{
        String PING_UDP = "PING";
        String HELLO_UDP = "HELLO";
        String OK_UDP = "OK";
        String RC_EXISTS_UDP = "EXIST";
        String WRONG_CODE_UDP = "WRCODE";
    }
}