package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetPossibleFightersRequest extends JsonSchema {
    
    @SerializedName("session")
    public String session;
    
    @SerializedName("listNames")
    public String[] listNames;
    
    @SerializedName("searchString")
    public String searchString;
    
    @SerializedName("includeMe")
    public Boolean includeMe;
    

    public GetPossibleFightersRequest(String session, String[] listNames, String searchString, Boolean includeMe) {
        this.session = session;this.listNames = listNames;this.searchString = searchString;this.includeMe = includeMe;
    }
}
