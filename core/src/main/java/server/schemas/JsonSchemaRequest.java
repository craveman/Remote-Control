package server.schemas;

import com.google.gson.annotations.SerializedName;

/**
 * Json schema for request
 */
public abstract class JsonSchemaRequest extends JsonSchema {
    @SerializedName("session")
    public String SessionID;

    protected JsonSchemaRequest(String sessionID) {
        SessionID = sessionID;
    }
}
