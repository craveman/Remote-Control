package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

import server.schemas.JsonSchema;

public class GetMessagesRequest extends JsonSchema {

    @SerializedName("session")
    public String session;

    @SerializedName("dialogId")
    public String dialogId;

    @SerializedName("peerClientId")
    public String peerClientId;

    @SerializedName("startTime")
    public String startTime;

    @SerializedName("endTime")
    public String endTime;

    @SerializedName("onlyNew")
    public boolean onlyNew;

    @SerializedName("offset")
    public Integer offset;

    @SerializedName("limit")
    public Integer limit;

    public GetMessagesRequest(String session, String dialogId, String peerClientId, boolean onlyNew, Integer offset, Integer limit, String startTime, String endTime) {
        this.session = session;
        this.dialogId = dialogId;
        this.peerClientId = peerClientId;
        this.onlyNew = onlyNew;
        this.offset = offset;
        this.limit = limit;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
