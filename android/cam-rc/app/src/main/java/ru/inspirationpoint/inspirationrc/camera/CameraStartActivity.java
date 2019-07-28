package ru.inspirationpoint.inspirationrc.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.camera.utils.PermissionHelper;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.helpers.UDPHelper;
import ru.inspirationpoint.inspirationrc.rc.ui.activity.LocalAppCompatActivity;
import ru.inspirationpoint.inspirationrc.rc.ui.activity.NewFightActivity;

import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.CAMERA_BEGIN_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.CAMERA_READY_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.FIGHT_START_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.PING_UDP;

public class CameraStartActivity extends LocalAppCompatActivity{

    private int selectedWidth = 1280;
    private int selectedHeight = 720;
    private UDPHelper udpHelper;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_start);
        configureToolbar();
        setCustomTitle(R.string.camera);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        drawer = findViewById(R.id.drawer_layout_trainings);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

                String[] data = {"640*480", "1280*720", "1920*1080", "2560*1440", "3840*2160"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        selectedWidth = 640;
                        selectedHeight = 480;
                        break;
                    case 1:
                        selectedWidth = 1280;
                        selectedHeight = 720;
                        break;
                    case 2:
                        selectedWidth = 1920;
                        selectedHeight = 1080;
                        break;
                    case 3:
                        selectedWidth = 2560;
                        selectedHeight = 1440;
                        break;
                    case 4:
                        selectedWidth = 3840;
                        selectedHeight = 2160;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
//        drawer.setVisibility(View.GONE);
//        navigationView.setVisibility(View.GONE);
//        spinner.setVisibility(View.GONE);
//        findViewById(R.id.tv_sync_holder).setVisibility(View.GONE);
//        findViewById(R.id.textView).setVisibility(View.GONE);
//        findViewById(R.id.spinner_container).setVisibility(View.GONE);
        udpHelper = new UDPHelper(this);
        udpHelper.setListener(new UDPHelper.BroadcastListener() {
            @Override
            public void onReceive(String[] msg, String ip) {
                Log.wtf("RECEIVED", "receive message " + msg[0] + " from " + ip);
                switch (msg[0]) {
                    case PING_UDP:
//                        if (TextUtils.isEmpty(InspirationDayApplication.getApplication().getRcIpForCam()) &&
//                        TextUtils.isEmpty(InspirationDayApplication.getApplication().getSmIpForCam())) {
                            new Thread(() -> {
                                try {
                                    udpHelper.sendTargetMessage(String.valueOf(InspirationDayApplication.getApplication().getCamId())
                                            + "\0VCAM", ip);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }).start();
//                        }
                        break;
                    case "STAT":
                        new Thread(() -> {
                            try {
                                udpHelper.sendTargetMessage(InspirationDayApplication.getApplication().getSmIpForCam(), ip);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                        break;
                    case "RESE":
                        InspirationDayApplication.getApplication().setSmIpForCam("");
                        InspirationDayApplication.getApplication().setRcIpForCam("");
                        if (InspirationDayApplication.getApplication().getHelper() != null) {
                            InspirationDayApplication.getApplication().endTcp();
                        }
                        break;
                    case CAMERA_BEGIN_UDP:
                        InspirationDayApplication.getApplication().setRcIpForCam(ip);
                        InspirationDayApplication.getApplication().setSmIpForCam(msg[1]);
                        new Thread(() -> {
                            try {
                                udpHelper.sendTargetMessage(CAMERA_READY_UDP + "\0" + msg[1],
                                        InspirationDayApplication.getApplication().getRcIpForCam());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                        break;
                    case FIGHT_START_UDP:
                        //TODO remove temp
                        SettingsManager.removeValue("REFMODE");
                        Intent intent = new Intent(CameraStartActivity.this, MainCameraActivity.class);
                        intent.putExtra("width", selectedWidth);
                        intent.putExtra("height", selectedHeight);
                        startActivity(intent);
                        finish();
                        break;
                    case "REFRST":
                        SettingsManager.setValue("REFMODE", msg[1]);
                        Log.wtf("CAMSTART", msg[1]);
                        break;
                }
            }

            @Override
            public void onCreated() {

            }
        });
        udpHelper.start();
        InspirationDayApplication.getApplication().setUdpHelper(udpHelper);
//        if (InspirationDayApplication.getApplication().getHelper() != null) {
//            InspirationDayApplication.getApplication().endTcp();
//        }
        findViewById(R.id.cam_id_tv).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.cam_id_tv)).setText(getResources().getString(R.string.cam_id,
                String.valueOf(InspirationDayApplication.getApplication().getCamId())));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!PermissionHelper.hasCameraPermission(this) || !PermissionHelper.hasWriteStoragePermission(this)) {
            PermissionHelper.requestCameraPermission(this, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.wtf("RESUME", "+");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!PermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this,
                    "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
            PermissionHelper.launchPermissionSettings(this);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, NewFightActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
