package ru.inspirationpoint.remotecontrol.manager.handlers.EthernetCommandsHelpers;

import android.os.Handler;
import android.os.HandlerThread;

import ru.inspirationpoint.inspirationrc.manager.constants.commands.EthernetFinishAskCommand;
import ru.inspirationpoint.inspirationrc.manager.handlers.CoreHandler;

public class FightFinishAskHandler {

    public FightFinishAskHandler(CoreHandler core) {
        this.core = core;
    }

    private static final long DELAY = 700L;
    private Handler h = null;
    private CoreHandler core;

    public void start() {
        synchronized (this) {
            if (h == null) {
                HandlerThread thread = new HandlerThread("fin_ask_thread");
                thread.start();
                h = new Handler(thread.getLooper());
                h.post(callback);
            }
            else {
                h.post(callback);
            }
        }
    }

    public void pause() {
        synchronized (this) {
            if (h != null) {
                h.removeCallbacks(callback);
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
            synchronized (FightFinishAskHandler.this) {
                method();
                if (h != null) {
                    h.postDelayed(this, DELAY);
                }
            }
        }

        void method() {
            core.sendToSM(new EthernetFinishAskCommand().getBytes());
        }
    };
}
