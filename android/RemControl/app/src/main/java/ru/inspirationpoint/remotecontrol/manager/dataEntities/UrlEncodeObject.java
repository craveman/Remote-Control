package ru.inspirationpoint.remotecontrol.manager.dataEntities;

import com.google.gson.annotations.SerializedName;

public class UrlEncodeObject {

    @SerializedName("code")
    private int[] code;

    @SerializedName("ip")
    private String ip;

    @SerializedName("ssid")
    private String ssid;

    public UrlEncodeObject() {
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int[] getCode() {
        return code;
    }

    public void setCode(int[] code) {
        this.code = code;
    }
}
