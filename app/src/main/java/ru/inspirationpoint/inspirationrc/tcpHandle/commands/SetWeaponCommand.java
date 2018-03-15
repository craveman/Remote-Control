package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetWeaponCommand extends CommonTCPCommand {

    private int weapon;

    public SetWeaponCommand(int weapon) {
        this.weapon = weapon;
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        DataOutputStream s1 = new DataOutputStream(b1);
        s1.writeByte(weapon);
        body = b1.toByteArray();
        cmd = CommandsContract.SETWEAPON_TCP_CMD;
        return super.getBytes();
    }
}
