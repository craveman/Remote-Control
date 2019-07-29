package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

public class FightAction {

    @SerializedName("_id")
    public String _id;
    
    @SerializedName("type")
    public String type;

    @SerializedName("fightId")
    public String fightId;
    
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

    @SerializedName("establishedTime")
    public long establishedTime;

    @SerializedName("phrase")
    public int phrase;

    @SerializedName("comment")
    public String comment;

    @SerializedName("videoUrl")
    public String videoUrl;

    @SerializedName("videoPreviewUrl")
    public String videoPreviewUrl;

    public FightAction(String _id, String type, String fightId, Long timestamp, String fighter, String period, Integer score, int fightPeriod, long establishedTime, int phrase, String comment, String videoUrl, String videoPreviewUrl) {
        this.type = type;
        this.fightId = fightId;
        this.timestamp = timestamp;
        this.fighter = fighter;
        this.period = period;
        this.score = score;
        this.fightPeriod = fightPeriod;
        this.establishedTime = establishedTime;
        this.phrase = phrase;
        this.comment = comment;
        this.videoUrl = videoUrl;
        this.videoPreviewUrl = videoPreviewUrl;
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFightId() {
        return fightId;
    }

    public void setFightId(String fightId) {
        this.fightId = fightId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFighter() {
        return fighter;
    }

    public void setFighter(String fighter) {
        this.fighter = fighter;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public int getFightPeriod() {
        return fightPeriod;
    }

    public void setFightPeriod(int fightPeriod) {
        this.fightPeriod = fightPeriod;
    }

    public long getEstablishedTime() {
        return establishedTime;
    }

    public void setEstablishedTime(long establishedTime) {
        this.establishedTime = establishedTime;
    }

    public int getPhrase() {
        return phrase;
    }

    public void setPhrase(int phrase) {
        this.phrase = phrase;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoPreviewUrl() {
        return videoPreviewUrl;
    }

    public void setVideoPreviewUrl(String videoPreviewUrl) {
        this.videoPreviewUrl = videoPreviewUrl;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
