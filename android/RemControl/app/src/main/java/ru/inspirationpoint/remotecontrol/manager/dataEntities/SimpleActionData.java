package ru.inspirationpoint.remotecontrol.manager.dataEntities;

public class SimpleActionData {
    private String id;
    private long timestamp;
    private int period;
    private long fightTime;

    public SimpleActionData(String id, long timestamp, int period, long fightTime) {
        this.id = id;
        this.timestamp = timestamp;
        this.period = period;
        this.fightTime = fightTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public long getFightTime() {
        return fightTime;
    }

    public void setFightTime(long fightTime) {
        this.fightTime = fightTime;
    }
}
