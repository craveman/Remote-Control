package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class SaveCommandFightResult {

    @SerializedName("commandFight")
    public CommandFightOutput commandFight;
}
