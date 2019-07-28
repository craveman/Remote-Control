package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class ResetPasswordRequest extends JsonSchema {
    
    @SerializedName("email")
    public String email;
    

    public ResetPasswordRequest(String email) {
        this.email = email;
    }
}
