package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class RemoveUserFromFriendsRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("userId")
    public String userId;

    public RemoveUserFromFriendsRequest(String session, String userId) {
        this.session = session;
        this.userId = userId;
    }
}
