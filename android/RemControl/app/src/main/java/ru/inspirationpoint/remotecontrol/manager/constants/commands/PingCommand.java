package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class PingCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.PING_IN;
        return super.getBytes();
    }
}
