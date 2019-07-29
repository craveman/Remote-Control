package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetFightResult {
    @SerializedName("fight")
    public FightOutput fight;
    
}
