package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetMyProfileRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    

    public GetMyProfileRequest(String session) {
        this.session = session;
    }
}
