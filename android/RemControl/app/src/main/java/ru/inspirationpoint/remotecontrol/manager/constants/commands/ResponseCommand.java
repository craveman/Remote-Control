package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.RESPONSE_CMD;

public class ResponseCommand extends CommonTCPCommand {

    private int command;
    private int result = 1;

    public ResponseCommand(int command) {
        this.command = command;
    }

    public ResponseCommand(int command, int result) {
        this.command = command;
        this.result = result;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = RESPONSE_CMD;
        status = (byte) result;
        return super.getBytes();
    }
}
