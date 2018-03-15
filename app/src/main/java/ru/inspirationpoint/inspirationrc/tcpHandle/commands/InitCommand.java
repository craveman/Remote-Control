package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InitCommand extends CommonTCPCommand {

    private int code;

    public InitCommand(int code) {
        this.code = code;
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        s1.writeByte(1);
        s1.write(intToBytes(code));
        body = b1.toByteArray();
        cmd = CommandsContract.INIT_TCP_CMD;
        return super.getBytes();
    }
}
