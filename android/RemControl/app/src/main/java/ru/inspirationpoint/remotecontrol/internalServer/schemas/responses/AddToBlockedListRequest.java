package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class AddToBlockedListRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("peerClientId")
    public String peerClientId;

    public AddToBlockedListRequest(String session, String peerClientId) {
        this.session = session;
        this.peerClientId = peerClientId;
    }
}
