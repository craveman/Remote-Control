package ru.inspirationpoint.inspirationrc.manager.constants.commands;

public class EthernetFinishAskCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.FINISH_FIGHT_ASK;
        return super.getBytes();
    }
}
