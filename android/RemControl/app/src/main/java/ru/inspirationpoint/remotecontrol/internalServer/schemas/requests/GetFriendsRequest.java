package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetFriendsRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    public GetFriendsRequest(String session) {
        this.session = session;
    }
}
