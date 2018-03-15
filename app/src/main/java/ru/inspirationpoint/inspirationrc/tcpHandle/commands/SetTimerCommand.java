package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetTimerCommand extends CommonTCPCommand {

    private long time;

    public SetTimerCommand(long time) {
        this.time = time;
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        s1.write(intToBytes((int)time));
        body = b1.toByteArray();
        cmd = CommandsContract.SETTIMER_TCP_CMD;
        return super.getBytes();
    }

}
