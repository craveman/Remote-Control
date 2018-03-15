package ru.inspirationpoint.inspirationrc.tcpHandle;

import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

public class TCPHelper extends Thread {

    private TCPClient mTcpClient;
    private String serverIp;
    private TCPListener listener;

    public TCPHelper(String serverIp) {
        this.serverIp = serverIp;
    }

    @Override
    public void run() {
        mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {

            @Override
            public void messageReceived(byte[] message) {
                Log.d("TASK", "Data received: " + Arrays.toString(message));

                if (listener != null) {
                    listener.onReceive(message);
                }
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

    public void setListener (TCPListener listener) {
        this.listener = listener;
    }

    public interface TCPListener {
        void onReceive(byte[] message);
    }
}
