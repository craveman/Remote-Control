package ru.inspirationpoint.inspirationrc.manager.helpers;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPHelper extends Thread {

    private BroadcastListener listener;
    private Context ctx;
    private DatagramSocket socket;
    private static final int PORT = 21074;

    public UDPHelper(Context ctx, BroadcastListener listener) throws IOException {
        this.listener = listener;
        this.ctx = ctx;
    }

    public void send(String msg) throws IOException {
        if (socket == null) socket = new DatagramSocket(PORT);
        socket.setReuseAddress(true);
        socket.setBroadcast(true);
        byte[] sendData = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(
                sendData, sendData.length, getBroadcastAddress(), PORT);
        Log.wtf("SOCKET", socket.getInetAddress() + " " + socket.getBroadcast() );
        socket.send(sendPacket);
    }

    @Override
    public void run() {
        try {
            if (socket == null) {
                socket = new DatagramSocket(PORT);
                socket.setReuseAddress(true);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally {
            try {
                send("PIN");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            while (!socket.isClosed()) {
                try {
                    byte[] buf = new byte[4];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    BigInteger integer = new BigInteger(packet.getData());
                    Log.wtf("IN UDP", integer.toString());
                    listener.onReceive(integer.toString(),
                            packet.getAddress().getHostAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            socket.close();
        }
    }

    public void end() {
        if (socket != null) {
            socket.close();
        }
    }

    public interface BroadcastListener {
        void onReceive(String msg, String ip);
    }

    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = null;
        if (wifi != null) {
            dhcp = wifi.getDhcpInfo();
        }
        if(dhcp == null)
            return InetAddress.getByName("255.255.255.255");
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
}
