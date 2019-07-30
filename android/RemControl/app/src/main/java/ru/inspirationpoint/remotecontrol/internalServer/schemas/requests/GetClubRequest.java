package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetClubRequest extends JsonSchema {
    
    @SerializedName("clubId")
    public String clubId;
    

    public GetClubRequest(String clubId) {
        this.clubId = clubId;
    }
}
