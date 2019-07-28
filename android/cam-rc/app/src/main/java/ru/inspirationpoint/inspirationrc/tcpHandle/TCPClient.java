package ru.inspirationpoint.inspirationrc.tcpHandle;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.constants.commands.CommandsContract;

import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.DEVICE_ID_SETTING;
import static ru.inspirationpoint.inspirationrc.tcpHandle.TCPHelper.PORT;

public class TCPClient {

    private String serverIp;

    private OnMessageReceived mMessageListener;
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
                    outputStream.write(message);
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

        mMessageListener = null;
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
                    byte[] temp = new byte[CommandsContract.HEADER_LENGTH];
                    int read = inputStream.read(temp, 0, CommandsContract.HEADER_LENGTH);
                    if (temp[0] == CommandsContract.PROTOCOL_VERSION ) {
                        byte[] buffer = new byte[temp[4] - CommandsContract.HEADER_LENGTH];
                        inputStream.read(buffer);
                        if (temp[5] == CommandsContract.PING_TCP_CMD) {
                            Log.wtf("TCPClient","PING RCVD");
                            sendMessage(CommandHelper.hello(CommonConstants.DEV_TYPE_RC, CommonConstants.DEV_STRING_TYPE_RC
                             + "_" + SettingsManager.getValue(DEVICE_ID_SETTING, "")));
                        } else {
                            Log.wtf("TCPClient","MSG" + Arrays.toString(buffer));
                            mMessageListener.messageReceived(temp[5], buffer);
                        }
                    }
                    if (read == -1) {
                        if (mMessageListener != null) {
                            Log.wtf("READ -1", "+");
                            mMessageListener.connectionLost();
                        }
                    }
                }
        } catch (SocketException e1) {
            Log.wtf("TCPClient", e1.getLocalizedMessage());
            if (mMessageListener != null) {
                mMessageListener.connectionLost();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface OnMessageReceived {
        void messageReceived(byte command, byte[] message);
        void outputStreamCreated();
        void connectionLost();
    }

}
