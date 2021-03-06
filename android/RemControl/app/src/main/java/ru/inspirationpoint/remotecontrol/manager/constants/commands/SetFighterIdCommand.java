package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class SetFighterIdCommand extends CommonTCPCommand {

    private int person;
    private String id;

    public SetFighterIdCommand(int person, String name) {
        this.person = person;
        this.id = name;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(person);
            byte[] buf = id.getBytes(Charset.forName("UTF-8"));
            s1.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SETID_TCP_CMD;
        return super.getBytes();
    }
}
