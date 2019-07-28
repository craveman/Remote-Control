package ru.inspirationpoint.inspirationrc.manager.constants.commands;


public class SwapFightersCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes(){
        cmd = CommandsContract.SWAP_FIGHTERS_TCP_CMD;
        return super.getBytes();
    }
}
