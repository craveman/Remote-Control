package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import org.apache.commons.net.util.Charsets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class CompetitionSetCommand extends CommonTCPCommand {

    private String name;
    private int nameLen;

    public CompetitionSetCommand(String name) {
        this.name = name;
        if (name == null) {
            this.name = " ";
        }
        nameLen = this.name.length();
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(nameLen);
            byte[] buf = name.getBytes(Charset.forName("UTF-8"));
            s1.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.COMPETITION_SET;
        return super.getBytes();
    }
}
