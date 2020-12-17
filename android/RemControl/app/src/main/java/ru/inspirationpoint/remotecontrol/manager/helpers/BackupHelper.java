package ru.inspirationpoint.remotecontrol.manager.helpers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.manager.Camera;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.Device;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FullFightInfo;
import ru.inspirationpoint.remotecontrol.manager.tcpHandle.TCPHelper;

public class BackupHelper {

    private final Context context;

    public BackupHelper(Context context) {
        this.context = context;
    }

    public FullFightInfo createBackup(FightData data) {
        TCPHelper helper = InspirationDayApplication.getApplication().getHelper();
        FullFightInfo info = new FullFightInfo(data, null, null);
        if (helper != null) {
            info.setConnectedSM(new Device(helper.getServerIp(), helper.getCode()));
        }
        if (InspirationDayApplication.getApplication().getCameras() != null) {
            ArrayList<Device> cameras = new ArrayList<>();
            for (Camera camera : InspirationDayApplication.getApplication().getCameras()) {
                cameras.add(new Device(camera.ip.get(), 0));
            }
            info.setConnectedCams(cameras);
        }
        return info;
    }

    public void backupFight(FullFightInfo data) {
        JSONHelper.exportToJSON(context, data, "");
    }

    public void backupFight(FightData data) {
        JSONHelper.exportToJSON(context, data);
    }

    public void backupLastFight(FullFightInfo data, String appendix) {
        Log.wtf("BACKUP CALLED", appendix);
        Log.wtf("TO SAVE", data.getFightData().getOwner() + "|" +
                data.getFightData().getmCurrentPeriod() + "|" +
                data.getFightData().getLeftFighter().getScore() + "|" +
                data.getFightData().getRightFighter().getScore());
        JSONHelper.exportToJSON(context, data, appendix);
    }

    public FightData getBackup() {
        //TODO ??
//        if (data != null && data.getFightData()) {
//            data.getFightData().setId("");
//        }
        return JSONHelper.importFightFromJSON(context);
    }

    public void copyFight(String filename) {
        JSONHelper.copyFightToStorage(filename, context);
    }

    public void cleanBackup() {
        SettingsManager.removeValue(CommonConstants.LAST_FIGHT_ID);
        context.deleteFile("cached_fight.json");
    }

    public void restoreFightOnSm(FightData fightData) {
        TCPHelper helper = InspirationDayApplication.getApplication().getHelper();
        if (helper != null) {

        }
    }

    public void  restoreFightInner() {

    }
}
