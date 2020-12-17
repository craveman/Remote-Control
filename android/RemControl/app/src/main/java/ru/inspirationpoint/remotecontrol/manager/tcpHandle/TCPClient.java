package ru.inspirationpoint.remotecontrol.manager.tcpHandle;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract;
import ru.inspirationpoint.remotecontrol.manager.constants.commands.PingCommand;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.DEVICE_ID_SETTING;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.AUTH_RESPONSE;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.TCP_OK;
import static ru.inspirationpoint.remotecontrol.manager.tcpHandle.TCPHelper.PORT;

public class TCPClient {

    private final String serverIp;

    private final OnMessageReceived mMessageListener;
    private boolean mRun = false;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public TCPClient(OnMessageReceived listener, String serverIp) {
        mMessageListener = listener;
        this.serverIp = serverIp;
    }

    public void sendMessage(final byte[] message) {
        new Thread(() -> {
            if (outputStream != null) {
                try {
                    if (message != null) {
                        if (message.length != 0) {
                            outputStream.write(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopClient() throws IOException {

        mRun = false;

        if (outputStream != null) {
            outputStream.flush();
            outputStream.close();
        }

//        mMessageListener = null;
        inputStream = null;
        outputStream = null;
    }

    public void run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(serverIp);
            Socket socket = new Socket(serverAddr, PORT);
            outputStream = new DataOutputStream(socket.getOutputStream());
            mMessageListener.outputStreamCreated();
            inputStream = new DataInputStream(socket.getInputStream());

            while (mRun) {
                if (inputStream != null) {
                    byte[] temp = new byte[2];
                    int read = inputStream.read(temp);
                    if (temp[1] != 0) {
                        int length = ((temp[0] & 0xff) << 8 | (temp[1] & 0xff));
                        if (length != 0) {
                            byte[] header = new byte[2];
                            int read2 = inputStream.read(header);
                            byte[] buffer = new byte[length - 2];
                            inputStream.read(buffer);
                            if (header[1] == (byte) 0x00 || header[0] == AUTH_RESPONSE) {
                                mMessageListener.messageReceived(header[0], header[1], buffer);
                            } else {
                                //TODO handle strange statuses
                            }
                            if (read == -1) {
                                if (mMessageListener != null) {
                                    Log.wtf("READ -1", "+");
                                    mMessageListener.connectionLost();
                                }
                            }
                        }
                    }
                } else {
                    if (mMessageListener != null) {
                        mMessageListener.connectionLost();
                    }
                }
            }
        } catch (SocketException e1) {
            Log.wtf("TCPClient SOCKET error", e1.getLocalizedMessage());
            if (mMessageListener != null) {
                mMessageListener.connectionLost();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.wtf("TCPClient IO error", e.getLocalizedMessage());
        }
    }

    public interface OnMessageReceived {
        void messageReceived(byte command, byte status, byte[] message);

        void outputStreamCreated();

        void connectionLost();
    }

}
