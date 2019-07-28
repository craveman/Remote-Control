package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class RemoveUserFromFriendsRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("userId")
    public String userId;

    public RemoveUserFromFriendsRequest(String session, String userId) {
        this.session = session;
        this.userId = userId;
    }
}
