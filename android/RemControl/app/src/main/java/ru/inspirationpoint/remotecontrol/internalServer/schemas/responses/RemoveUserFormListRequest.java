package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class RemoveUserFormListRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("listName")
    public String listName;
    
    @SerializedName("userId")
    public String userId;
    

    public RemoveUserFormListRequest(String session, String listName, String userId) {
        this.session = session;this.listName = listName;this.userId = userId;
    }
}
