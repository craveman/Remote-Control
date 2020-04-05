package ru.inspirationpoint.remotecontrol.manager.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.ui.dialog.WiFiPassDialog;

public class WiFiHelper {

    private AppCompatActivity activity;

    private Handler handler;
    private String requiredSSID;
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private WifiManager wifiMgr;

    public void setRequiredSSID(String requiredSSID) {
        this.requiredSSID = requiredSSID;
    }

    public WiFiHelper(AppCompatActivity activity) {
        this.activity = activity;
        handler = new Handler(activity.getMainLooper());
    }

    public void tryToWiFiConnection(String ssid) {
        wifiMgr = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr != null) {
            wifiMgr.setWifiEnabled(true);
            boolean wifiConfigurated = false;
            int netId = -1;
            for (WifiConfiguration wifiConfiguration : wifiMgr.getConfiguredNetworks()) {
                Log.wtf("SSID COMPARE", wifiConfiguration.SSID + "||" + ssid);
                if (wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
                    wifiConfiguration.priority = 99999;
                    wifiMgr.updateNetwork(wifiConfiguration);
                    wifiMgr.saveConfiguration();
                    wifiMgr.enableNetwork(wifiConfiguration.networkId, true);
                    wifiConfigurated = true;
                    Log.wtf("EXISTS", "CONN");
                }
            }
            if (!wifiConfigurated) {
                Log.wtf("NOT CONFIGED", "+");
                wifiMgr.startScan();
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> initNewWiFi(ssid), 1000);

            }
        } else Log.wtf("WIFI MGR", "NULL");
    }

    private void initNewWiFi(String ssid) {
        List<ScanResult> networkList = wifiMgr.getScanResults();
        if (networkList.isEmpty()) {
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> initNewWiFi(ssid), 1000);
            Log.wtf("INIT", "LIST EMPTY");
        } else {
            for (ScanResult network : networkList) {
                if (network.SSID.equals(ssid)) {
                    String capabilities = network.capabilities;
                    if (capabilities.contains("WPA")) {
                        Log.wtf("INIT", capabilities);
                        WiFiPassDialog.show(activity,
                                activity.getResources().getString(R.string.wifi_pass_title, requiredSSID),
                                activity.getResources().getString(R.string.wifi_dlg_message));
                    } else {
                        WifiConfiguration wc = new WifiConfiguration();
                        wc.SSID = "\"" + ssid + "\"";
                        wc.hiddenSSID = true;
                        wc.priority = 0xBADBAD;
                        wc.status = WifiConfiguration.Status.ENABLED;
                        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        int id = wifiMgr.addNetwork(wc);
                        wifiMgr.enableNetwork(id, true);
                        handler.removeCallbacksAndMessages(null);
                        Log.wtf("OPENED", capabilities);
                    }

                }
            }
        }
    }

    public void connectWPA(String networkPass) {
        handler.removeCallbacksAndMessages(null);
        Log.wtf("WPA CASE", "+");
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + requiredSSID + "\"";
        wc.preSharedKey = "\"" + networkPass + "\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        int id = 0;
        if (wifiMgr != null) {
            id = wifiMgr.addNetwork(wc);
            wifiMgr.enableNetwork(id, true);
        }
    }

    public void routeNetworkRequestsThroughWifi(String ssid) {
        mConnectivityManager = (ConnectivityManager) activity.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        unregisterNetworkCallback(mNetworkCallback);
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        mNetworkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                if (wifiMgr.getConnectionInfo().getSSID().equals(ssid)) {
                    Log.wtf("EQUALS", "SSID");
                    releaseNetworkRoute();
                    createNetworkRoute(network);
                } else {
                    releaseNetworkRoute();
                }
            }
        };

        mConnectivityManager.requestNetwork(request, mNetworkCallback);
    }

    private void unregisterNetworkCallback(ConnectivityManager.NetworkCallback callback) {
        if (callback != null) {
            try {
                mConnectivityManager.unregisterNetworkCallback(callback);

            } catch (Exception ignore) {
            } finally {
                mNetworkCallback = null;
            }
        }
    }

    private void createNetworkRoute(Network network) {
        boolean processBoundToNetwork;
        if (Build.VERSION.SDK_INT >= 23) {
            processBoundToNetwork = mConnectivityManager.bindProcessToNetwork(network);
        } else {
            processBoundToNetwork = ConnectivityManager.setProcessDefaultNetwork(network);
        }
    }

    private void releaseNetworkRoute() {
        boolean processBoundToNetwork;
        if (Build.VERSION.SDK_INT >= 23) {
            processBoundToNetwork = mConnectivityManager.bindProcessToNetwork(null);
        } else {
            processBoundToNetwork = ConnectivityManager.setProcessDefaultNetwork(null);
        }
    }
}
