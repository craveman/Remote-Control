package ru.inspirationpoint.inspirationrc.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetDefTimerCommand extends CommonTCPCommand {

    private long time;

    public SetDefTimerCommand(long time) {
        this.time = time;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.write(intToBytes((int)time));
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SET_DEF_TIMER_CMD;
        return super.getBytes();
    }
}
