package ru.inspirationpoint.remotecontrol.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.cloudManager.CloudRequestManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.helpers.PermissionHelper;
import ru.inspirationpoint.remotecontrol.ui.dialog.MessageDialog;


public class StartActivityVM extends ActivityViewModel<StartActivity> {

    private static final long PROCEED_DELAY_MILLIS = 2000;

    public static final int LOCATION_REQUEST = 22;

    private final Handler mHandler = new Handler();
    private boolean mTimeIsOut = false;
    private final Runnable mProceedRunnable = new Runnable() {
        @Override
        public void run() {
            mTimeIsOut = true;
            checkReadyToLaunch();
        }
    };

    public StartActivityVM(StartActivity activity) {
        super(activity);
        CloudRequestManager.login();
//        LocaleHelper.setLocale(getActivity(), "en");
        SettingsManager.setValue(CommonConstants.LOCALE_CHANGED_FIELD, true);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //TODO cache handle
//        ArrayList<FightInput> cachedFights = (ArrayList<FightInput>) JSONHelper.importFromJSON(activity, JSONHelper.ItemClass.FightInput);
//        Log.wtf("CACHED BEGIN", (cachedFights != null ? cachedFights.size() : 0) + "");
//
//        if (cachedFights != null) {
//            final ArrayList<FightInput> cacheTemp = (ArrayList<FightInput>) cachedFights.clone();
//            for (final FightInput input : cachedFights) {
//                DataManager.instance().saveFight(input, new DataManager.RequestListener<SaveFightResult>() {
//
//                    @Override
//                    public void onSuccess(SaveFightResult result) {
//                        Log.d("FIGHT UPLOADED", input.address + input.date);
//                        cacheTemp.remove(input);
//                    }
//
//                    @Override
//                    public void onFailed(String error, String message) {
//                        Log.d("FIGHT FAIL TO UPL", input.address + input.date + "|" + error);
//                    }
//
//                    @Override
//                    public void onStateChanged(boolean inProgress) {
//
//                    }
//                });
//                Log.wtf("CACHED REMAIN", cacheTemp.size() + "");
//            }
//            cachedFights = cacheTemp;
//        }
//
//        JSONHelper.exportToJSON(activity, cachedFights);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.postDelayed(mProceedRunnable, PROCEED_DELAY_MILLIS);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mProceedRunnable);

        super.onPause();
    }

    private synchronized void checkReadyToLaunch() {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (mTimeIsOut) {
            if (!PermissionHelper.hasCameraPermission(getActivity()) || !PermissionHelper.hasWriteStoragePermission(getActivity())) {
                PermissionHelper.requestCameraPermission(getActivity(), true);
            } else if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                MessageDialog.show(getActivity(), LOCATION_REQUEST, activity.getResources().getString(R.string.gps_off_title),
                        activity.getResources().getString(R.string.gps_off_message));
            } else {
                goToMainActivity();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                MessageDialog.show(getActivity(), LOCATION_REQUEST, activity.getResources().getString(R.string.gps_off_title),
                        activity.getResources().getString(R.string.gps_off_message));
            } else {
                goToMainActivity();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        goToMainActivity();
    }

    private void goToMainActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(getActivity().getApplicationContext())) {
                finish();
    //        LocaleHelper.setLocale(getActivity(), "en");
                SettingsManager.setValue(CommonConstants.LOCALE_CHANGED_FIELD, true);

    //        if (DataManager.instance().isLoggedIn()) {
                Intent intent = new Intent(getActivity(), FightActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
    //        } else {
    //            Intent intent = new Intent(getActivity(), LoginActivity.class);
    //            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    //            getActivity().startActivity(intent);
    //        }
            } else {
                Intent goToSettings = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                goToSettings.setData(Uri.parse("package:" + getActivity().getApplicationContext().getPackageName()));
                getActivity().startActivityForResult(goToSettings, 1);
            }
        }
    }

}