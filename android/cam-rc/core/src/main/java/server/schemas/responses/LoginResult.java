package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class LoginResult {
    @SerializedName("session")
    public String session;
    @SerializedName("version")
    public String version;
    @SerializedName("profile")
    public Profile profile;
    
}
