package ru.inspirationpoint.inspirationrc.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ResetCommand extends CommonTCPCommand {

    private int time;

    public ResetCommand(int time) {
        this.time = time;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.write(intToBytes(time));
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.RESET_TCP_CMD;
        return super.getBytes();
    }
}
