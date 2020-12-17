package ru.inspirationpoint.remotecontrol.manager.handlers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import ru.inspirationpoint.remotecontrol.manager.tcpHandle.CommandHelper;
import ru.inspirationpoint.remotecontrol.manager.usb.UsbBroadcastReceiver;

import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_DETACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.AUTH_RESPONSE;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.CARD_STATUS_RED;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_LEFT;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PING_OUT;
import static ru.inspirationpoint.remotecontrol.manager.usb.UsbBroadcastReceiver.ACTION_USB_PERMISSION;

public class UsbConnectionHandler implements UsbBroadcastReceiver.OnUSBEventListener, Runnable{

    private final Context context;
    private final CoreHandler core;
    private final UsbBroadcastReceiver receiver;
    private final IntentFilter mIntentFilter;
    private final UsbManager mUsbManager;
    private boolean mRun = false;
    UsbEndpoint mOutEndpoint = null;
    UsbEndpoint mInEndpoint = null;
    UsbDeviceConnection connection2 = null;
    Thread usbRcThread;
    ParcelFileDescriptor fileDescriptor;
    FileInputStream inputStream;
    FileOutputStream outputStream;
    private static final int BUFSIZ = 4096;
    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFSIZ);


    public UsbConnectionHandler(Context context, CoreHandler core) {
        this.context = context;
        this.core = core;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        receiver = new UsbBroadcastReceiver(this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_USB_PERMISSION);
        mIntentFilter.addAction(ACTION_USB_DEVICE_DETACHED);
        mIntentFilter.addAction(ACTION_USB_ACCESSORY_ATTACHED);
        mIntentFilter.addAction(ACTION_USB_DEVICE_ATTACHED);
        mIntentFilter.addAction(ACTION_USB_ACCESSORY_DETACHED);
        context.registerReceiver(receiver, mIntentFilter);
        Log.wtf("HANDLER CREATED", "+");
        if (mUsbManager.getAccessoryList() != null ) {
            if (mUsbManager.getAccessoryList().length != 0) {
                openAccessoryConnection(mUsbManager.getAccessoryList()[0]);
            }
        }
    }

    @Override
    public void onUsbPermission(UsbDevice device) {
//        core.showInLog("USB DETECTED");
        if (!mUsbManager.hasPermission(device)) {
//            core.showInLog("NO PERM");
            mUsbManager.requestPermission(device, PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0));
        } else {
//            core.showInLog("HAS PERM");
            onUsbReady(device);
        }
    }

    @Override
    public void onUsbAccessoryPermission(UsbAccessory accessory) {
        if (!mUsbManager.hasPermission(accessory)) {
//            core.showInLog("NO PERM");
            mUsbManager.requestPermission(accessory, PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0));
        } else {
//            core.showInLog("HAS PERM");
            onAccessory(accessory);
        }
    }

    @Override
    public void onUsbDetached() {
        stopPing();
        core.isUSBMode.set(false);
    }

    public void openAccessoryConnection(UsbAccessory accessory) {
        mUsbManager.requestPermission(accessory, PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0));
    }

    @Override
    public void onUsbReady(UsbDevice device) {
//        core.showInLog("USB READY");
    }

    @Override
    public void onAccessory(UsbAccessory accessory) {
//        core.showInLog("ACCESSORY");
        fileDescriptor = mUsbManager.openAccessory(accessory);
//        core.showInLog((fileDescriptor != null) + "||");
        if (fileDescriptor != null) {
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
            startPing();
            core.isUSBMode.set(true);
        }
    }

    public void stopPing(){
        mRun = false;
        try {
            inputStream.close();
            outputStream.close();
            fileDescriptor.close();
        } catch (IOException e) {
            Log.wtf("CLOSING EXCEPTION", e.getLocalizedMessage());
        }
        usbRcThread.interrupt();
    }

    public void startPing() {
        mRun = true;
        usbRcThread = new Thread(this);
        usbRcThread.start();
    }

    public void writeSMUsb(final byte[] message) {
            if (outputStream != null) {
                try {
                    if (message != null) {
                        if (message.length != 0) {
                            outputStream.write(message);
                            Log.wtf("WRITE ", Arrays.toString(message));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void run() {
        mRun = true;

        while (mRun) {
            if (inputStream!= null) {
                try {
                    int readed = inputStream.read(mReadBuffer.array());
                    if (readed > 3) {
                        byte[] header = new byte[4];
                        mReadBuffer.get(header);
                        byte command = header[2];
                        byte status = header[3];
                        byte length = header[1];
                        if (readed > 4) {
                            byte[] cmdBody = new byte[readed - 4];
                            mReadBuffer.get(cmdBody);
                            Log.wtf("REC USB", command + "   " + Arrays.toString(cmdBody));
                            core.onReceive(command, status, cmdBody);
                        } else {
                            core.onReceive(command, status, null);
                        }
                        mReadBuffer.clear();
                    }
                } catch (IOException e) {
                    Log.wtf("USB handler", e.getLocalizedMessage());
                }
            }
        }
    }
}
