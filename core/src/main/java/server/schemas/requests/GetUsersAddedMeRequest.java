package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetUsersAddedMeRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    public GetUsersAddedMeRequest(String session) {
        this.session = session;
    }
}
