package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class SaveListRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("listName")
    public String listName;
    
    @SerializedName("userIds")
    public String[] userIds;
    

    public SaveListRequest(String session, String listName, String[] userIds) {
        this.session = session;this.listName = listName;this.userIds = userIds;
    }
}
