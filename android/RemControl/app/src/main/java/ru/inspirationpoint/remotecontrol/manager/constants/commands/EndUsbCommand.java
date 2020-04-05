package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class EndUsbCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes(){
        cmd = CommandsContract.END_USB_MODE;
        return super.getBytes();
    }
}
