package ru.inspirationpoint.inspirationrc.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LoadFileCommand extends CommonTCPCommand {

    private String fileName;
    private int nameLenght = 1;
    private int source;

    public LoadFileCommand(String fileName, int source) {
        this.fileName = fileName;
        if (fileName != null) {
            if (fileName.length() != 0) {
                nameLenght = fileName.length();
            }
        }
        this.source = source;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(source);
            s1.writeByte(nameLenght);
            byte[] buf = fileName.getBytes("UTF-8");
            s1.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.LOAD_FILE_TCP_CMD;
        return super.getBytes();
    }
}
