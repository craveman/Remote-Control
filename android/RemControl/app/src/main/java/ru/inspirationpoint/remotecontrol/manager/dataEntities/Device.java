package ru.inspirationpoint.remotecontrol.manager.dataEntities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Device implements Cloneable, Serializable {

    @SerializedName("ip")
    private String ip;
    @SerializedName("code")
    private int code;

    public Device(String ip, int code) {
        this.ip = ip;
        this.code = code;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
