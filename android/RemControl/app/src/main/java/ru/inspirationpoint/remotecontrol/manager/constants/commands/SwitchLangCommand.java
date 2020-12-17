package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SwitchLangCommand extends CommonTCPCommand {

    private final int lang;

    public SwitchLangCommand(int lang) {
        this.lang = lang;
    }

    @Override
    public byte[] getBytes(){
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(lang);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SWITCH_LANG;
        return super.getBytes();
    }
}
