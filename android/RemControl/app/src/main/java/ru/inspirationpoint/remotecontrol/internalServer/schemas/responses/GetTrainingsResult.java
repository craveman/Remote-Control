package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetTrainingsResult {
    @SerializedName("trainings")
    public Training[] trainings;
    
}
