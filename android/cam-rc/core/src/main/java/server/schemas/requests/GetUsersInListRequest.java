package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetUsersInListRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("listName")
    public String listName;
    

    public GetUsersInListRequest(String session, String listName) {
        this.session = session;this.listName = listName;
    }
}
