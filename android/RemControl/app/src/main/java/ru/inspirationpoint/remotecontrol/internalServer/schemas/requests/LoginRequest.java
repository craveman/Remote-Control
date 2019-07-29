package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class LoginRequest extends JsonSchema {
    
    @SerializedName("email")
    public String email;
    
    @SerializedName("password")
    public String password;
    

    public LoginRequest(String email, String password) {
        this.email = email;this.password = password;
    }
}
