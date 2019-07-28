package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class GetMyTrainingsResult {
    @SerializedName("trainings")
    public Training[] trainings;
    
}
