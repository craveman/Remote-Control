package ru.inspirationpoint.inspirationrc.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CurrentStateCommand extends CommonTCPCommand {

    private int flags;
    private int timestamp;

    public CurrentStateCommand(int flags, int timestamp) {
        this.flags = flags;
        this.timestamp = timestamp;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(flags);
            s1.write(intToBytes(timestamp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.BROADCAST_TCP_CMD;
        return super.getBytes();
    }
}
