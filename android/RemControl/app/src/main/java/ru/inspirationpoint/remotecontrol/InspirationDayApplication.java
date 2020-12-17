package ru.inspirationpoint.remotecontrol;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.Contract;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;

import ru.inspirationpoint.remotecontrol.manager.Camera;
import ru.inspirationpoint.remotecontrol.manager.Repeater;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.handlers.CoreHandler;
import ru.inspirationpoint.remotecontrol.manager.handlers.UsbConnectionHandler;
import ru.inspirationpoint.remotecontrol.manager.helpers.LocaleHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.TCPHelper;
import ru.inspirationpoint.remotecontrol.ui.activity.LoginActivity;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.CHARS;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEVICE_ID_SETTING;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEV_STRING_TYPE_RC;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.DEV_TYPE_RC;


public final class InspirationDayApplication extends Application {

    private static final String PROD_SERVER_URL = "http://public-api.inspirationpoint.ru";
    private static InspirationDayApplication mApplicationInstance = null;
    private static Typeface customFontTypeface;
    private TCPHelper helper;
    private LinkedHashSet<Camera> cameras = new LinkedHashSet<>();
    private LinkedHashSet<Repeater> repeaters = new LinkedHashSet<>();
    private int camId;
    private int refereeId;
    private String rcIpForCam;
    private String smIpForCam = "";
    private boolean isSMVideoTarget = true;
    private boolean isSaveNeeded = true;
    private CoreHandler coreHandler;
    private final Random rnd = new Random();
    private int mode = DEV_TYPE_RC;
    private String modeString = DEV_STRING_TYPE_RC;
    private UsbConnectionHandler usbHandler;

    @Contract(pure = true)
    public static InspirationDayApplication getApplication() {
        return mApplicationInstance;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        coreHandler.setMode(mode);
    }

    public String getModeString() {
        return modeString;
    }

    public void setModeString(String modeString) {
        this.modeString = modeString;
    }

