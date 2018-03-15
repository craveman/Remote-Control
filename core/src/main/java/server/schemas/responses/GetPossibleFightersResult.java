package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetPossibleFightersResult {
    @SerializedName("fighters")
    public Fighter[] fighters;
    
}
