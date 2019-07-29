package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class PingCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.PING_TCP_CMD;
        return super.getBytes();
    }
}
