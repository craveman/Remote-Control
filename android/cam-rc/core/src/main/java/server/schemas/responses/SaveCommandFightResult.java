package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class SaveCommandFightResult {

    @SerializedName("commandFight")
    public CommandFightOutput commandFight;
}
