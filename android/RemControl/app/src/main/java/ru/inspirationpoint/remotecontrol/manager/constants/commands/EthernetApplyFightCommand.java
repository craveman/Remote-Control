package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class EthernetApplyFightCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.ETH_APPLY_FIGHT;
        return super.getBytes();
    }
}
