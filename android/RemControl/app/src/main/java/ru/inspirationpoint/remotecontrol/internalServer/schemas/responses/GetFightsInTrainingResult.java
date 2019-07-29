package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetFightsInTrainingResult {
    @SerializedName("fights")
    public FightOutput[] fights;
}
