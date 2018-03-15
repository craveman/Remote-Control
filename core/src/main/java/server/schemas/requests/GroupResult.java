package server.schemas.requests;

import com.google.gson.annotations.SerializedName;

public class GroupResult {

    @SerializedName("_id")
    public long id;

    @SerializedName("number_first")
    public int numberFirst;

    @SerializedName("number_second")
    public int numberSecond;

    @SerializedName("scoreFirst")
    public int scoreFirst;

    @SerializedName("scoreSecond")
    public int scoreSecond;

    public GroupResult(long id, int numberFirst, int numberSecond, int scoreFirst, int scoreSecond) {
        this.id = id;
        this.numberFirst = numberFirst;
        this.numberSecond = numberSecond;
        this.scoreFirst = scoreFirst;
        this.scoreSecond = scoreSecond;
    }
}
