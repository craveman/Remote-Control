package ru.inspirationpoint.remotecontrol.manager;


import ru.inspirationpoint.remotecontrol.internalServer.schemas.requests.FightAction;

public interface ActionUploadCallback {

    void onUpload(FightAction action);
    void onRefresh(String id);
}
