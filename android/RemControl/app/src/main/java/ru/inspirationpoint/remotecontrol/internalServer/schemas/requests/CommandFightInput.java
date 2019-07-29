package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

public class CommandFightInput {

    @SerializedName("_id")
    public String _id;

    @SerializedName("left")
    public Integer left;

    @SerializedName("right")
    public Integer right;

    @SerializedName("fightNumber")
    public Integer fightNumber;
}
