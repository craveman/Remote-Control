package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import static ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract.DISCONNECT_TCP_CMD;

public class DisconnectCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        body = new byte[]{(byte)0x01};
        cmd = DISCONNECT_TCP_CMD;
        return super.getBytes();
    }
}