    public void resetSession() {
//        DataManager.instance().setSessionId("");

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public TCPHelper startTCPHelper(String ip, int code) {
        helper = new TCPHelper(ip, code);
        return helper;
    }

    public void endTcp() {
        helper.end();
        helper = null;
    }

    @Contract(pure = true)
    public TCPHelper getHelper() {
        return helper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        try {
//            udpHelper = new UDPHelper(this);
//            udpHelper.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        overrideFont(getApplicationContext());
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        preferences.edit().putBoolean(CommonConstants.IS_DARK_THEME, true).apply();
        setDarkTheme(preferences.getBoolean(CommonConstants.IS_DARK_THEME, false));
        setTheme(preferences.getBoolean(CommonConstants.IS_DARK_THEME, false));
//        Server.instance().init(PROD_SERVER_URL);
        mApplicationInstance = this;
        if (TextUtils.isEmpty(SettingsManager.getValue(DEVICE_ID_SETTING, ""))) {
            SettingsManager.setValue(DEVICE_ID_SETTING, String.valueOf(rnd.nextInt(10)) +
                    rnd.nextInt(10) +
                    CHARS[rnd.nextInt(CHARS.length)] + CHARS[rnd.nextInt(CHARS.length)] +
                    rnd.nextInt(10));
        }
        camId = (new Random().nextInt((999) + 1))*100;
        refereeId = (new Random().nextInt((999) + 1))*10;
        coreHandler = new CoreHandler(this, DEV_TYPE_RC);
//        usbHandler = new UsbConnectionHandler(this, coreHandler);
    }

    public int getRefereeId() {
        return refereeId;
    }

    public int getCamId() {
        return camId;
    }

    public void setCamId(int camId) {
        this.camId = camId;
    }

    @Contract(pure = true)
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
        super.onLowMemory();
    }

    private void overrideFont(Context context) {
        customFontTypeface = Typeface.createFromAsset(context.getAssets(), "dinPro.otf");
            try {
                final Field defaultFontTypefaceField = Typeface.class.getDeclaredField("SERIF");
                defaultFontTypefaceField.setAccessible(true);
                defaultFontTypefaceField.set(null, customFontTypeface);
            } catch (Exception e) {
                Log.d("Fonts Error", "Can not set custom font instead of " + "SERIF");
            }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public void setTheme(boolean isDark) {
        setTheme(isDark ? R.style.AppThemeDark : R.style.AppTheme);
        getApplicationContext().setTheme(isDark ? R.style.AppThemeDark : R.style.AppTheme);
    }

    public static void setDarkTheme(boolean isSet) {
        setRColor("colorPrimary", isSet ? Color.parseColor("#02284C") : Color.parseColor("#E6E7E8"));
        setRColor("colorPrimaryVeryDark", isSet ? Color.parseColor("#0082FC") : Color.parseColor("#021BB2"));
        setRColor("textColorLightDisabled", isSet ? Color.parseColor("#FEFEFE") : Color.parseColor("#EAEAEA"));
        setRColor("divider", isSet ? Color.parseColor("#FEFEFE") : Color.parseColor("#BDBDBD"));
    }

    public static void setRColor(String rFieldName, Object newValue) {
        setR(R.class, rFieldName, newValue);
    }

    public static void setR(Class rClass, String rFieldName, Object newValue) {
        String STRING_DOLAR = "$";
        setStatic(rClass.getName() + STRING_DOLAR + "color", rFieldName, newValue);
    }

    public static void setStatic(String aClassName, String staticFieldName, Object toSet) {
        try {
            setStatic(Class.forName(aClassName), staticFieldName, toSet);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setStatic(Class<?> aClass, String staticFieldName, Object toSet) {
        try {
            Field declaredField = aClass.getDeclaredField(staticFieldName);
            declaredField.setAccessible(true);
            declaredField.set(null, toSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr != null) {
            if (wifiMgr.isWifiEnabled()) {

                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                if( wifiInfo.getNetworkId() == -1 ){
                    return CommonConstants.WIFI_STATE_NOT_CONNECTED;
                }
                return CommonConstants.WIFI_STATE_CONNECTED;
            }
            else {
                return CommonConstants.WIFI_STATE_OFF;
            }
        } else return CommonConstants.WIFI_STATE_OFF;
    }

    public LinkedHashSet<Camera> getCameras() {
        return cameras;
    }

    public void setCameras(LinkedHashSet<Camera> cameras) {
        this.cameras = cameras;
    }

    public void addCamera(Camera camera) {
        cameras.add(camera);
    }

    public void removeCamera(Camera camera) {
        Camera temp = null;
        for (Camera temp0 : cameras) {
            if (Objects.equals(temp0.ip.get(), camera.ip.get())) {
                temp = temp0;
            }
        }
        if (temp != null) {
            cameras.remove(temp);
        }
    }

    public void resetCameras() {
        cameras.clear();
    }

    public String getRcIpForCam() {
        return rcIpForCam;
    }

    public void setRcIpForCam(String rcIpForCam) {
        this.rcIpForCam = rcIpForCam;
    }

    public String getSmIpForCam() {
        return smIpForCam;
    }

    public void setSmIpForCam(String smIpForCam) {
        this.smIpForCam = smIpForCam;
    }

    public LinkedHashSet<Repeater> getRepeaters() {
        return repeaters;
    }

    public void setRepeaters(LinkedHashSet<Repeater> repeaters) {
        this.repeaters = repeaters;
    }

    public void addRepeater(Repeater repeater) {
        Log.wtf("REPEATERS", repeaters.size() + "");
        repeaters.add(repeater);
    }

    public void removeRepeater(Repeater repeater) {
        Repeater temp = null;
        for (Repeater temp0 : repeaters) {
            if (Objects.equals(temp0.ip.get(), repeater.ip.get())) {
                temp = temp0;
            }
        }
        if (temp != null) {
            repeaters.remove(temp);
        }
    }

    public void resetRepeaters() {
        repeaters.clear();
    }

    public boolean isSMVideoTarget() {
        return isSMVideoTarget;
    }

    public void setSMVideoTarget(boolean SMVideoTarget) {
        isSMVideoTarget = SMVideoTarget;
    }

    public boolean isSaveNeeded() {
        return isSaveNeeded;
    }

    public void setSaveNeeded(boolean saveNeeded) {
        isSaveNeeded = saveNeeded;
    }

    public CoreHandler getCoreHandler() {
        return coreHandler;
    }
}
