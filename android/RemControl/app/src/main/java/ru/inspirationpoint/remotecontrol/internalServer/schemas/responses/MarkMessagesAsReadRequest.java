package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class MarkMessagesAsReadRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("dialogId")
    public String dialogId;

    @SerializedName("peerClientId")
    public String peerClientId;

    @SerializedName("startTime")
    public String startTime;

    @SerializedName("endTime")
    public String endTime;

    public MarkMessagesAsReadRequest(String session, String dialogId, String peerClientId) {
        this.session = session;
        this.dialogId = dialogId;
        this.peerClientId = peerClientId;
    }
}
