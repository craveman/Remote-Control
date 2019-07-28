package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetMyFightsInTrainingResult {
    @SerializedName("fights")
    public FightOutput[] fights;
    
}
