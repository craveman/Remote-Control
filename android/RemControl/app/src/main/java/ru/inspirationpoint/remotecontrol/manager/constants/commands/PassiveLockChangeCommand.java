package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class PassiveLockChangeCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.PASSIVE_LOCK_CHANGE;
        return super.getBytes();
    }
}
