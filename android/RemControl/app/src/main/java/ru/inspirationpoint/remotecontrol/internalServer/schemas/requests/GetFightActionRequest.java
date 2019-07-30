package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetFightActionRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("actionId")
    public String actionId;

    public GetFightActionRequest(String session, String actionId) {
        this.session = session;
        this.actionId = actionId;
    }
}
