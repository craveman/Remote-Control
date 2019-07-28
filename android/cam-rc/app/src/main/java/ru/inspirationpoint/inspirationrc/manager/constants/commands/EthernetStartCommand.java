package ru.inspirationpoint.inspirationrc.manager.constants.commands;

public class EthernetStartCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.ETH_SERVER_START;
        return super.getBytes();
    }
}
