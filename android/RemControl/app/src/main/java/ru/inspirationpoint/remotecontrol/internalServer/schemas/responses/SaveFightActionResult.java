package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class SaveFightActionResult {

    @SerializedName("fight")
    public FightOutput fight;
}
