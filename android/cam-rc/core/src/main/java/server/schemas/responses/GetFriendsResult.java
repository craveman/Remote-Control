package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetFriendsResult {
    @SerializedName("users")
    public ListUser[] users;
}
