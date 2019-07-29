package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerCommand extends CommonTCPCommand {

    private int mode;
    private int speed;

    public PlayerCommand(int mode, int speed) {
        this.mode = mode;
        this.speed = speed;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(mode);
            s1.writeByte(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.PLAYER_TCP_CMD;
        return super.getBytes();
    }
}
