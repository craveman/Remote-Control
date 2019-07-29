package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class ChangePasswordRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("oldPassword")
    public String oldPassword;
    
    @SerializedName("password")
    public String password;
    

    public ChangePasswordRequest(String session, String oldPassword, String password) {
        this.session = session;this.oldPassword = oldPassword;this.password = password;
    }
}
