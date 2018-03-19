package ru.inspirationpoint.inspirationrc;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Field;

import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.DataManager;
import ru.inspirationpoint.inspirationrc.manager.helpers.LocaleHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.TCPHelper;
import ru.inspirationpoint.inspirationrc.ui.activity.LoginActivity;
import server.Server;


public final class InspirationDayApplication extends Application {

    public final static EServerType mServerType = EServerType.Prod;
    private final static String LOG_TAG = Application.class.getSimpleName();
    private static String PROD_SERVER_URL = "http://public-api.inspirationpoint.ru";
    private static String QA_SERVER_URL = "http://diary-public.q-s.pw";
    private static InspirationDayApplication mApplicationInstance = null;
    private static Typeface customFontTypeface;
    private static String STRING_DOLAR = "$";
    private TCPHelper helper;

    public static InspirationDayApplication getApplication() {
        return mApplicationInstance;
    }

    public static boolean isProdVersion() {
        return mApplicationInstance.mServerType == EServerType.Prod;
    }

    public void resetSession() {
        DataManager.instance().setSessionId("");

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public TCPHelper startTCPHelper(String ip) {
        helper = new TCPHelper(ip);
        return helper;
    }

    public TCPHelper getHelper() {
        return helper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        overrideFont(getApplicationContext(), "SERIF", "geom.ttf");
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
//        setDarkTheme(preferences.getBoolean(Constants.IS_DARK_THEME, false));
        setTheme(preferences.getBoolean(Constants.IS_DARK_THEME, false));
        Server.instance().init(mServerType == EServerType.Prod ? PROD_SERVER_URL : QA_SERVER_URL);
        mApplicationInstance = this;
    }

    public static Typeface getCustomFontTypeface() {
        return customFontTypeface;
    }

    @Override
    public void onTerminate() {
        mApplicationInstance = null;

        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        Log.w(LOG_TAG, "Low memory.");
        super.onLowMemory();
    }

    private void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);
            try {
                final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
                defaultFontTypefaceField.setAccessible(true);
                defaultFontTypefaceField.set(null, customFontTypeface);
            } catch (Exception e) {
                Log.d("Fonts Error", "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
            }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public enum EServerType {
        Prod, QA
    }

    public void setTheme(boolean isDark) {
        setTheme(isDark ? R.style.AppThemeDark : R.style.AppTheme);
    }

    public static void setDarkTheme(boolean isSet) {
        Log.d("IS SET", String.valueOf(isSet));
        setRColor("colorPrimary", isSet ? Color.parseColor("#02284C") : Color.parseColor("#E6E7E8"));
        setRColor("colorPrimaryVeryDark", isSet ? Color.parseColor("#0082FC") : Color.parseColor("#021BB2"));
        setRColor("textColorLightDisabled", isSet ? Color.parseColor("#FEFEFE") : Color.parseColor("#EAEAEA"));
        setRColor("divider", isSet ? Color.parseColor("#FEFEFE") : Color.parseColor("#BDBDBD"));
    }

    public static void setRColor(String rFieldName, Object newValue) {
        Log.d("SETRCOLOR", rFieldName + " + " + String.format("#%06X", (0xFFFFFF & (int)newValue)));
        setR(R.class, rFieldName, newValue);
    }

    public static void setR(Class rClass, String rFieldName, Object newValue) {
        setStatic(rClass.getName() + STRING_DOLAR  + "color", rFieldName, newValue);
    }

    public static boolean setStatic(String aClassName, String staticFieldName, Object toSet) {
        try {
            return setStatic(Class.forName(aClassName), staticFieldName, toSet);
        } catch (ClassNotFoundException e) {
            Log.d("SET STATIC", e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setStatic(Class<?> aClass, String staticFieldName, Object toSet) {
        try {
            Field declaredField = aClass.getDeclaredField(staticFieldName);
            declaredField.setAccessible(true);
            declaredField.set(null, toSet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
