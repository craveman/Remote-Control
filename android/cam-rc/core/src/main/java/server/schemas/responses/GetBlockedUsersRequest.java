package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetBlockedUsersRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    public GetBlockedUsersRequest(String session) {
        this.session = session;
    }
}
