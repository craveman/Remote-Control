package ru.inspirationpoint.remotecontrol.manager.helpers;

import java.io.Serializable;
import java.util.ArrayList;

import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData;

public class Metric implements Serializable{

    private String name;
    private ArrayList<FightActionData.ActionType> indicators;

    public Metric(String name, ArrayList<FightActionData.ActionType> indicators) {
        this.name = name;
        this.indicators = indicators;
    }

    public String getName() {
        return name;
    }

    public ArrayList<FightActionData.ActionType> getIndicators() {
        return indicators;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndicators(ArrayList<FightActionData.ActionType> indicators) {
        this.indicators = indicators;
    }
}
