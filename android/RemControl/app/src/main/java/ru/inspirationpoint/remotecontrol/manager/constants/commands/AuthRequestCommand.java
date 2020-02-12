package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.DEV_TYPE_RC;

public class AuthRequestCommand extends CommonTCPCommand {

    private String code;
    private String name;
    private int codeLen;
    private int nameLen;

    public AuthRequestCommand(String code, String name) {
        this.code = code;
        this.name = name;
        codeLen = code.length();
        nameLen = name.length();
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(DEV_TYPE_RC);
            s1.writeByte(codeLen);
            byte[] buf = code.getBytes(StandardCharsets.UTF_8);
            s1.write(buf);
            s1.writeByte(nameLen);
            byte[] buf2 = name.getBytes(StandardCharsets.UTF_8);
            s1.write(buf2);
            s1.writeByte(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.AUTH_TCP_CMD;
        return super.getBytes();
    }
}
