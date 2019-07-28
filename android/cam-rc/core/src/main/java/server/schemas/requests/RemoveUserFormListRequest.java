package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

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
