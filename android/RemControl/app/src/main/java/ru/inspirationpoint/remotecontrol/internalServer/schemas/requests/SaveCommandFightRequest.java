package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class SaveCommandFightRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("commandFight")
    public CommandFightInput commandFight;

    public SaveCommandFightRequest(String session, CommandFightInput commandFight) {
        this.session = session;
        this.commandFight = commandFight;
    }
}
