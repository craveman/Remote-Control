package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClientInfo implements Serializable {

    @SerializedName("clientId")
    public String clientId;

    @SerializedName("name")
    public String name;

    @SerializedName("nick")
    public String nick;
}
