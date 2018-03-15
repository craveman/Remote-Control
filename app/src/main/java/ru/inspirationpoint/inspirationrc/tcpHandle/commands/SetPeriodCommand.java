package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetPeriodCommand extends CommonTCPCommand {

    private int period;

    public SetPeriodCommand(int period) {
        this.period = period;
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        s1.writeByte(period);
        body = b1.toByteArray();
        cmd = CommandsContract.SETPERIOD_TCP_CMD;
        return super.getBytes();
    }
}
