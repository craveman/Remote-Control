package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetFightRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("fightId")
    public String fightId;
    

    public GetFightRequest(String session, String fightId) {
        this.session = session;this.fightId = fightId;
    }
}
