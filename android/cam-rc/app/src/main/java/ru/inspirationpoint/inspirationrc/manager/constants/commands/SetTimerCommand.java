package ru.inspirationpoint.inspirationrc.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetTimerCommand extends CommonTCPCommand {

    private long time;
    private int mode;

    public SetTimerCommand(long time, int mode) {
        this.time = time;
        this.mode = mode;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(mode);
            s1.write(intToBytes((int)time));
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SETTIMER_TCP_CMD;
        return super.getBytes();
    }

}
