package ru.inspirationpoint.inspirationrc.tcpHandle;

import java.io.IOException;

import ru.inspirationpoint.inspirationrc.tcpHandle.commands.InitCommand;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.SetCardCommand;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.SetNameCommand;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.SetPeriodCommand;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.SetPriorityCommand;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.SetScoreCommand;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.SetTimerCommand;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.SetWeaponCommand;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.StartTimerCommand;

public class CommandHelper {

    public static byte[] init(int code) throws IOException {
        InitCommand command = new InitCommand(code);
        return command.getBytes();
    }

    public static byte[] setCard(int fighter, boolean isCard) throws IOException {
        SetCardCommand command = new SetCardCommand(fighter, isCard ? 1 : 0);
        return command.getBytes();
    }

    public static byte[] setScore(int fighter, int score) throws IOException {
        SetScoreCommand command = new SetScoreCommand(fighter, score);
        return command.getBytes();
    }

    public static byte[] setName(int person, String name) throws IOException {
        SetNameCommand command = new SetNameCommand(person, name);
        return command.getBytes();
    }

    public static byte[] setPriority(int fighter, boolean isPriority) throws IOException {
        SetPriorityCommand command = new SetPriorityCommand(fighter, isPriority);
        return command.getBytes();
    }

    public static byte[] setPeriod(int period) throws IOException {
        SetPeriodCommand command = new SetPeriodCommand(period);
        return command.getBytes();
    }

    public static byte[] setTimer(long time) throws IOException {
        SetTimerCommand command = new SetTimerCommand(time);
        return command.getBytes();
    }

    public static byte[] setWeapon(int weapon) throws IOException {
        SetWeaponCommand command = new SetWeaponCommand(weapon);
        return command.getBytes();
    }

    public static byte[] startTimer(boolean start) throws IOException {
        StartTimerCommand command = new StartTimerCommand(start ? 1 : 0);
        return command.getBytes();
    }

}
