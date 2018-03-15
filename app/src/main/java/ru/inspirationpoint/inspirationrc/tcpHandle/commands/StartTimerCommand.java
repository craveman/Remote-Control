package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StartTimerCommand extends CommonTCPCommand {

    private int start;

    public StartTimerCommand(int start) {
        this.start = start;
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        s1.writeByte(start);
        body = b1.toByteArray();
        cmd = CommandsContract.STARTTIMER_TCP_CMD;
        return super.getBytes();
    }
}
