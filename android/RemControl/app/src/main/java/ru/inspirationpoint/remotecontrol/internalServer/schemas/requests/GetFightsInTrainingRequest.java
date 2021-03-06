package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetFightsInTrainingRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("fighterId")
    public String fighterId;
    
    @SerializedName("date")
    public String date;
    
    @SerializedName("address")
    public String address;
    

    public GetFightsInTrainingRequest(String session, String fighterId, String date, String address) {
        this.session = session;this.fighterId = fighterId;this.date = date;this.address = address;
    }
}
