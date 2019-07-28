package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

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
