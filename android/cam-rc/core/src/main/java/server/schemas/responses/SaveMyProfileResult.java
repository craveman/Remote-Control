package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class SaveMyProfileResult {
    @SerializedName("profile")
    public Profile profile;
    
}
