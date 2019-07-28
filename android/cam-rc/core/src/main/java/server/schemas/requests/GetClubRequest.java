package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetClubRequest extends JsonSchema {
    
    @SerializedName("clubId")
    public String clubId;
    

    public GetClubRequest(String clubId) {
        this.clubId = clubId;
    }
}
