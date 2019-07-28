package ru.inspirationpoint.inspirationrc.manager.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightData;

public abstract class Helper {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat SHORT_TIME_FORMAT = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private static final SimpleDateFormat SERVER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat SERVER_DATE_FULL_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.getDefault());
    private static final SimpleDateFormat SERVER_EXTENDED_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private static final long HOUR_MS = 60 * 60 * 1000;
    private static Vibrator vibrator;
    private static ToneGenerator toneGenerator;
    private static Handler handler;

    public static void hideKeyboard(View view, Context context) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void prepareBeep(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler();
        toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
    }

    public static void cancelBeep() {
        toneGenerator.release();
    }

    public static void beep(Context context, int soundDurationMS, int vibrateDurationMS) {
        if (vibrateDurationMS > 0) {
            if (vibrator != null) {
                vibrator.vibrate(vibrateDurationMS);
            }
        }
        if (soundDurationMS > 0) {
            toneGenerator.startTone(ToneGenerator.TONE_DTMF_S, vibrateDurationMS);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toneGenerator.stopTone();
                }
            }, soundDurationMS);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static Date serverStringToDate(String textDate) {
        try {
            return SERVER_DATE_FORMAT.parse(textDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date serverStringToFullDate(String textDate) {
        try {
            return SERVER_DATE_FULL_FORMAT.parse(textDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToServerString(Date date) {
        return SERVER_DATE_FORMAT.format(date);
    }

    public static String dateToExtServerString(Date date) {
        return SERVER_EXTENDED_FORMAT.format(date);
    }

    public static String dateToString(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String timeToString(Date time) {
        return (time.getTime() < HOUR_MS ? SHORT_TIME_FORMAT : TIME_FORMAT).format(time);
    }

    public static String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(String.format("%02X", aByte));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }

//    public static FightInput convertFightDataToInput(FightData mFightData) {
//        FightInput fight = new FightInput();
//        fight._id = mFightData.getId();
//        fight.date = Helper.dateToServerString(mFightData.getDate());
//        fight.address = mFightData.getPlace();
//        fight.leftFighterId = mFightData.getLeftFighter().getId();
//        fight.leftFighterName = mFightData.getLeftFighter().getName();
//        fight.rightFighterId = mFightData.getRightFighter().getId();
//        fight.rightFighterName = mFightData.getRightFighter().getName();
//        fight.startTime = mFightData.getmStartTime();
//        return fight;
//    }
}