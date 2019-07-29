package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class EthernetPrevCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.ETH_PREV;
        return super.getBytes();
    }
}
