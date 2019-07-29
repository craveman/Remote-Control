package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TCPHeader {

    private int dummy;
    private int len;
    private byte cmd;

    public TCPHeader(int dummy, int len, byte cmd) {
        this.dummy = dummy;
        this.len = len;
        this.cmd = cmd;
    }

    public TCPHeader(byte data[]) throws IOException {
        DataInputStream s = new DataInputStream(new ByteArrayInputStream(data));
        this.dummy = s.readInt();
        this.len = s.readByte();
        this.cmd = s.readByte();
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream s = new DataOutputStream(b);
        s.write(dummy);
        s.write(0);
        s.write(0);
        s.write(0);
        s.writeByte(this.len);
        s.writeByte(this.cmd);
        return b.toByteArray();
    }

    private byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

}
