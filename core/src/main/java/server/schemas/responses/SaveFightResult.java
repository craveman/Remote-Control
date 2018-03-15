package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class SaveFightResult {
    @SerializedName("fight")
    public FightOutput fight;
    
}
