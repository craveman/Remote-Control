package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class SendMessageRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("peerClientId")
    public String peerClientId;

    @SerializedName("text")
    public String text;

    public SendMessageRequest(String session, String peerClientId, String text) {
        this.session = session;
        this.peerClientId = peerClientId;
        this.text = text;
    }
}
