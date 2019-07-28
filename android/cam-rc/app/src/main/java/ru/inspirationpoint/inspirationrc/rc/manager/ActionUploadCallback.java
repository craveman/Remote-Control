package ru.inspirationpoint.inspirationrc.rc.manager;

import server.schemas.requests.FightAction;

public interface ActionUploadCallback {

    void onUpload(FightAction action);
    void onRefresh(String id);
}
