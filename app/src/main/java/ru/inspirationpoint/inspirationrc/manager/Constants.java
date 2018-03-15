package ru.inspirationpoint.inspirationrc.manager;

import android.util.Pair;

public abstract class Constants {
    public final static String SESSION_ID_FIELD = "uuid";
    public final static String LAST_USER_NAME_FIELD = "last_name";
    public final static String LAST_USER_EMAIL_FIELD = "last_email";
    public final static String LAST_USER_ID_FIELD = "last_id";
    public final static String LAST_LINE_FIELD = "last_line";
    public final static String LANGUAGE_FIELD = "language";
    public final static String SOUND_FIELD = "sound";
    public final static String VIBRATION_FIELD = "vibration";
    public final static String LOCALE_CHANGED_FIELD = "locale_changed";
    public final static String USER_ID_FIELD = "user_id";
    public final static String DATE_FIELD = "date";
    public final static String PLACE_FIELD = "place";
    public final static String IS_COACH_FIELD = "is_coach";
    public final static String IS_DARK_THEME = "is_dark";
    public final static String IS_THEME_CHANGED = "is_changed";
    public final static String IS_PHRASES_ENABLED = "is_phrases";
    public final static Pair<Integer, Integer>[] PAIRS = new Pair[] {new Pair(1, 4),
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
    public final static String FIGHTER_ONE = "fighter_one";
    public final static String FIGHTER_TWO = "fighter_two";
    public final static String FIGHTER_THREE = "fighter_three";
    public final static String FIGHTER_FOUR = "fighter_four";
    public final static String FIGHTER_FIVE = "fighter_five";
    public final static String FIGHTER_SIX = "fighter_six";
    public final static String FIGHTER_SEVEN = "fighter_seven";
    public final static String CURRENT_PAIR = "current_pair";
    public final static String GROUP_ID = "group_id";

    public final static  String[] phrasesRU = {"Простая атака", "Атака с действием на оружие", "Сложная атака",
            "Ответная атака", "Атака на подготовку", "Контратака", "Ремиз", "Парад рипост",
            "Контр рипост", "Прямая рука", "Штрафной укол"};

    public final static  String[] phrasesEN = {"Direct attack", "Attack with action on a weapon", "Combined attack",
            "Return attack", "Attack on preparing", "Counter-attack", "Remise", "Parade riposte",
            "Counter riposte", "Direct hand", "Fine point"};
}