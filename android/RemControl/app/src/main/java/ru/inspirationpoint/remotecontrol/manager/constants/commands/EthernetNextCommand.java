package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class EthernetNextCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.ETH_NEXT;
        return super.getBytes();
    }
}
