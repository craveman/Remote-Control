package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {

    @SerializedName("_id")
    public String _id;

    @SerializedName("dialogId")
    public String dialogId;

    @SerializedName("clientId")
    public String clientId;

    @SerializedName("peerClientId")
    public String peerClientId;

    @SerializedName("readTime")
    public String readTime;

    @SerializedName("isDeleted")
    public boolean isDeleted;

    @SerializedName("text")
    public String text;

    @SerializedName("created")
    public String created;

    @SerializedName("modified")
    public String modified;
}
