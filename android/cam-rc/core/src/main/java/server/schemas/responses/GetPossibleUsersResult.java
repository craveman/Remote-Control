package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetPossibleUsersResult {
    @SerializedName("users")
    public ListUser[] users;
    
}
