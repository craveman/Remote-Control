package ru.inspirationpoint.remotecontrol.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetCardCommand extends CommonTCPCommand {

    private int fighter;
    private int card;

    public SetCardCommand(int fighter, int card) {
        this.fighter = fighter;
        this.card = card;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(fighter);
            s1.writeByte(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SETCARD_TCP_CMD;
        return super.getBytes();
    }

}
