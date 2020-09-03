package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SetPisteNumCommand extends CommonTCPCommand {

    private int numLength;
    private String num;
    private int isOn;

    public SetPisteNumCommand(@NotNull String num, int isOn) {
        this.isOn = isOn;
        this.num = num;
        this.numLength = num.length();
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(isOn);
            s1.writeByte(numLength);
            byte[] buf = num.getBytes(StandardCharsets.UTF_8);
            s1.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SET_PISTE_NUM;
        return super.getBytes();
    }
}
