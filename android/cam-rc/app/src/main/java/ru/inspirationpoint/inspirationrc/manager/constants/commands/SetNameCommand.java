package ru.inspirationpoint.inspirationrc.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kotlin.text.Charsets;

public class SetNameCommand extends CommonTCPCommand {

    private int person;
    private int nameLength;
    private String name;

    public SetNameCommand(int person, String name) {
        this.person = person;
        this.name = name;
        if (name != null) {
            this.nameLength = name.length();
        } else {
            this.name = " ";
            this.nameLength = 1;
        }
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(person);
            s1.writeByte(nameLength);
            byte[] buf = name.getBytes(Charsets.UTF_8);
            s1.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SETNAME_TCP_CMD;
        return super.getBytes();
    }
}
