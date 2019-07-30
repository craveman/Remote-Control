package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetMyFightsInTrainingRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("date")
    public String date;
    
    @SerializedName("address")
    public String address;
    

    public GetMyFightsInTrainingRequest(String session, String date, String address) {
        this.session = session;this.date = date;this.address = address;
    }
}
