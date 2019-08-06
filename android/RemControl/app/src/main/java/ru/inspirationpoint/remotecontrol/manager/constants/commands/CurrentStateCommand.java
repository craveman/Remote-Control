package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CurrentStateCommand extends CommonTCPCommand {

    private int weapon;
    private int flagLeft;
    private int flagRight;
    private int timestamp;
    private int timerState;

    public CurrentStateCommand(int weapon, int flagLeft, int flagRight, int timestamp, int timerState) {
        this.weapon = weapon;
        this.flagLeft = flagLeft;
        this.flagRight = flagRight;
        this.timestamp = timestamp;
        this.timerState = timerState;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(weapon);
            s1.writeByte(flagLeft);
            s1.writeByte(flagRight);
            s1.write(intToBytes(timestamp));
            s1.writeByte(timerState);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.BROADCAST_TCP_CMD;
        return super.getBytes();
    }
}
