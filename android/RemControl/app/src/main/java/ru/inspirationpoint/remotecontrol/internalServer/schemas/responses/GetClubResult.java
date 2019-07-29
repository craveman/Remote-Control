package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetClubResult {
    @SerializedName("club")
    public ClubOutput club;
    
}
