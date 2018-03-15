package ru.inspirationpoint.inspirationrc.tcpHandle.commands;

public interface CommandsContract {

    byte INIT_TCP_CMD = (byte)0x01;
    byte SETNAME_TCP_CMD = (byte) 0x02;
    byte SETSCORE_TCP_CMD = (byte) 0x03;
    byte SETCARD_TCP_CMD = (byte) 0x04;
    byte SETPRIORITY_TCP_CMD = (byte) 0x05;
    byte SETPERIOD_TCP_CMD = (byte) 0x06;
    byte SETWEAPON_TCP_CMD = (byte) 0x07;
    byte SETTIMER_TCP_CMD = (byte) 0x08;
    byte STARTTIMER_TCP_CMD = (byte) 0x09;
    byte BROADCAST_TCP_CMD = (byte) 0x0A;

    int PERSON_TYPE_LEFT = 1;
    int PERSON_TYPE_RIGHT = 2;
    int PERSON_TYPE_REFEREE = 3;

}
