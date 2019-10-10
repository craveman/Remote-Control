package ru.inspirationpoint.remotecontrol.manager.handlers;

import android.os.Handler;
import android.os.HandlerThread;

public class SMMainAliveHandler {

    private static final long DELAY = 1000L;
    private Handler h = null;
    private CoreHandler core;
    private int remain = 6;

    public SMMainAliveHandler(CoreHandler core) {
        this.core = core;
    }

    public void start() {
        synchronized (this) {
            remain = 6;
            if (h == null) {
                HandlerThread thread = new HandlerThread("sm_alive_thread");
                thread.start();
                h = new Handler(thread.getLooper());
                h.postDelayed(callback, DELAY);
            }
            else {
                h.removeCallbacksAndMessages(null);
                h.postDelayed(callback, DELAY);
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
//            core.updateSMOnline(remain);
        }
    }

    private Runnable callback = new Runnable() {
        @Override
        public void run() {
            method();
            synchronized (SMMainAliveHandler.this) {
                if (h != null) {
                    h.postDelayed(this, DELAY);
                }
            }
        }

        void method() {
            core.updateSMAlive(remain);
            if (remain > 0) {
                remain -= 1;
            } else finish();
        }
    };
}
