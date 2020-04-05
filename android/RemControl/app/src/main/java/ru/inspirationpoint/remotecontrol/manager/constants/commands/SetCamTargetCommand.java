package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetCamTargetCommand extends CommonTCPCommand {

    private int toRepeaters;

    public SetCamTargetCommand(boolean toRepeaters) {
        this.toRepeaters = toRepeaters?1:0;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(toRepeaters);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SET_CAM_TARGET_CMD;
        return super.getBytes();
    }
}
