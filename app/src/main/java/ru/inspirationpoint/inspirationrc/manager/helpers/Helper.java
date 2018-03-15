package ru.inspirationpoint.inspirationrc.manager.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class Helper {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat SHORT_TIME_FORMAT = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private static final SimpleDateFormat SERVER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat SERVER_DATE_FULL_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.getDefault());
    private static final SimpleDateFormat SERVER_EXTENDED_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private static final long HOUR_MS = 60 * 60 * 1000;

    public static void hideKeyboard(View view, Context context) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void beep(Context context, int soundDurationMS, int vibrateDurationMS) {
        if (vibrateDurationMS > 0) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(vibrateDurationMS);
            }
        }
        if (soundDurationMS > 0) {
            final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
            toneGenerator.startTone(ToneGenerator.TONE_DTMF_S, vibrateDurationMS);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toneGenerator.stopTone();
                    toneGenerator.release();
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
}