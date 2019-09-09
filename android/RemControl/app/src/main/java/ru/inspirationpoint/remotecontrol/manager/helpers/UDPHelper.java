package ru.inspirationpoint.remotecontrol.manager.helpers;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import ru.inspirationpoint.remotecontrol.manager.SettingsManager;

import static android.content.Context.WIFI_SERVICE;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.SM_CODE;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.UDPCommands.PING_UDP;

public class UDPHelper extends Thread {

    private BroadcastListener listener;
    private Context ctx;
    private DatagramSocket socket;
    private static final int PORT = 21075;
    private Handler handler;

    public UDPHelper(Context ctx) {
        this.ctx = ctx;
        resetListener();
    }

    public void setListener(BroadcastListener listener) {
        this.listener = listener;
    }

    public void resetListener() {
        this.listener = new BroadcastListener() {
            @Override
            public void onReceive(String[] msg, String ip) {
                Log.wtf("IN UDP INNER", Arrays.toString(msg));
//                String smIp;
//                try {
//                    smIp = InspirationDayApplication.getApplication().getHelper().getServerIp();
//                } catch (NullPointerException e) {
//                    smIp = "";
//                }
//                switch (msg[0]){
//                    case "ISRC":
//                        if (!TextUtils.isEmpty(smIp)) {
//                            if (msg[1].equals(smIp)) {
//                                try {
//                                    sendTargetMessage("RCRDY\0" + smIp, ip);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                        break;
//                    case "CAMRDY":
//                        if (!TextUtils.isEmpty(smIp)) {
//                            if (msg[1].equals(smIp)) {
//                                Camera camera = new Camera();
//                                camera.ip.set(ip);
//                                camera.name.set(msg[2]);
//                                InspirationDayApplication.getApplication().addCamera(camera);
//                            }
//                        }
//                        break;
//                    case "PING":
//                        try {
//                            sendBroadcast("0000" + "\0" + "RCON");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    case "STAT":
//                        if (!TextUtils.isEmpty(smIp)) {
//                            try {
//                                sendBroadcast(smIp);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        break;
//                    case "VIDRDY":
//                        //TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                        break;
//                }
            }

            @Override
            public void onCreated() {

            }
        };
    }

    public void sendBroadcast(String msg) throws IOException {
        byte[] sendData = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(
                sendData, sendData.length, getBroadcastAddress(), PORT);
        socket.send(sendPacket);
    }

    public void sendTargetMessage(String msg, String ip) throws IOException {
        byte[] sendData = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(
                sendData, sendData.length, InetAddress.getByName(ip), PORT);
        Log.wtf("PACKET ADDR", "||"
         + sendPacket.getAddress().isLoopbackAddress());
        socket.send(sendPacket);
    }

    @Override
    public void run() {
        Log.wtf("UDPThread", "RUN");
        try {
            if (socket == null || socket.isClosed()) {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(PORT));
//                socket.setBroadcast(false);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally {
            listener.onCreated();
        }
        if (socket != null) {
            while (!socket.isClosed()) {
                try {
                    byte[] buf = new byte[128];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    if (!socket.isClosed()) {
                        socket.receive(packet);
                    }
                    String text = new String(packet.getData()).trim();
                    packet.setLength(buf.length);
                    WifiManager wm = (WifiManager) ctx.getApplicationContext().getSystemService(WIFI_SERVICE);
                    String ip = null;
                    if (wm != null) {
                        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                    }
                    if (!packet.getAddress().getHostName().equals(ip)) {
                        Log.wtf("UDP RECEIVE", text);
//                        if (text.equals(PING_UDP)) {
//                            sendTargetMessage("HELLO" + "\0" + "2" + "\0"
//                                            + SettingsManager.getValue(SM_CODE, ""),
//                                    packet.getAddress().getHostName());
//                        }else {
                            String[] messageArray = text.split("\0");
                            if ((messageArray.length >= 1)) {
                                listener.onReceive(messageArray, packet.getAddress().getHostAddress());
                            }
//                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void end() {
        try {
            socket.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean isUDPAlive() {
        return (socket!=null)&&(!socket.isClosed());
    }

    public interface BroadcastListener {
        void onReceive(String[] msg, String ip);
        void onCreated();
    }

    private InetAddress getBroadcastAddress() throws IOException {
//        WifiManager wifi = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        DhcpInfo dhcp = null;
//        if (wifi != null) {
//            dhcp = wifi.getDhcpInfo();
//        }
//        if(dhcp == null)
            return InetAddress.getByName("255.255.255.255");
//        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
//        byte[] quads = new byte[4];
//        for (int k = 0; k < 4; k++)
//            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
//        return InetAddress.getByAddress(quads);
    }
}
