package ru.inspirationpoint.inspirationrc.manager.handlers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Arrays;
import java.util.Set;

import ru.inspirationpoint.inspirationrc.manager.usb.UsbBroadcastReceiver;
import ru.inspirationpoint.inspirationrc.manager.usb.UsbService;

public class UsbConnectionHandler implements UsbBroadcastReceiver.OnUSBEventListener, UsbService.ConnectionListener {

    private Context context;
    private UsbService usbService;
    private CoreHandler core;
    private UsbBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private Handler handler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setConnectionListener(UsbConnectionHandler.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    public UsbConnectionHandler(Context context, CoreHandler core) {
        this.context = context;
        this.core = core;
        receiver = new UsbBroadcastReceiver(this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        mIntentFilter.addAction(UsbService.ACTION_NO_USB);
        mIntentFilter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        mIntentFilter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        mIntentFilter.addAction(UsbService.ACTION_USB_READY);
        context.registerReceiver(receiver, mIntentFilter);
        Log.wtf("HANDLER CREATED", "+");
        core.setUsbHandler(this);
        startService(UsbService.class, usbConnection, null);
        handler = new Handler();
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(context, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(startService);
            } else {
                context.startService(startService);
            }
        }
        Intent bindingIntent = new Intent(context, service);
        context.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void writeUSB(byte[] data) {
        if (usbService != null) {
            usbService.write(data);
            Log.wtf("USB WRITE", Arrays.toString(data));
        }
    }

    @Override
    public void onUsbPermission(boolean granted) {
        if (granted) {
            Log.wtf("USB GRANTED", "IN CONN HANDLER");
//            startService(UsbService.class, usbConnection, null);
        }
    }

    @Override
    public void onUsbDisconnected() {
        stopPing();
        context.stopService(new Intent(context, UsbService.class));
    }

    @Override
    public void onUsbDetached() {
        stopPing();
        usbService.onUSBDetached();
    }

    @Override
    public void onUsbReady() {
        writeUSB(new byte[]{0x02, 0x03, 0x04});
    }

    public void stopPing(){
        handler.removeCallbacksAndMessages(null);
    }

//    public void startPingUsb() {
//        Log.wtf("START PING", "+");
//        Runnable runnableCode = new Runnable() {
//            @Override
//            public void run() {
//                if (usbService != null) {
//                    usbService.write(new SyncUSBCmd(core.getGoAddress() != null).getBytes());
//                }
//                handler.postDelayed(this, 100);
//            }
//        };
//        handler.post(runnableCode);
//    }

    @Override
    public void onConnectionEstablished(UsbDevice device) {
        core.onUsbConnected(device);
    }

    @Override
    public void onUsbMessageReceived(byte[] message) {
        core.receiveUsbMessage(message);
    }

    @Override
    public void onConnectionLost(UsbDevice device) {

    }

    @Override
    public void onServiceStopped() {
        core.refreshUSBData();
    }

}
