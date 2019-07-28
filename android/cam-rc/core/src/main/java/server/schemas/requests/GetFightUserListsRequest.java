package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetFightUserListsRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("fightId")
    public String fightId;


    public GetFightUserListsRequest(String session, String fightId) {
        this.session = session;
        this.fightId = fightId;
    }
}
