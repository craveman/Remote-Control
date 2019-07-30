package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class SendMessageToDialogRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("dialogId")
    public String dialogId;

    @SerializedName("text")
    public String text;

    public SendMessageToDialogRequest(String session, String dialogId, String text) {
        this.session = session;
        this.dialogId = dialogId;
        this.text = text;
    }
}
