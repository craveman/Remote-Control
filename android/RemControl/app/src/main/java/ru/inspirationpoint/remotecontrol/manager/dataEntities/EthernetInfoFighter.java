package ru.inspirationpoint.remotecontrol.manager.dataEntities;

public class EthernetInfoFighter {

    private String id = "";
    private String name = "";
    private String nation = "";
    private int score = 0;
    private String status = "U";
    private int yellowNum = 0;
    private int redNum = 0;
    private int light = 0;
    private int whiteLight = 0;
    private int medical = 0;
    private String reserve = "N";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getYellowNum() {
        return yellowNum;
    }

    public void setYellowNum(int yellowNum) {
        this.yellowNum = yellowNum;
    }

    public int getRedNum() {
        return redNum;
    }

    public void setRedNum(int redNum) {
        this.redNum = redNum;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getWhiteLight() {
        return whiteLight;
    }

    public void setWhiteLight(int whiteLight) {
        this.whiteLight = whiteLight;
    }

    public int getMedical() {
        return medical;
    }

    public void setMedical(int medical) {
        this.medical = medical;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }
}
