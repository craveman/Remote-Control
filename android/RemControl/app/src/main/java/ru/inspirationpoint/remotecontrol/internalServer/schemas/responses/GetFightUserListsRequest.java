package ru.inspirationpoint.remotecontrol.internalServer.schemas.responses;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

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
