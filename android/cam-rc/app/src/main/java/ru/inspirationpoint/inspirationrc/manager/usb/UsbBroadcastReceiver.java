package ru.inspirationpoint.inspirationrc.manager.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ru.inspirationpoint.inspirationrc.manager.usb.UsbService.ACTION_USB_READY;

public class UsbBroadcastReceiver extends BroadcastReceiver {

    private OnUSBEventListener listener;

    public UsbBroadcastReceiver(OnUSBEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())) {
            case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                Log.wtf("USB GRANTED", "IN UBR");
                listener.onUsbPermission(true);
                break;
            case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                listener.onUsbPermission(false);
                break;
            case UsbService.ACTION_NO_USB: // NO USB CONNECTED

                break;
            case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED

                break;
            case ACTION_USB_DEVICE_DETACHED:
                listener.onUsbDetached();
                break;
            case ACTION_USB_READY:
                listener.onUsbReady();
                break;
        }
    }

    public interface OnUSBEventListener {
        void onUsbPermission(boolean granted);
        void onUsbDisconnected();
        void onUsbDetached();
        void onUsbReady();
    }
}
