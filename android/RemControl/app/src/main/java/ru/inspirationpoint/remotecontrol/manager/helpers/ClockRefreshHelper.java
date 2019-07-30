package ru.inspirationpoint.remotecontrol.manager.helpers;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Date;

public class ClockRefreshHelper {

    private Handler h = null;
    private int pingCounter = 0;
    private AppCompatActivity activity;
    private TextView view;

    public ClockRefreshHelper(AppCompatActivity activity, TextView view) {
        this.activity = activity;
        this.view = view;
    }

    public void startRefresh() {
        synchronized (this) {
            if (h == null) {
                HandlerThread thread = new HandlerThread("ping_thread");
                thread.start();
                h = new Handler(thread.getLooper());
                h.post(callback);
            }
        }
    }

    public void finishRefresh() {
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
            synchronized (this) {
                if (h != null) {
                    h.postDelayed(this, 1000L);
                }
            }
        }

        void method() {
            pingCounter++;
            if (pingCounter % 60 == 0) {
                pingCounter = 0;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Date date = new Date();
                    view.setText(Helper.timeToString(date));
                }
            });
        }
    };
}
