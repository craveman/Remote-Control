package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetPossibleUsersRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("searchString")
    public String searchString;

    @SerializedName("includeMe")
    public Boolean includeMe;

    public GetPossibleUsersRequest(String session, String searchString, Boolean includeMe) {
        this.session = session;
        this.searchString = searchString;
        this.includeMe = includeMe;
    }
}
