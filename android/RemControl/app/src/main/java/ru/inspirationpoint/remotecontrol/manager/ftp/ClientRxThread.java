package ru.inspirationpoint.remotecontrol.manager.ftp;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.FILE_TRANSFER_START;

public class ClientRxThread extends Thread {
    private String dstAddress;
    private static final int dstPort = 8080;
    private Socket socket = null;
    private boolean mRun = false;
    private String timestamp;
    private String filePath;
    private FileUploadListener listener;

    public ClientRxThread(String address, String timestamp, String filePath, FileUploadListener listener) {
        dstAddress = address;
        this.filePath = filePath;
        this.timestamp = timestamp;
        this.listener = listener;
    }

    @Override
    public void run() {
        mRun = true;
        try {
            socket = new Socket(dstAddress, dstPort);
            Log.wtf("SOCKET++", socket.getInetAddress().getHostAddress());
            sendFile(new File(filePath), timestamp);
        } catch (IOException e) {

            e.printStackTrace();

            //TODO fail notify

        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopServer() {
        mRun = false;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void sendFile(File file, String prefix) {
        try {
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(file);
            OutputStream out = socket.getOutputStream();

            out.write(FILE_TRANSFER_START);
            out.write(prefix.getBytes("UTF-8").length);
            out.write(prefix.getBytes("UTF-8"));
            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            in.close();
            if (listener != null) {
                listener.onUpload();
            }

            //TODO notify success

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (listener != null) {
                listener.onError();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (listener != null) {
                listener.onError();
            }
        }
    }

    public interface FileUploadListener{
        void onUpload();
        void onError();
    }
}
