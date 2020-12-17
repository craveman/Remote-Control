package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VideoCounterCommand extends CommonTCPCommand {

    private final int left;
    private final int right;

    public VideoCounterCommand(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(left);
            s1.writeByte(right);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.VIDEO_COUNTER_CMD;
        return super.getBytes();
    }
}
