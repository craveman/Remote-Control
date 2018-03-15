package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class LogoutRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    

    public LogoutRequest(String session) {
        this.session = session;
    }
}
