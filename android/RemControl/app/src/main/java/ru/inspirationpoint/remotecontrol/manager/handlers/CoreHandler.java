package ru.inspirationpoint.remotecontrol.manager.handlers;

import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.hardware.usb.UsbDevice;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.coreObjects.Device;
import ru.inspirationpoint.remotecontrol.manager.helpers.BackupHelper;
import ru.inspirationpoint.remotecontrol.manager.helpers.UDPHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.TCPHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.TCPScheduler;
import ru.inspirationpoint.remotecontrol.ui.activity.FightActivity;
import ru.inspirationpoint.remotecontrol.ui.dialog.ConfirmationDialog;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEVICE_ID_SETTING;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEV_TYPE_REFEREE;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEV_TYPE_SM;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.SM_CODE;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.SM_IP;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.UDPCommands.PING_UDP;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.AUTH_RESPONSE;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.CODE_INCORRECT_AUTH;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.DEV_TYPE_CAM;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.RC_EXISTS_AUTH;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.TCP_OK;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivity.WIFI_MESSAGE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.SYNC_STATE_NONE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.SYNC_STATE_SYNCED;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.SYNC_STATE_SYNCING;


public class CoreHandler implements TCPHelper.TCPListener {

    private static Vibrator vibrator;
    private Context context;
    private AppCompatActivity activity;
    private ArrayList<Device> connectedDevices = new ArrayList<>();
    private int mode;
    private CoreServerCallback serverCallback;
    private UsbConnectionHandler usbHandler;
    private TCPHelper tcpHelper;
    private UDPHelper udpHelper;
    private String camIp = "";
    private String smIp = "";
    private FightValuesHandler fightHandler;
    private BackupHelper backupHelper;

    private SMMainAliveHandler smMainAliveHandler;

    private TCPScheduler scheduler;

    public boolean camExists = false;

    public ObservableBoolean isUSBMode = new ObservableBoolean(false);

