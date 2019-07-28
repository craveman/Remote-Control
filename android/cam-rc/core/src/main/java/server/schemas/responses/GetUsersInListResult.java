package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetUsersInListResult {
    @SerializedName("users")
    public ListUser[] users;
    
}
