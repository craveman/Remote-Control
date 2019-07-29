package ru.inspirationpoint.remotecontrol.internalServer.schemas.requests;

import com.google.gson.annotations.SerializedName;

public class FightInput {
    
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

    @SerializedName("startTime")
    public long startTime;

    @SerializedName("endTime")
    public long endTime;

    @Override
    public int hashCode() {
        int base = 42;
        int result;
        result = base*leftFighterId.hashCode() + rightFighterId.hashCode() + date.hashCode();
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
        FightInput input = (FightInput)obj;
        return _id.equals(input._id);
    }
}