    public CoreHandler(Context context, int mode) {
        this.context = context;
        this.mode = mode;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        udpHelper = new UDPHelper(context);
        scheduler = new TCPScheduler(this);
        udpHelper.setListener(new UDPHelper.BroadcastListener() {
            @Override
            public void onReceive(String[] msg, String ip) {
//                if (msg[0].equals(OK_UDP) && tcpHelper == null) {
//                    tcpHelper = new TCPHelper(ip);
//                    tcpHelper.setListener(CoreHandler.this);
//                    tcpHelper.start();
//                    connectedDevices.add(new Device(ip, DEV_TYPE_SM, SettingsManager.getValue(SM_CODE, "")));
//                } else if (msg[0].equals(WRONG_CODE_UDP) || msg[0].equals(RC_EXISTS_UDP)) {
//                    if (activity instanceof FightActivity) {
//                        ((FightActivity) activity).getViewModel().syncState.set(SYNC_STATE_NONE);
//                    }
//                }
//                if (msg[0].equals(PING_UDP)) {
//                    tryToConnect();
//                }
            }

            @Override
            public void onCreated() {

            }
        });
        smMainAliveHandler = new SMMainAliveHandler(this);
        startUSB();
        isUSBMode.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                onDisconnect();
                if (isUSBMode.get()) {
//                    if (activity instanceof FightActivity) {
//                        ((FightActivity)activity).getViewModel().syncState.set(SYNC_STATE_SYNCED);
//                    }
                } else {
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            activity.finishAndRemoveTask();
//                            System.exit(0);
//                        }
//                    }, 1000);

                }
            }
        });
    }

    public void tryToConnect() {
        Log.wtf("CODE", SettingsManager.getValue(SM_CODE, "") + "|||" + SettingsManager.getValue(SM_IP, ""));
        if (tcpHelper == null || !tcpHelper.isConnected()) {
            if (!TextUtils.isEmpty(SettingsManager.getValue(SM_IP, ""))) {
                    new Thread(() -> {
//                        try {
//                            if (InetAddress.getByName(SettingsManager.getValue(SM_IP, "")).isReachable(200)) {
//                                Log.wtf("REACH", "++");
                                tcpHelper = new TCPHelper(SettingsManager.getValue(SM_IP, ""));
                                tcpHelper.setListener(CoreHandler.this);
                                tcpHelper.start();
//                            } else {
//                                Log.wtf("REACH", "--");
//                                SettingsManager.removeValue(SM_CODE);
//                                SettingsManager.removeValue(SM_IP);
//                                onDisconnect();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }).start();
            } else {
                SettingsManager.removeValue(SM_CODE);
                SettingsManager.removeValue(SM_IP);
                onDisconnect();
            }
        }
    }

    public void startWiFiNetworking() {
        if (!isUSBMode.get()) {
            if (checkWifiOnAndConnected()) {
                if (!udpHelper.isAlive()) {
                    if (!udpHelper.isUDPAlive()) {
                        udpHelper.start();
                    }
                }

            } else {
                ConfirmationDialog.show(activity, WIFI_MESSAGE, "",
                        context.getResources().getString(R.string.wifi_off_error));
            }
        }
    }

    public void vibr() {
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(120);
            }
        }
    }

    public void startTCP(String ip) {
        tcpHelper = new TCPHelper(ip);
        tcpHelper.setListener(CoreHandler.this);
        tcpHelper.start();
    }

    public void vibrContiniously() {
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0L, 150L, 200L, 150L}, -1));
            } else {
                //deprecated in API 26
                vibrator.vibrate(new long[]{0L, 150L, 200L, 150L}, -1);
            }
        }
    }

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr != null) {
            if (wifiMgr.isWifiEnabled()) {

                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                return wifiInfo.getNetworkId() != -1;
            } else {
                return false;
            }
        }

        return false;
    }

    public int getMode() {
        return mode;
    }

    public ArrayList<Device> getConnectedDevices() {
        return connectedDevices;
    }

    public ArrayList<Device> getConnectedCameras() {
        ArrayList<Device> cameras = new ArrayList<>();
        for (Device device : connectedDevices) {
            if (device.getType() == DEV_TYPE_CAM) {
                cameras.add(device);
            }
        }
        return cameras;
    }

    public ArrayList<Device> getConnectedRepeaters() {
        ArrayList<Device> repeaters = new ArrayList<>();
        for (Device device : connectedDevices) {
            if (device.getType() == DEV_TYPE_CAM) {
                repeaters.add(device);
            }
        }
        return repeaters;
    }

    public Device getConnectedSm() {
        for (Device device : connectedDevices) {
            if (device.getType() == DEV_TYPE_SM) {
                return device;
            }
        }
        return null;
    }

    public Device getConnectedReferee() {
        for (Device device : connectedDevices) {
            if (device.getType() == DEV_TYPE_REFEREE) {
                return device;
            }
        }
        return null;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
        if (activity instanceof FightActivity) {
            //TODO fix to check old fight
            fightHandler = new FightValuesHandler(null, this);
            backupHelper = new BackupHelper(activity);
        }
    }

    public void startUSB() {
        usbHandler = new UsbConnectionHandler(context, this);
    }

    public BackupHelper getBackupHelper() {
        return backupHelper;
    }

    public void setServerCallback(CoreServerCallback serverCallback) {
        this.serverCallback = serverCallback;
    }

    @Override
    public void onReceive(byte command, byte status, byte[] message) {
        smMainAliveHandler.start();
        Log.wtf("RECEIVE", command + " ");
        if (command == AUTH_RESPONSE) {
            switch (status) {
                case TCP_OK:
                    ((FightActivity)activity).getViewModel().syncState.set(SYNC_STATE_SYNCED);
                    if (!isUSBMode.get()) {
                        connectedDevices.add(new Device(tcpHelper.getServerIp(), DEV_TYPE_SM, SettingsManager.getValue(SM_CODE, "")));
                    }
                    break;
                case CODE_INCORRECT_AUTH:
                case RC_EXISTS_AUTH:
                    SettingsManager.removeValue(SM_CODE);
                    SettingsManager.removeValue(SM_IP);
                    onDisconnect();
                    break;

            }
        } else
        if (serverCallback != null) {
            serverCallback.messageReceived(command, message);
        }
    }

    @Override
    public void onStreamCreated() {
//        sendToSM(new SetTimerCommand(180000, 0).getBytes());
//        Log.wtf("STREAM", "+");
        sendToSM(CommandHelper.auth(SettingsManager.getValue(SM_CODE, ""),
                SettingsManager.getValue(DEVICE_ID_SETTING, "")));
        smMainAliveHandler.start();
        scheduler.start();
    }

    @Override
    public void onDisconnect() {
        if (tcpHelper != null) {
            tcpHelper.end();
        }
        tcpHelper = null;
        if (serverCallback != null)
            serverCallback.connectionLost();
        connectedDevices.clear();
        smMainAliveHandler.finish();
        scheduler.finish();
        if (!isUSBMode.get()) {
            if (checkWifiOnAndConnected()) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(SettingsManager.getValue(SM_CODE, "")) &&
                                !TextUtils.isEmpty(SettingsManager.getValue(SM_IP, ""))) {
                            ((FightActivity) activity).getViewModel().syncState.set(SYNC_STATE_SYNCING);
                            Log.wtf("RESYNCing", "+");
                            try {
                                Log.wtf("ITNITIAL CHECK: reach", String.valueOf(InetAddress.getByName(SettingsManager.getValue(SM_IP, "")).isReachable(200)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            tryToConnect();
                        }
                    }
                }, 2000);
            } else {
                startWiFiNetworking();
            }
        }
    }

    public void updateSMAlive(int remain) {
        if (!isUSBMode.get()) {
            if (remain == 0) {
                onDisconnect();
            }
        }
    }

    public void sendToSM(byte[] message) {
        if (isUSBMode.get()) {
            usbHandler.writeSMUsb(message);
        } else if (tcpHelper != null) {
            tcpHelper.send(message);
        } else {
            Log.wtf("TCP NULL", "++++");
        }
    }

    public interface CoreServerCallback {
        void messageReceived(byte command, byte[] message);

        void connectionLost();

        void devicesUpdated(ArrayList<Device> devices);
    }

    public void notifyRCVisibility(boolean v, boolean ph, boolean p, boolean c) {
        sendToSM(CommandHelper.visibilityOpt(v, ph, p, c));
    }

    public void changeVideoCounters(int left, int right) {
        sendToSM(CommandHelper.videoCounters(left, right));
    }

    public void receiveUsbMessage(byte[] message) {

    }

    public void refreshUSBData() {

    }

    public void onUsbConnected(UsbDevice device) {
        Log.wtf("USB", device.getVendorId() + "|" + device.getProductId() + "|" + device.getDeviceName());
    }

    public FightValuesHandler getFightHandler() {
        return fightHandler;
    }

    public void showInLog ( String text) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((FightActivity)activity).getBinding().syncLay.tvLog.setMovementMethod(new ScrollingMovementMethod());
                    final Layout layout = ((FightActivity)activity).getBinding().syncLay.tvLog.getLayout();
                    if (layout != null) {
                        int scrollDelta = layout.getLineBottom(((FightActivity)activity).getBinding().syncLay.tvLog.getLineCount() - 1)
                                - ((FightActivity)activity).getBinding().syncLay.tvLog.getScrollY() - ((FightActivity)activity).getBinding().syncLay.tvLog.getHeight();
                        if (scrollDelta > 0)
                            ((FightActivity)activity).getBinding().syncLay.tvLog.scrollBy(0, scrollDelta);
                    }
//                Log.wtf("LOG", activity.get().getViewModel().logTextTemp.get());
                }
            });
            ((FightActivity)activity).getViewModel().logTextTemp.set(((FightActivity)activity).getViewModel().logTextTemp.get() + "\n " + text);
        }
    }
}
