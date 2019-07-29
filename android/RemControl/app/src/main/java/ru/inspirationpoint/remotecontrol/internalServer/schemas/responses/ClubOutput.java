package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class ClubOutput {
    
    @SerializedName("created")
    public String created;
    
    @SerializedName("modified")
    public String modified;
    
    @SerializedName("_id")
    public String _id;
    
    @SerializedName("name")
    public String name;
    
    @SerializedName("address")
    public String address;
    
}
