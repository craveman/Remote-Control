package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetCommandFightRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("commandFightId")
    public String commandFightId;

    public GetCommandFightRequest(String session, String commandFightId) {
        this.session = session;
        this.commandFightId = commandFightId;
    }
}
