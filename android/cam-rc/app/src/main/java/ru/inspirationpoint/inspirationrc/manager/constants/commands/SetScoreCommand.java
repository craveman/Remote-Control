package ru.inspirationpoint.inspirationrc.manager.constants.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetScoreCommand extends CommonTCPCommand {

    private int fighter;
    private int score;

    public SetScoreCommand(int fighter, int score) {
        this.fighter = fighter;
        this.score = score;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        try {
            s1.writeByte(fighter);
            s1.writeByte(score);
        } catch (IOException e) {
            e.printStackTrace();
        }
        body = b1.toByteArray();
        cmd = CommandsContract.SETSCORE_TCP_CMD;
        return super.getBytes();
    }
}
