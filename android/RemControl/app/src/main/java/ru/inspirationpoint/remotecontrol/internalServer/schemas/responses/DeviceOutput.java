package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class DeviceOutput {
    
    @SerializedName("clientId")
    public String clientId;
    
    @SerializedName("cameraId")
    public String cameraId;
    
    @SerializedName("created")
    public String created;
    
    @SerializedName("modified")
    public String modified;
    
    @SerializedName("_id")
    public String _id;
    
    @SerializedName("pushId")
    public String pushId;
    
    @SerializedName("platform")
    public String platform;
    
    @SerializedName("isDebug")
    public Boolean isDebug;
    
}
