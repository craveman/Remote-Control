package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetFightRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("fightId")
    public String fightId;
    

    public GetFightRequest(String session, String fightId) {
        this.session = session;this.fightId = fightId;
    }
}
