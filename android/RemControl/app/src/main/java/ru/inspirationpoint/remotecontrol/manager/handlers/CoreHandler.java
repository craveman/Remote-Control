package ru.inspirationpoint.remotecontrol.manager.handlers;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.SetTimerCommand;
import ru.inspirationpoint.remotecontrol.manager.coreObjects.Device;
import ru.inspirationpoint.remotecontrol.manager.helpers.UDPHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.TCPHelper;
import ru.inspirationpoint.remotecontrol.ui.activity.FightActivity;
import ru.inspirationpoint.remotecontrol.ui.dialog.ConfirmationDialog;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEV_TYPE_REFEREE;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEV_TYPE_SM;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.UDPCommands.PING_UDP;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.DEV_TYPE_CAM;


public class CoreHandler implements TCPHelper.TCPListener{

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

    public boolean camExists = false;

    public CoreHandler(Context context, int mode) {
        this.context = context;
        this.mode = mode;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        udpHelper = new UDPHelper(context);
        udpHelper.setListener(new UDPHelper.BroadcastListener() {
            @Override
            public void onReceive(String[] msg, String ip) {
                if (msg[0].equals(PING_UDP)) {

                }
            }

            @Override
            public void onCreated() {

            }
        });
    }

    public void startWiFiNetworking() {
        if (checkWifiOnAndConnected()) {
            if (!udpHelper.isAlive()) {
                if (!udpHelper.isUDPAlive()) {
                    udpHelper.start();
                }
            }

        } else {
            ConfirmationDialog.show(activity, 7585, context.getResources().getString(R.string.wifi_off_title),
                    context.getResources().getString(R.string.wifi_off_error));
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
            }
            else {
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
        }
    }

    public void setServerCallback(CoreServerCallback serverCallback) {
        this.serverCallback = serverCallback;
    }

    @Override
    public void onReceive(byte command, byte[] message) {
        if (serverCallback != null) {
            serverCallback.messageReceived(command, message);
        }
    }

    @Override
    public void onStreamCreated() {
        sendToSM(new SetTimerCommand(180000, 0).getBytes());
    }

    @Override
    public void onDisconnect() {
        tcpHelper.end();
        tcpHelper = null;
        if (serverCallback != null)
        serverCallback.connectionLost();
    }

    public void sendToSM(byte[] message) {
        if (tcpHelper != null) {
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

    public void passiveLockChange() {
        sendToSM(CommandHelper.passiveLock());
    }

    public void setUsbHandler(UsbConnectionHandler usbHandler) {
        this.usbHandler = usbHandler;
    }

    public void receiveUsbMessage(byte[] message) {

    }

    public void refreshUSBData() {

    }

    public void onUsbConnected(UsbDevice device) {

    }

    public FightValuesHandler getFightHandler() {
        return fightHandler;
    }
}
