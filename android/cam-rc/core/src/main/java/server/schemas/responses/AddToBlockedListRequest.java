package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

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
