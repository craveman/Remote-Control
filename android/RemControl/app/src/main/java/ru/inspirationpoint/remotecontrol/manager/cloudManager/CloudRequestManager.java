package ru.inspirationpoint.remotecontrol.manager.cloudManager;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;

public class CloudRequestManager {

    public static void login() {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(CommonConstants.CLOUD_AUTH_URL)
                    .method("GET", null)
                    .addHeader(CommonConstants.CLOUD_HEADER_LOGIN, CommonConstants.CLOUD_LOGIN)
                    .addHeader(CommonConstants.CLOUD_HEADER_PASS, CommonConstants.CLOUD_PASS)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) {
                    Log.wtf("TOKEN", response.header(CommonConstants.CLOUD_HEADER_TOKEN));
                    SettingsManager.setValue(CommonConstants.CLOUD_TOKEN, response.header(CommonConstants.CLOUD_HEADER_TOKEN));
                }
            });
        }).start();
    }

    public static void uploadFile(String filename) {
        new Thread(() -> {
            Log.wtf("UPL START", System.currentTimeMillis() + "");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("file"), new File(filename));
            String[] filenameSplitted = filename.split("/");
            String finalFilename = filenameSplitted[filenameSplitted.length-1];
            Request request = new Request.Builder()
                    .url(CommonConstants.CLOUD_STORAGE + finalFilename)
                    .method("PUT", body)
                    .addHeader(CommonConstants.CLOUD_HEADER_TOKEN, SettingsManager.getValue(CommonConstants.CLOUD_TOKEN, ""))
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.wtf("UPL FINISHED", System.currentTimeMillis() + "");
                    Log.wtf("UPL FAIL", e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    //TODO notify camera mb?
                }
            });
        }).start();
    }
}
