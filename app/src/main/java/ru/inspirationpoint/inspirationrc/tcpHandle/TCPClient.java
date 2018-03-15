package ru.inspirationpoint.inspirationrc.tcpHandle;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class TCPClient {

    private String serverIp;
    private static final int PORT = 21074;
    private Byte[] mServerMessage;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public TCPClient(OnMessageReceived listener, String serverIp) {
        mMessageListener = listener;
        this.serverIp = serverIp;
    }

    public void sendMessage(final byte[] message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (outputStream != null) {
                    Log.d("TCP", "Sending: " + Arrays.toString(message));
                    try {
                        outputStream.write(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void stopClient() throws IOException {

        mRun = false;

        if (outputStream != null) {
            outputStream.flush();
            outputStream.close();
        }

        mMessageListener = null;
        inputStream = null;
        outputStream = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(serverIp);

            Log.e("TCP Client", "C: Connecting...");

            try (Socket socket = new Socket(serverAddr, PORT)) {
                outputStream = new DataOutputStream(socket.getOutputStream());
                inputStream = new DataInputStream(socket.getInputStream());

                while (mRun) {
                    int length = socket.getInputStream().available();
                    if (length > 0) {
                        Log.d("CLIENT", "Received" + length);
                        byte[] buffer = new byte[length];
                        inputStream.read(buffer);
                        mMessageListener.messageReceived(buffer);
                    }
                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + Arrays.toString(mServerMessage) + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    public interface OnMessageReceived {
        void messageReceived(byte[] message);
    }

}
