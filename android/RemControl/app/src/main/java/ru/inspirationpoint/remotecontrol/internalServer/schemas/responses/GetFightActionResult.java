package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.requests.FightAction;

public class GetFightActionResult {

    @SerializedName("action")
    public FightAction action;
}
