package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

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
