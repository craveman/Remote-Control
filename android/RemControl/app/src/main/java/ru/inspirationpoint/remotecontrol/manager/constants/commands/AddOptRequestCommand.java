package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class AddOptRequestCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes(){
        cmd = CommandsContract.ADD_OPT_REQUEST;
        return super.getBytes();
    }
}
