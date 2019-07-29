package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetAddressByLocationRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("latitude")
    public Number latitude;
    
    @SerializedName("longitude")
    public Number longitude;
    

    public GetAddressByLocationRequest(String session, Number latitude, Number longitude) {
        this.session = session;this.latitude = latitude;this.longitude = longitude;
    }
}
