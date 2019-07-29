package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class AddUserToListRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("listName")
    public String listName;
    
    @SerializedName("userId")
    public String userId;
    

    public AddUserToListRequest(String session, String listName, String userId) {
        this.session = session;this.listName = listName;this.userId = userId;
    }
}
