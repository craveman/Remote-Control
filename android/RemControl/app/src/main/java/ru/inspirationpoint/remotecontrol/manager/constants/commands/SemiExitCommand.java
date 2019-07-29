package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class SemiExitCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.SEMI_EXIT;
        return super.getBytes();
    }
}
