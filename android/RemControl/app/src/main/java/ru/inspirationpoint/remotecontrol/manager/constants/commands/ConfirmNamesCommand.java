package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.CONFIRM_NAMES_CMD;

public class ConfirmNamesCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        body = new byte[]{(byte)0x01};
        cmd = CONFIRM_NAMES_CMD;
        return super.getBytes();
    }
}
