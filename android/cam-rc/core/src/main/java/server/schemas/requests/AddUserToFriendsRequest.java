package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class AddUserToFriendsRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("userId")
    public String userId;

    public AddUserToFriendsRequest(String session, String userId) {
        this.session = session;
        this.userId = userId;
    }
}
