package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetDialogsRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    public GetDialogsRequest(String session) {
        this.session = session;
    }
}
