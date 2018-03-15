package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

public class FightAction {
    
    @SerializedName("type")
    public String type;
    
    @SerializedName("timestamp")
    public Long timestamp;
    
    @SerializedName("fighter")
    public String fighter;
    
    @SerializedName("period")
    public String period;
    
    @SerializedName("score")
    public Integer score;

    @SerializedName("fightPeriod")
    public int fightPeriod;
    
    @SerializedName("comment")
    public String comment;

    @SerializedName("establishedTime")
    public long establishedTime;

    @SerializedName("phrase")
    public int phrase;
}
