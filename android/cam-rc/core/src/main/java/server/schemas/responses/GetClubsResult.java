package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetClubsResult {
    @SerializedName("clubs")
    public ClubOutput[] clubs;
    
}
