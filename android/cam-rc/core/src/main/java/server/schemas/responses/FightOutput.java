package server.schemas.responses;

import com.google.gson.annotations.SerializedName;

import server.schemas.requests.FightAction;

public class FightOutput {

    @SerializedName("_id")
    public String _id;

    @SerializedName("date")
    public String date;

    @SerializedName("address")
    public String address;

    @SerializedName("leftFighterId")
    public String leftFighterId;

    @SerializedName("rightFighterId")
    public String rightFighterId;

    @SerializedName("leftFighterName")
    public String leftFighterName;

    @SerializedName("rightFighterName")
    public String rightFighterName;

    @SerializedName("actions")
    public FightAction[] actions;

    @SerializedName("ownerId")
    public String ownerId;

    @SerializedName("ownerName")
    public String ownerName;

    @SerializedName("created")
    public String created;

    @SerializedName("modified")
    public String modified;

    @SerializedName("startTime")
    public long startTime;

    @SerializedName("endTime")
    public long endTime;

    @Override
    public int hashCode() {
        int base = 42;
        int result;
        result = base*actions.length + leftFighterId.hashCode() + rightFighterId.hashCode() + date.hashCode();
        result = base*result + leftFighterName.length() + rightFighterName.length();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FightOutput input = (FightOutput) obj;
        return leftFighterName.equals(input.leftFighterName) && rightFighterName.equals(input.rightFighterName) &&
                startTime == input.startTime;
    }

}
