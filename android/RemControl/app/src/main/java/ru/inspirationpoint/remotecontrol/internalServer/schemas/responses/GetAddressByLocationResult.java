package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetAddressByLocationResult {
    @SerializedName("address")
    public String address;
    
}
