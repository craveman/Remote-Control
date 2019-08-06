package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EthNextPrevCommand extends CommonTCPCommand {

    private int mode;

    public EthNextPrevCommand(int mode) {
        this.mode = mode;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(mode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.ETH_NEXT_PREV;
        return super.getBytes();
    }
}
