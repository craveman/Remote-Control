package ru.inspirationpoint.inspirationrc.rc.manager.dataEntities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FullFightInfo implements Cloneable, Serializable {

    @SerializedName("fightData")
    private FightData fightData;
    @SerializedName("connectedSM")
    private Device connectedSM;
    @SerializedName("connectedCams")
    private ArrayList<Device> connectedCams;

    public FullFightInfo(FightData fightData, Device connectedSM, ArrayList<Device> connectedCams) {
        this.fightData = fightData;
        this.connectedSM = connectedSM;
        this.connectedCams = connectedCams;
    }

    public FightData getFightData() {
        return fightData;
    }

    public void setFightData(FightData fightData) {
        this.fightData = fightData;
    }

    public Device getConnectedSM() {
        return connectedSM;
    }

    public void setConnectedSM(Device connectedSM) {
        this.connectedSM = connectedSM;
    }

    public ArrayList<Device> getConnectedCams() {
        return connectedCams;
    }

    public void setConnectedCams(ArrayList<Device> connectedCams) {
        this.connectedCams = connectedCams;
    }
}
