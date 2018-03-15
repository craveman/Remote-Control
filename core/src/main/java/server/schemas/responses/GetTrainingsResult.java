package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetTrainingsResult {
    @SerializedName("trainings")
    public Training[] trainings;
    
}
