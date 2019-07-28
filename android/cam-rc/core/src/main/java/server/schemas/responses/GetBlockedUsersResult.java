package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetBlockedUsersResult {

    @SerializedName("peerClients")
    public ClientInfo[] peerClients;
}
