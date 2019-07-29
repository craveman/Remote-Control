package ru.inspirationpoint.remotecontrol.manager;

import server.schemas.requests.FightAction;

public interface ActionUploadCallback {

    void onUpload(FightAction action);
    void onRefresh(String id);
}
