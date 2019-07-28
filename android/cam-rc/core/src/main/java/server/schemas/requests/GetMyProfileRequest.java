package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetMyProfileRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    

    public GetMyProfileRequest(String session) {
        this.session = session;
    }
}
