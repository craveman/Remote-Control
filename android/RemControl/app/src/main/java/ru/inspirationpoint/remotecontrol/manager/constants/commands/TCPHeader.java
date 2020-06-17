package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TCPHeader {

    private int len;
    private byte cmd;
    private int status;

    public TCPHeader(int len, byte cmd, int status) {
        this.len = len;
        this.cmd = cmd;
        this.status = status;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream s = new DataOutputStream(b);
        byte[] length = new byte[] {(byte) (len >> 8), (byte) len};
        s.writeByte(length[0]);
        s.writeByte(length[1]);
        s.writeByte(this.cmd);
        s.writeByte(this.status);
        return b.toByteArray();
    }

    private byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

}
