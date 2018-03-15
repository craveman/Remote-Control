package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetPriorityCommand extends CommonTCPCommand {

    private int fighter;
    private boolean isPriority;

    public SetPriorityCommand(int fighter, boolean isPriority) {
        this.fighter = fighter;
        this.isPriority = isPriority;
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        s1.writeByte(isPriority ? fighter : 0);
        body = b1.toByteArray();
        cmd = CommandsContract.SETPRIORITY_TCP_CMD;
        return super.getBytes();
    }
}
