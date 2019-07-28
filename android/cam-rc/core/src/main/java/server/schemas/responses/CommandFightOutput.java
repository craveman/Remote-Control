package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class CommandFightOutput {

    @SerializedName("created")
    public String created;

    @SerializedName("modified")
    public String modified;

    @SerializedName("_id")
    public String _id;

    @SerializedName("left")
    public Integer left;

    @SerializedName("right")
    public Integer right;

    @SerializedName("fightNumber")
    public Integer fightNumber;
}
