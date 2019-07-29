package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class RegisterResult {
    @SerializedName("version")
    public String version;
    @SerializedName("profile")
    public Profile profile;
    
}
