package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EthernetApplyFightCommand extends CommonTCPCommand {

    private final String necessaryInfo;

    public EthernetApplyFightCommand(String necessaryInfo) {
        this.necessaryInfo = necessaryInfo;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            byte[] buf = necessaryInfo.getBytes(StandardCharsets.UTF_8);
            s1.writeByte(buf.length);
            s1.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.ETH_APPLY_FIGHT;
        return super.getBytes();
    }
}
