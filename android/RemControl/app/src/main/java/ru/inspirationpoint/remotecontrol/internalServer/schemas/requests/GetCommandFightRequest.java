package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

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
