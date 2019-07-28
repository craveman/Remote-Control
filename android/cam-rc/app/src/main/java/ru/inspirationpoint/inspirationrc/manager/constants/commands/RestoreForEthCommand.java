package ru.inspirationpoint.inspirationrc.manager.constants.commands;

public class RestoreForEthCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.RESTORE_FOR_ETH;
        return super.getBytes();
    }
}
