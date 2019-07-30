package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetMyTrainingsRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("searchString")
    public String searchString;
    

    public GetMyTrainingsRequest(String session, String searchString) {
        this.session = session;this.searchString = searchString;
    }
}
