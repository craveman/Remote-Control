package ru.inspirationpoint.inspirationrc.manager;

import android.content.Context;
import android.content.SharedPreferences;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;

public abstract class SettingsManager {

    private static final String PREFERENCES = "preferences";

    public static void setValue(Context context, String key, String value) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putString(key, value);
                editor.commit();
            }
        }
    }

    public static String getValue(Context context, String key, String defaultValue) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            return preferences.getString(key, defaultValue);
        }

        return defaultValue;
    }

    public static void setValue(Context context, String key, int value) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putInt(key, value);
                editor.commit();
            }
        }
    }

    public static int getValue(Context context, String key, int defaultValue) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            return preferences.getInt(key, defaultValue);
        }

        return defaultValue;
    }

    public static void setValue(Context context, String key, long value) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putLong(key, value);
                editor.commit();
            }
        }
    }

    public static long getValue(Context context, String key, long defaultValue) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            return preferences.getLong(key, defaultValue);
        }

        return defaultValue;
    }

    public static void setLongValue(Context context, String key, long value) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putLong(key, value);
                editor.commit();
            }
        }
    }

    public static long getLongValue(Context context, String key, long defaultValue) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            return preferences.getLong(key, defaultValue);
        }

        return defaultValue;
    }

    public static void setValue(Context context, String key, boolean value) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putBoolean(key, value);
                editor.commit();
            }
        }
    }

    public static boolean getValue(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            return preferences.getBoolean(key, defaultValue);
        }

        return defaultValue;
    }

    public static void removeValue(Context context, String key) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.remove(key);
                editor.commit();
            }
        }
    }

    public static boolean hasValue(Context context, String key) {
        SharedPreferences preferences = getPreferences(context);
        return preferences == null ? false : preferences.contains(key);
    }

    public static void setValue(String key, String value) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putString(key, value);
                editor.commit();
            }
        }
    }

    public static String getValue(String key, String defaultValue) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        if (preferences != null) {
            return preferences.getString(key, defaultValue);
        }

        return defaultValue;
    }

    public static void setValue(String key, int value) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putInt(key, value);
                editor.commit();
            }
        }
    }

    public static int getValue(String key, int defaultValue) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        if (preferences != null) {
            return preferences.getInt(key, defaultValue);
        }

        return defaultValue;
    }

    public static void setValue(String key, long value) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putLong(key, value);
                editor.commit();
            }
        }
    }

    public static long getValue(String key, long defaultValue) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        if (preferences != null) {
            return preferences.getLong(key, defaultValue);
        }

        return defaultValue;
    }

    public static void setValue(String key, boolean value) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putBoolean(key, value);
                editor.commit();
            }
        }
    }

    public static boolean getValue(String key, boolean defaultValue) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        if (preferences != null) {
            return preferences.getBoolean(key, defaultValue);
        }

        return defaultValue;
    }

    public static void removeValue(String key) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.remove(key);
                editor.commit();
            }
        }
    }

    public static boolean hasValue(String key) {
        SharedPreferences preferences = getPreferences(InspirationDayApplication.getApplication().getApplicationContext());
        return preferences == null ? false : preferences.contains(key);
    }

    private static SharedPreferences getPreferences(Context context) {
        if (context == null) {
            return null;
        }

        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
}
