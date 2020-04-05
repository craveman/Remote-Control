package ru.inspirationpoint.remotecontrol.manager.dataEntities;

public class EthernetInfoMain {

    private String protocol = "";
    private String command = "";
    private String piste = "";
    private String competition = "";
    private int phase = 1;
    private String poulTab = "";
    private int matchNumber = 1;
    private int round = 1;
    private String time = "11:11";
    private String stopwatch = "03:00";
    private String type = "I";
    private String weapon = "F";
    private String priority = "N";
    private String state = "H";

    public EthernetInfoMain() {
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPiste() {
        return piste;
    }

    public void setPiste(String piste) {
        this.piste = piste;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public String getPoulTab() {
        return poulTab;
    }

    public void setPoulTab(String poulTab) {
        this.poulTab = poulTab;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStopwatch() {
        return stopwatch;
    }

    public void setStopwatch(String stopwatch) {
        this.stopwatch = stopwatch;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
