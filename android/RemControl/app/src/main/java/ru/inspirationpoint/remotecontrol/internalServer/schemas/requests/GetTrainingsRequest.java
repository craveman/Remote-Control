package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetTrainingsRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("fighterId")
    public String fighterId;

    @SerializedName("ownerId")
    public String ownerId;
    
    @SerializedName("searchString")
    public String searchString;
    

    public GetTrainingsRequest(String session, String fighterId, String ownerId, String searchString) {
        this.ownerId = ownerId;this.session = session;this.fighterId = fighterId;this.searchString = searchString;
    }
}
