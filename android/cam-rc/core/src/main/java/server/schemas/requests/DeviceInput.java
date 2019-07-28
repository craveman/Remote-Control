package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

public class DeviceInput {
    
    @SerializedName("_id")
    public String _id;
    
    @SerializedName("pushId")
    public String pushId;
    
    @SerializedName("platform")
    public String platform;
    
    @SerializedName("isDebug")
    public Boolean isDebug;
    
}
