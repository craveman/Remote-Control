package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetTimerCommand extends CommonTCPCommand {

    private long time;
    private int mode;

    public SetTimerCommand(long time, int mode) {
        this.time = time;
        if (time < 0) {
            this.time = 0;
        }
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
        Log.wtf("IN CMD", time + "");
        return super.getBytes();
    }

}
