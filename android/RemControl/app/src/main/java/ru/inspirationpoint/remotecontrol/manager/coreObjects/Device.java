package ru.inspirationpoint.remotecontrol.manager.coreObjects;

import android.util.Pair;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;

public class Device {

    private String address;
    private int type;
    private String name;
    private Pair<Byte, byte[]> message = new Pair<>(Byte.valueOf("0"), new byte[0x0]);

    public Device() {
    }

    public Device(String address, int type, String name) {
        this.address = address;
        this.type = type;
        this.name = name;
    }

    public String getInetAddress() {
        return address;
    }

    public void setInetAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Pair<Byte, byte[]> getMessage() {
        return message;
    }

    public void setMessage(Byte cmd, byte[] msg) {
        this.message = new Pair<>(cmd, msg);
    }

    @Override
    public boolean equals(Object o) {
//        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return type == device.type &&
                Objects.equals(name, device.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, name);
    }
}

