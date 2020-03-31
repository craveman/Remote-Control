package ru.inspirationpoint.remotecontrol.manager.constants.commands;

public class GetVideosCommand extends CommonTCPCommand {

    @Override
    public byte[] getBytes() {
        cmd = CommandsContract.GET_VIDEOS_CMD;
        return super.getBytes();
    }
}
