package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class FinishFightCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.FINISH_FIGHT;
        return super.getBytes();
    }
}
