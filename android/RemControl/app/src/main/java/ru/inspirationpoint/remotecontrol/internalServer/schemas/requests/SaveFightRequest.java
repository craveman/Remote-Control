package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class SaveFightRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("fight")
    public FightInput fight;
    

    public SaveFightRequest(String session, FightInput fight) {
        this.session = session;this.fight = fight;
    }
}
