package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class SaveFightRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("fight")
    public FightInput fight;
    

    public SaveFightRequest(String session, FightInput fight) {
        this.session = session;this.fight = fight;
    }
}
