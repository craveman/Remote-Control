package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class LogoutRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    

    public LogoutRequest(String session) {
        this.session = session;
    }
}
