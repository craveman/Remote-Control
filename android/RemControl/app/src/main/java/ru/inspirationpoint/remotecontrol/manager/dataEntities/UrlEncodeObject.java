package ru.inspirationpoint.remotecontrol.manager.dataEntities;

import com.google.gson.annotations.SerializedName;

public class UrlEncodeObject {

    @SerializedName("ssid")
    private String ssid;

    @SerializedName("ip")
    private String ip;

    @SerializedName("code")
    private String code;

    public UrlEncodeObject() {
    }

    public UrlEncodeObject(String ssid, String ip, String code) {
        this.ssid = ssid;
        this.ip = ip;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
