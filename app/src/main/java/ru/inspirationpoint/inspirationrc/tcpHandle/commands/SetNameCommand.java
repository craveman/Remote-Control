package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetNameCommand extends CommonTCPCommand {

    private int person;
    private int nameLength;
    private String name;

    public SetNameCommand(int person, String name) {
        this.person = person;
        this.name = name;
        this.nameLength = name.length();
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        s1.writeByte(person);
        s1.writeByte(nameLength);
        byte[] buf = name.getBytes("UTF-8");
        s1.write(buf);
        body = b1.toByteArray();
        cmd = CommandsContract.SETNAME_TCP_CMD;
        return super.getBytes();
    }
}
