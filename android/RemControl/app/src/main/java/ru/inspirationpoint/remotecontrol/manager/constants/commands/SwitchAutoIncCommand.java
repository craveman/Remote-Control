package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SwitchAutoIncCommand extends CommonTCPCommand {

    private final int isOn;

    public SwitchAutoIncCommand(int isOn) {
        this.isOn = isOn;
    }

    @Override
    public byte[] getBytes(){
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(isOn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SWITCH_AUTOINC;
        return super.getBytes();
    }
}
