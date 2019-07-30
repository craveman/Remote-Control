package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.JsonSchema;

public class SaveFightActionRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("action")
    public FightAction fightAction;


    public SaveFightActionRequest(String session, FightAction fightAction) {
        this.session = session;this.fightAction = fightAction;
    }
}
