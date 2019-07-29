package ru.inspirationpoint.remotecontrol.manager.tcpHandle;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;

import ru.inspirationpoint.inspirationrc.manager.helpers.UDPHelper;

public class Sender {

    public Sender(UDPHelper udpHelper) {
        this.udp = udpHelper;
    }

    private static final long DELAY = 3000L;
    private Handler h = null;
    private UDPHelper udp;

    public void start() {
        synchronized (this) {
            if (h == null) {
                HandlerThread thread = new HandlerThread("ping_thread");
                thread.start();
                h = new Handler(thread.getLooper());
                h.post(callback);
            }
        }
    }

    public void finish() {
        synchronized (this) {
            if (h != null) {
                h.removeCallbacks(callback);
                h.getLooper().quit();
                h = null;
            }
        }
    }

    private Runnable callback = new Runnable() {
        @Override
        public void run() {
            Log.wtf("SENDER CALLBACK", "RUN");
            method();
            synchronized (Sender.this) {
                if (h != null) {
                    h.postDelayed(this, DELAY);
                }
            }
        }

        void method() {
            try {
                udp.sendBroadcast("PING");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
