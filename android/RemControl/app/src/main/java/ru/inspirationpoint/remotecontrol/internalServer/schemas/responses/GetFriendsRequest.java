package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetFriendsRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    public GetFriendsRequest(String session) {
        this.session = session;
    }
}
