package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

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
