package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class RemoveUserFromBlockedRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("peerClientId")
    public String peerClientId;

    public RemoveUserFromBlockedRequest(String session, String peerClientId) {
        this.session = session;
        this.peerClientId = peerClientId;
    }
}
