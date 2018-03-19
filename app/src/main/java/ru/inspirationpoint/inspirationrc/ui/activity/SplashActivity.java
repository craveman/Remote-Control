package ru.inspirationpoint.inspirationrc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.DataManager;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.helpers.JSONHelper;
import server.schemas.requests.FightInput;
import server.schemas.responses.SaveFightResult;

public class SplashActivity extends LocalAppCompatActivity {

    private static final long PROCEED_DELAY_MILLIS = 2000;

    private View mContentView;

    private Handler mHandler = new Handler();
    private boolean mTimeIsOut = false;
    private Runnable mProceedRunnable = new Runnable() {
        @Override
        public void run() {
            mTimeIsOut = true;
            checkReadyToLaunch();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        SettingsManager.setValue(Constants.CURRENT_PAIR, 21);

        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ArrayList<FightInput> cachedFights = (ArrayList<FightInput>) JSONHelper.importFromJSON(this, JSONHelper.ItemClass.FightInput);

        if (cachedFights != null) {
            final ArrayList<FightInput> cacheTemp = (ArrayList<FightInput>) cachedFights.clone();
            for (final FightInput input : cachedFights) {
                DataManager.instance().saveFight(input, new DataManager.RequestListener<SaveFightResult>() {

                    @Override
                    public void onSuccess(SaveFightResult result) {
                        Log.d("FIGHT UPLOADED", input.address + input.date);
                        cacheTemp.remove(input);
                    }

                    @Override
                    public void onFailed(String error, String message) {
                        Log.d("FIGHT FAIL TO UPL", input.address + input.date);
                    }

                    @Override
                    public void onStateChanged(boolean inProgress) {

                    }
                });

            }
            cachedFights = cacheTemp;
        }

        JSONHelper.exportToJSON(this, cachedFights);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mHandler.postDelayed(mProceedRunnable, PROCEED_DELAY_MILLIS);
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mProceedRunnable);

        super.onPause();
    }

    private synchronized void checkReadyToLaunch() {
        if (mTimeIsOut) {
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        finish();

        if (DataManager.instance().isLoggedIn()) {
            Intent intent = new Intent(this, NewFightActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
