package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class CommonTCPCommand {

    byte[] body;
    DataOutputStream s;
    byte cmd;

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        s = new DataOutputStream(b);
        s.write(new TCPHeader(1, body.length + 6, cmd).getBytes());
        s.write(body);
        return b.toByteArray();
    }

    byte[] intToBytes(final int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

}
