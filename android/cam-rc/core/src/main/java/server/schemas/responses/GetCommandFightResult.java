package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetCommandFightResult {

    @SerializedName("commandFight")
    public CommandFightOutput commandFight;
}
