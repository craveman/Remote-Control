package ru.inspirationpoint.inspirationrc.manager.constants.commands;

import android.util.Log;

public class EthernetStopCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.ETH_SERVER_STOP;
        return super.getBytes();
    }
}
