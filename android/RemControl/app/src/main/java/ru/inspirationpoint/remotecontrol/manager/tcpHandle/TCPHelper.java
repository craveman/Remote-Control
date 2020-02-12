package ru.inspirationpoint.remotecontrol.manager.tcpHandle;

import android.util.Log;

import java.io.IOException;

public class TCPHelper extends Thread {

    private TCPClient mTcpClient;
    private String serverIp;
    private TCPListener listener;
    private int code;
    public static final int PORT = 21074;

    public TCPHelper(String serverIp, int code) {
        this.serverIp = serverIp;
        this.code = code;
    }

    public TCPHelper(String serverIp) {
        this.serverIp = serverIp;
    }

    @Override
    public void run() {
        mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {

            @Override
            public void messageReceived(byte command, byte status, byte[] message) {
                if (listener != null) {
                    listener.onReceive(command, status, message);
                }
            }

            @Override
            public void outputStreamCreated() {
                if (listener != null) {
                    listener.onStreamCreated();
                }
            }

            @Override
            public void connectionLost() {
                code = 0;
                listener.onDisconnect();
            }

        }, serverIp);

        mTcpClient.run();
    }

    public void send(byte[] message) {
        if (mTcpClient != null) {
            mTcpClient.sendMessage(message);
        }
    }

    public void end() {
        try {
            mTcpClient.stopClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return mTcpClient != null;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getCode() {
        return code;
    }

    public void setListener (TCPListener listener) {
        this.listener = listener;
    }

    public interface TCPListener {
        void onReceive(byte command, byte status, byte[] message);
        void onStreamCreated();
        void onDisconnect();
    }
}
