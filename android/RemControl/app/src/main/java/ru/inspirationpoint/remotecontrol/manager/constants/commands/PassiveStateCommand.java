package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PassiveStateCommand extends CommonTCPCommand {

    private int show;
    private int lock;
    private int defTime;

    public PassiveStateCommand(boolean isShown, boolean isLocked, int defTime) {
        show = isShown?1:0;
        lock = isLocked?1:0;
        this.defTime = defTime;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(show);
            s1.writeByte(lock);
            s1.write(intToBytes(defTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.PASSIVE_STATE;
        return super.getBytes();
    }
}
