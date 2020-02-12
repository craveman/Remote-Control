package ru.inspirationpoint.remotecontrol.manager.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.Objects;

import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_DETACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static android.hardware.usb.UsbManager.EXTRA_ACCESSORY;
import static android.hardware.usb.UsbManager.EXTRA_DEVICE;

public class UsbBroadcastReceiver extends BroadcastReceiver {

    private OnUSBEventListener listener;
    public static final String ACTION_USB_PERMISSION =
            "ru.inspirationpoint.remotecontrol.manager.usb.USB_PERMISSION";

    public UsbBroadcastReceiver(OnUSBEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())) {

            case ACTION_USB_DEVICE_ATTACHED:
                listener.onUsbPermission(intent.getParcelableExtra(EXTRA_DEVICE));
                break;
            case ACTION_USB_PERMISSION:
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    UsbAccessory accessory = intent.getParcelableExtra(EXTRA_ACCESSORY);
                    UsbDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
                    if (device != null) {
                        listener.onUsbReady(intent.getParcelableExtra(UsbManager.EXTRA_DEVICE));
                    } else if (accessory != null) {
                        listener.onAccessory(accessory);
                    }
                }
                break;
            case ACTION_USB_ACCESSORY_ATTACHED:
                listener.onUsbAccessoryPermission(intent.getParcelableExtra(EXTRA_ACCESSORY));
                break;
            case ACTION_USB_DEVICE_DETACHED:
                listener.onUsbDetached();
                break;
            case ACTION_USB_ACCESSORY_DETACHED:
                listener.onUsbDetached();
                break;

        }
    }

    public interface OnUSBEventListener {
        void onUsbPermission(UsbDevice device);
        void onUsbAccessoryPermission(UsbAccessory accessory);
        void onUsbDetached();
        void onUsbReady(UsbDevice device);
        void onAccessory(UsbAccessory accessory);
    }
}
