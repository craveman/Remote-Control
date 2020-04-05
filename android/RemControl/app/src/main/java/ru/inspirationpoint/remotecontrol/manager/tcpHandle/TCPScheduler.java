package ru.inspirationpoint.remotecontrol.manager.tcpHandle;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import ru.inspirationpoint.remotecontrol.manager.handlers.CoreHandler;

public class TCPScheduler {

    private static final long DELAY = 2000L;
    private Handler h = null;
    private CoreHandler core;

    public TCPScheduler(CoreHandler core) {
        this.core = core;
    }

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
            method();
            synchronized (TCPScheduler.this) {
                if (h != null) {
                    h.postDelayed(this, DELAY);
                }
            }
        }

        void method() {
            core.sendToSM(CommandHelper.ping());
        }
    };
}
