package ru.inspirationpoint.inspirationrc.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kotlin.text.Charsets;

public class CompetitionSetCommand extends CommonTCPCommand {

    private String name;

    public CompetitionSetCommand(String name) {
        this.name = name;
        if (name == null) {
            this.name = " ";
        }
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            byte[] buf = name.getBytes(Charsets.UTF_8);
            s1.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.COMPETITION_SET;
        return super.getBytes();
    }
}
