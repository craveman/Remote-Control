package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class GetDialogsRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    public GetDialogsRequest(String session) {
        this.session = session;
    }
}
