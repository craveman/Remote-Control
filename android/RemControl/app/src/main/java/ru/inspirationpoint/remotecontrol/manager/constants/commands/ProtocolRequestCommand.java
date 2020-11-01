package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PROTOCOL_REQUEST;

public class ProtocolRequestCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = PROTOCOL_REQUEST;
        return super.getBytes();
    }
}
