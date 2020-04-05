package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.VIDEO_TRANSFER_ABORT;

public class VideoAbortCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = VIDEO_TRANSFER_ABORT;
        return super.getBytes();
    }
}
