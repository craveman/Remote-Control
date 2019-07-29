package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetBlockedUsersResult {

    @SerializedName("peerClients")
    public ClientInfo[] peerClients;
}
