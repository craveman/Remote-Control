package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VisibilityOptionsCommand extends CommonTCPCommand {

    private final int isVideo;
    private final int isPhoto;
    private final int isPassive;
    private final int isCountry;

    public VisibilityOptionsCommand(boolean isv, boolean isph, boolean isp, boolean isc) {
        isVideo = isv?1:0;
        isPhoto = isph?1:0;
        isPassive = isp?1:0;
        isCountry = isc?1:0;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(isVideo);
            s1.writeByte(isPhoto);
            s1.writeByte(isPassive);
            s1.writeByte(isCountry);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.VISIBILITY_OPTIONS_CMD;
        return super.getBytes();
    }
}
