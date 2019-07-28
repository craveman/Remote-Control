package ru.inspirationpoint.inspirationrc.tcpHandle;

import android.util.Log;

import ru.inspirationpoint.inspirationrc.manager.constants.commands.CompetitionSetCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.EthernetStartCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.EthernetStopCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.HelloCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.LoadFileCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.PassiveLockChangeCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.PingCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.PlayerCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.ResetCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.SetCardCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.SetDefTimerCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.SetNameCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.SetPeriodCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.SetPriorityCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.SetScoreCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.SetTimerCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.SetWeaponCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.StartTimerCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.SwapFightersCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.VideoCounterCommand;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.VisibilityOptionsCommand;

public class CommandHelper {


    public static byte[] swap(){
        SwapFightersCommand command = new SwapFightersCommand();
        return command.getBytes();
    }

    public static byte[] setCard(int fighter, int card){
        SetCardCommand command = new SetCardCommand(fighter, card);
        return command.getBytes();
    }

    public static byte[] setScore(int fighter, int score){
        SetScoreCommand command = new SetScoreCommand(fighter, score);
        return command.getBytes();
    }

    public static byte[] setName(int person, String name){
        SetNameCommand command = new SetNameCommand(person, name);
        return command.getBytes();
    }

    public static byte[] setCompetition(String name){
        CompetitionSetCommand command = new CompetitionSetCommand(name);
        return command.getBytes();
    }

    public static byte[] setPriority(int fighter){
        SetPriorityCommand command = new SetPriorityCommand(fighter);
        return command.getBytes();
    }

    public static byte[] setPeriod(int period){
        SetPeriodCommand command = new SetPeriodCommand(period);
        return command.getBytes();
    }

    public static byte[] setTimer(long time, int mode){
        SetTimerCommand command = new SetTimerCommand(time, mode);
        return command.getBytes();
    }

    public static byte[] setDefTimer(long time){
        SetDefTimerCommand command = new SetDefTimerCommand(time);
        return command.getBytes();
    }

    public static byte[] setWeapon(int weapon) {
        SetWeaponCommand command = new SetWeaponCommand(weapon);
        return command.getBytes();
    }

    public static byte[] startTimer(boolean start) {
        StartTimerCommand command = new StartTimerCommand(start ? 0 : 1);
        return command.getBytes();
    }

    public static byte[] notifyPlayer(int mode, int speed) {
        PlayerCommand command = new PlayerCommand(mode, speed);
        return command.getBytes();
    }

    public static byte[] loadFile(int source, String fileName) {
        LoadFileCommand command = new LoadFileCommand(fileName, source);
        return command.getBytes();
    }

    public static byte[] reset(int time) {
        ResetCommand command = new ResetCommand(time);
        return command.getBytes();
    }

    public static byte[] ethStart() {
        EthernetStartCommand command = new EthernetStartCommand();
        Log.wtf("WTH START", "CALLED");
        return command.getBytes();
    }

    public static byte[] ethStop() {
        EthernetStopCommand command = new EthernetStopCommand();
        return command.getBytes();
    }

    public static byte[] visibilityOpt(boolean isVideo, boolean isPhoto, boolean isPassive, boolean isCountry) {
        VisibilityOptionsCommand command = new VisibilityOptionsCommand(isVideo, isPhoto, isPassive, isCountry);
        return command.getBytes();
    }

    public static byte[] videoCounters(int left, int right) {
        VideoCounterCommand command = new VideoCounterCommand(left, right);
        return command.getBytes();
    }

    public static byte[] passiveLock() {
        PassiveLockChangeCommand command = new PassiveLockChangeCommand();
        return command.getBytes();
    }

    public static byte[] ping() {
        PingCommand command = new PingCommand();
        return command.getBytes();
    }

    public static byte[] hello(int type, String name) {
        HelloCommand command = new HelloCommand(type, name);
        return command.getBytes();
    }

}
