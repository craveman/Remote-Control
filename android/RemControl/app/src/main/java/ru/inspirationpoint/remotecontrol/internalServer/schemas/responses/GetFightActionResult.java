package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import server.schemas.requests.FightAction;

public class GetFightActionResult {

    @SerializedName("action")
    public FightAction action;
}
