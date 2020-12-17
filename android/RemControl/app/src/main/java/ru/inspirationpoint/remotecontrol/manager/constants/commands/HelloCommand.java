package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HelloCommand extends CommonTCPCommand {

    private final int type;
    private final String name;
    private final int nameLength;

    public HelloCommand(int type, String name) {
        this.type = type;
        this.name = name;
        this.nameLength = name.length();
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(type);
            s1.writeByte(nameLength);
            byte[] buf = name.getBytes(StandardCharsets.UTF_8);
            s1.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.HELLO_TCP_CMD;
        return super.getBytes();
    }
}
