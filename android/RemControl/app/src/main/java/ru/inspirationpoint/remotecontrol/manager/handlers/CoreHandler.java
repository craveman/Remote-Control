package ru.inspirationpoint.remotecontrol.manager.handlers;

import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.hardware.usb.UsbDevice;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.coreObjects.Device;
import ru.inspirationpoint.remotecontrol.manager.helpers.BackupHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.TCPHelper;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.TCPScheduler;
import ru.inspirationpoint.remotecontrol.ui.activity.FightActivity;
import ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEVICE_ID_SETTING;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEV_TYPE_REFEREE;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEV_TYPE_SM;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.AUTH_RESPONSE;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.CODE_INCORRECT_AUTH;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.DEV_TYPE_CAM;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.RC_EXISTS_AUTH;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.TCP_OK;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.CARD_STATE_NONE;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.CARD_STATE_RED;
import static ru.inspirationpoint.remotecontrol.ui.activity.FightActivityVM.CARD_STATE_YELLOW;
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
    private String camIp = "";
    private String smIp = "";
    private FightValuesHandler fightHandler;
    private BackupHelper backupHelper;

    private SMMainAliveHandler smMainAliveHandler;

    private TCPScheduler scheduler;

    public boolean camExists = false;

    public ObservableBoolean isUSBMode = new ObservableBoolean(false);
    public Boolean isInRestore = false;

    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager nsdManager;
    private boolean isDiscoveryActive = false;

    public CoreHandler(Context context, int mode) {
        this.context = context;
        this.mode = mode;
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        scheduler = new TCPScheduler(this);
//        udpHelper.setListener(new UDPHelper.BroadcastListener() {
//            @Override
//            public void onReceive(String[] msg, String ip) {
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
//            }
//
//            @Override
//            public void onCreated() {
//
//            }
//        });
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

    public void tryToConnect(String ip) {
        if (tcpHelper == null || !tcpHelper.isConnected()) {
            showInLog("CONN TRY");
            if (tcpHelper!= null) {
                tcpHelper.end();
            }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    tcpHelper = new TCPHelper(ip);
                    tcpHelper.setListener(CoreHandler.this);
                    tcpHelper.start();
                    showInLog("TCP START TRY");
                }, 100);
        } else {
            Log.wtf("TCP NOT NULL", " ");
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
        if (command == AUTH_RESPONSE) {
            switch (status) {
                case TCP_OK:
                    ((FightActivity) activity).getViewModel().syncState.set(SYNC_STATE_SYNCED);
                    if (!isUSBMode.get()) {
                        connectedDevices.add(new Device(tcpHelper.getServerIp(), DEV_TYPE_SM, ""));
                    }
                    break;
                case CODE_INCORRECT_AUTH:
                case RC_EXISTS_AUTH:
                    onDisconnect();
                    break;

            }
        } else if (serverCallback != null) {
            serverCallback.messageReceived(command, message);
        }
    }

    @Override
    public void onStreamCreated() {
//        sendToSM(new SetTimerCommand(180000, 0).getBytes());
        Log.wtf("STREAM", "+");
        sendToSM(CommandHelper.auth("",
                SettingsManager.getValue(DEVICE_ID_SETTING, "")));
        smMainAliveHandler.start();
        scheduler.start();
        stopDiscovery();
        showInLog("STREAM +");
    }

    @Override
    public void onDisconnect() {
        showInLog("DISCONN +");
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
            ((FightActivity)activity).getViewModel().syncState.set(SYNC_STATE_NONE);
        }
    }

    public void updateSMAlive(int remain) {
        if (!isUSBMode.get()) {
            if (remain == 0) {
                Log.wtf("DISCONNECT", "UPD SM ALIVE");
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
            onDisconnect();
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

    public void restorePCards(int ly, int lr, int ry, int rr) {
        FightActivityVM model = ((FightActivity) activity).getViewModel();
        if (lr != 0) {
            for (int i = 0; i < lr; i++) {
                model.leftPCard.set(CARD_STATE_RED);
                model.leftPCard.set(CARD_STATE_YELLOW);
            }
        } else {
            if (ly != 0) {
                model.leftPCard.set(CARD_STATE_YELLOW);
            } else {
                model.leftPCard.set(CARD_STATE_NONE);
            }
        }

        if (rr != 0) {
            for (int i = 0; i < rr; i++) {
                model.rightPCard.set(CARD_STATE_RED);
                model.rightPCard.set(CARD_STATE_YELLOW);
            }
        } else {
            if (ry != 0) {
                model.rightPCard.set(CARD_STATE_YELLOW);
            } else {
                model.rightPCard.set(CARD_STATE_NONE);
            }
        }
    }

    public void initializeDiscoveryListener() {

        NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.wtf("TAG", "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.wtf("TAG", "Resolve Succeeded. " + serviceInfo);

//                if (serviceInfo.getServiceName().equals(serviceName)) {
//                    Log.wtf("TAG", "Same IP.");
//                    return;
//                }
                if (serviceInfo.getAttributes().containsKey("RC")) {
                    boolean isRCExists = new String(serviceInfo.getAttributes().get("RC"), StandardCharsets.UTF_8).contains("YES");
                    if (!isRCExists) {
                        tryToConnect(serviceInfo.getHost().getHostAddress());
                    } else {
                        ((FightActivity)activity).getViewModel().syncState.set(SYNC_STATE_NONE);
                    }
                }
            }
        };

        // Instantiate a new DiscoveryListener
        discoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.wtf("TAG", "Service discovery started");
                isDiscoveryActive = true;
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.wtf("TAG", "Service discovery success" + service);
//                if (!service.getServiceType().equals(SERVICE_TYPE)) {
//                    // Service type is the string containing the protocol and
//                    // transport layer for this service.
//                    Log.wtf("TAG", "Unknown Service Type: " + service.getServiceType());
//                } else if (service.getServiceName().equals(serviceName)) {
//                    // The name of the service tells the user what they'd be
//                    // connecting to. It could be "Bob's Chat App".
//                    Log.wtf("TAG", "Same machine: " + serviceName);
//                } else
                if (service.getServiceName().contains("Inspiration")){
                    nsdManager.resolveService(service, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.wtf("TAG", "service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.wtf("TAG", "Discovery stopped: " + serviceType);
                isDiscoveryActive = false;
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.wtf("TAG", "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.wtf("TAG", "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };
        nsdManager.discoverServices(
                "_ip_top._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void stopDiscovery() {
        if (isDiscoveryActive) {
            nsdManager.stopServiceDiscovery(discoveryListener);
        }
    }

    public void showInLog(String text) {
//        if (activity != null) {
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ((FightActivity) activity).getBinding().syncLay.tvLog.setMovementMethod(new ScrollingMovementMethod());
//                    final Layout layout = ((FightActivity) activity).getBinding().syncLay.tvLog.getLayout();
//                    if (layout != null) {
//                        int scrollDelta = layout.getLineBottom(((FightActivity) activity).getBinding().syncLay.tvLog.getLineCount() - 1)
//                                - ((FightActivity) activity).getBinding().syncLay.tvLog.getScrollY() - ((FightActivity) activity).getBinding().syncLay.tvLog.getHeight();
//                        if (scrollDelta > 0)
//                            ((FightActivity) activity).getBinding().syncLay.tvLog.scrollBy(0, scrollDelta);
//                    }
////                Log.wtf("LOG", activity.get().getViewModel().logTextTemp.get());
//                }
//            });
//            ((FightActivity) activity).getViewModel().logTextTemp.set(((FightActivity) activity).getViewModel().logTextTemp.get() + "\n " + text);
//        }
    }
}
