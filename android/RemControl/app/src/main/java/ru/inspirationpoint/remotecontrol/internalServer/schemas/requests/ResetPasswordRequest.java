package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class ResetPasswordRequest extends JsonSchema {
    
    @SerializedName("email")
    public String email;
    

    public ResetPasswordRequest(String email) {
        this.email = email;
    }
}
