package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetMyProfileResult {
    @SerializedName("profile")
    public Profile profile;
    
}
