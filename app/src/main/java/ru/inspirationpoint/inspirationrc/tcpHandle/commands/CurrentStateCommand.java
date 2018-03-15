package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

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
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        s1.writeByte(flags);
        s1.write(intToBytes(timestamp));
        body = b1.toByteArray();
        cmd = CommandsContract.BROADCAST_TCP_CMD;
        return super.getBytes();
    }
}
