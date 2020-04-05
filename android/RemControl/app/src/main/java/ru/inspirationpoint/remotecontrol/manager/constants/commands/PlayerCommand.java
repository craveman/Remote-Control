package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerCommand extends CommonTCPCommand {

    private int mode;
    private int speed;
    private int timestamp;

    public PlayerCommand(int mode, int speed, int timestamp) {
        this.mode = mode;
        this.speed = speed;
        this.timestamp = timestamp;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(speed);
            s1.writeByte(mode);
//            s1.write(intToBytes(timestamp));
            s1.writeByte(timestamp);
            Log.wtf("PAMAMS", speed + "|" + mode + "||" + timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.PLAYER_TCP_CMD;
        return super.getBytes();
    }
}
