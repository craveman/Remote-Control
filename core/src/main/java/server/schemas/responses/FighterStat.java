package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class FighterStat {

    public FighterStat(String _id, String name) {
        this._id = _id;
        this.name = name;
    }

    @SerializedName("_id")
    public String _id;

    @SerializedName("fightCount")
    public int fightCount;

    @SerializedName("name")
    public String name;
}
