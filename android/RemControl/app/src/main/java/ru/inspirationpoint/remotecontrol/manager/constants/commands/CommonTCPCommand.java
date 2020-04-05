package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class CommonTCPCommand {

    byte[] body;
    private DataOutputStream s;
    byte cmd;
    byte status = (byte) 0x00;

    public byte[] getBytes(){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        s = new DataOutputStream(b);
        if (body != null) {
            try {
                s.write(new TCPHeader(body.length + 2, cmd, status).getBytes());
                s.write(body);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                s.write(new TCPHeader(2, cmd, status).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b.toByteArray();
    }

    byte[] intToBytes(final int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

}
