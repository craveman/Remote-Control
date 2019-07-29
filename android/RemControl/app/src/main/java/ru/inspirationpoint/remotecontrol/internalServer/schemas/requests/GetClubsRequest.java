package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetClubsRequest extends JsonSchema {
    
    @SerializedName("searchString")
    public String searchString;
    

    public GetClubsRequest(String searchString) {
        this.searchString = searchString;
    }
}
