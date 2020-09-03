package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class SwitchLangCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes(){
        cmd = CommandsContract.SWITCH_LANG;
        return super.getBytes();
    }
}
