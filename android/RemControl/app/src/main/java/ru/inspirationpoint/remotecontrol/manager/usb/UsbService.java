package ru.inspirationpoint.remotecontrol.manager.usb;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.felhr.usbserial.CDCSerialDevice;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import ru.inspirationpoint.inspirationrc.rc.ui.activity.LoginActivity;

import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;

public class UsbService extends Service {

    public static final String ACTION_USB_READY = "com.felhr.connectivityservices.USB_READY";
    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_USB_NOT_SUPPORTED = "com.felhr.usbservice.USB_NOT_SUPPORTED";
    public static final String ACTION_NO_USB = "com.felhr.usbservice.NO_USB";
    public static final String ACTION_USB_PERMISSION_GRANTED = "com.felhr.usbservice.USB_PERMISSION_GRANTED";
    public static final String ACTION_USB_PERMISSION_NOT_GRANTED = "com.felhr.usbservice.USB_PERMISSION_NOT_GRANTED";
    public static final String ACTION_CDC_DRIVER_NOT_WORKING = "com.felhr.connectivityservices.ACTION_CDC_DRIVER_NOT_WORKING";
    public static final String ACTION_USB_DEVICE_NOT_WORKING = "com.felhr.connectivityservices.ACTION_USB_DEVICE_NOT_WORKING";
    public static final int MESSAGE_FROM_SERIAL_PORT = 0;
    public static final int CTS_CHANGE = 1;
    public static final int DSR_CHANGE = 2;
    public static final int SYNC_READ = 3;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int BAUD_RATE = 115200; // BaudRate. Change this value if you need
    public static boolean SERVICE_CONNECTED = false;

    private IBinder binder = new UsbBinder();

    private Context context;
    private UsbManager usbManager;

    private ConnectionListener listener;
    private  UsbSerialDevice serialPort;
    private UsbDevice device;

    public static String getActionUsbReady() {
        return ACTION_USB_READY;
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (Objects.requireNonNull(arg1.getAction())) {
                case ACTION_USB_PERMISSION:
                    boolean granted = arg1.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        UsbDevice currentDevice = arg1.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        Log.wtf("USB GRANTED", "IN SERVICE" + "|" + currentDevice.getProductId());
                        Intent intent = new Intent(ACTION_USB_PERMISSION_GRANTED);
                        arg0.sendBroadcast(intent);
                        ConnectionThread connectionThread = new ConnectionThread(currentDevice);
                        connectionThread.start();
                    } else // User not accepted our USB connection. Send an Intent to the Main Activity
                    {
                        Intent intent = new Intent(ACTION_USB_PERMISSION_NOT_GRANTED);
                        arg0.sendBroadcast(intent);
                    }
                    break;
                case ACTION_USB_DEVICE_ATTACHED:
                    findSerialPortDevice();
                    break;
                case ACTION_USB_ACCESSORY_ATTACHED:
                    findSerialPortDevice();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        this.context = this;
        UsbService.SERVICE_CONNECTED = true;
        setFilter();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        findSerialPortDevice();
        Notification notification = null;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "111");
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("111", "My channel",
                        NotificationManager.IMPORTANCE_MIN);
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId("111");
                notification = builder.build();
            } else {
                Intent notificationIntent = new Intent(this, LoginActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                notification = new NotificationCompat.Builder(this)
                        .setContentIntent(pendingIntent).build();
            }
        startForeground(1337, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SERVICE_CONNECTED = false;
        listener.onServiceStopped();
    }

    public void setConnectionListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public void onUSBDetached() {
        //TODO rescan devices and correct data according to it
    }

    public void write(byte[] data) {
        serialPort.write(data);
    }

    private void findSerialPortDevice() {
        Log.wtf("FIND SERIAL PORT", "++");
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        UsbAccessory[] usbAccessories = usbManager.getAccessoryList();
        Log.wtf("ACCESSORIES", Arrays.toString(usbAccessories));
        Log.wtf("DEVICES", usbDevices.toString());
        if (!usbDevices.isEmpty()) {
            //TODO wrap in worker queue
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {

                UsbDevice tempDevice = entry.getValue();
                int deviceVID = tempDevice.getVendorId();
                int devicePID = tempDevice.getProductId();
                Log.wtf("DEVICE CYCLE", devicePID + "|" + deviceVID);
                if (deviceVID != 0x1d6b &&
                        (devicePID != 0x0001 && devicePID != 0x0002 && devicePID != 0x0003) &&
                        deviceVID != 0x5c6 && devicePID != 0x904c &&
                        (devicePID != 33107 && deviceVID != 3034)) {
                    Log.wtf("DEVICE", devicePID + "|" + deviceVID);
                    requestUserPermission(tempDevice);
                }
            }
        } else {
            Intent intent = new Intent(ACTION_NO_USB);
            sendBroadcast(intent);
        }
    }

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(ACTION_USB_ACCESSORY_ATTACHED);
        registerReceiver(usbReceiver, filter);
    }

    /*
     * Request user permission. The response will be received in the BroadcastReceiver
     */
    private void requestUserPermission(UsbDevice tempDevice) {
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        usbManager.requestPermission(tempDevice, mPendingIntent);
    }

    public class UsbBinder extends Binder {
        public UsbService getService() {
            return UsbService.this;
        }
    }

    private class ConnectionThread extends Thread {

        private UsbDevice device;

        public ConnectionThread(UsbDevice currDevice) {
            this.device = currDevice;
        }

        @Override
        public void run() {
            UsbSerialDevice serialPort = UsbSerialDevice.createUsbSerialDevice(device, usbManager.openDevice(device));
            if (serialPort != null) {
                if (serialPort.open()) {
                    serialPort.setBaudRate(BAUD_RATE);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_DSR_DTR);
                    serialPort.read(data -> listener.onUsbMessageReceived(data));
                    UsbService.this.device = device;
                    UsbService.this.serialPort = serialPort;
                    listener.onConnectionEstablished(device);

                    Intent intent = new Intent(ACTION_USB_READY);
                    context.sendBroadcast(intent);
                } else {
                    if (serialPort instanceof CDCSerialDevice) {
                        Intent intent = new Intent(ACTION_CDC_DRIVER_NOT_WORKING);
                        context.sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(ACTION_USB_DEVICE_NOT_WORKING);
                        context.sendBroadcast(intent);
                    }
                }
            } else {
                // No driver for given tempDevice, even generic CDC driver could not be loaded
                Intent intent = new Intent(ACTION_USB_NOT_SUPPORTED);
                context.sendBroadcast(intent);
            }
        }
    }

    public interface ConnectionListener {
        void onConnectionEstablished(UsbDevice device);

        void onUsbMessageReceived(byte[] message);

        void onConnectionLost(UsbDevice device);

        void onServiceStopped();
    }
}
