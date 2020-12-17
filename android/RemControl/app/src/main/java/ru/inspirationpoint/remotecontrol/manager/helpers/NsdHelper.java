package ru.inspirationpoint.remotecontrol.manager.helpers;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class NsdHelper {

    public NsdHelper(Context context) {
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    NsdManager nsdManager;
    private NsdManager.DiscoveryListener discoveryListener = null;
    private NsdManager.ResolveListener resolveListener = null;
    private final AtomicBoolean resolveListenerBusy = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<NsdServiceInfo> pendingNsdServices = new ConcurrentLinkedQueue<>();
    private static final String NSD_SERVICE_TYPE = "_ip_top._tcp.";
    private static final String NSD_SERVICE_NAME = "Inspiration";

    public void initializeNsd() {
        initializeResolveListener();
    }

    // Instantiate DNS-SD discovery listener
    // used to discover available Sonata audio servers on the same network
    private void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        discoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                stopDiscovery();
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {

            }

            @Override
            public void onDiscoveryStopped(String serviceType) {

            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.wtf("SERVICE", serviceInfo.toString());
                if (serviceInfo.getServiceType().equals(NSD_SERVICE_TYPE) &&
                        serviceInfo.getServiceName().contains(NSD_SERVICE_NAME) ) {
                    Log.wtf("IT IS", "++");
                    if (resolveListenerBusy.compareAndSet(false, true)) {
                        nsdManager.resolveService(serviceInfo, resolveListener);
                    }
                    else {
                        pendingNsdServices.add(serviceInfo);
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Iterator<NsdServiceInfo> iterator = pendingNsdServices.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getServiceName().equals(serviceInfo.getServiceName()))
                        iterator.remove();
                }

                onNsdServiceLost(serviceInfo);
            }
        };
    }

    // Instantiate DNS-SD resolve listener to get extra information about the service
    private void initializeResolveListener() {
        resolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                resolveNextInQueue();
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                onNsdServiceResolved(serviceInfo);

                resolveNextInQueue();
            }
        };
    }

    public void discoverServices() {
        stopDiscovery();

        initializeDiscoveryListener();

        nsdManager.discoverServices(NSD_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void stopDiscovery() {
        if (discoveryListener != null) {
            try {
                nsdManager.stopServiceDiscovery(discoveryListener);
            } finally {
            }
            discoveryListener = null;
        }
    }

    private void resolveNextInQueue() {
        NsdServiceInfo nextNsdService = pendingNsdServices.poll();
        if (nextNsdService != null) {
            nsdManager.resolveService(nextNsdService, resolveListener);
        }
        else {
            resolveListenerBusy.set(false);
        }
    }

    public abstract void onNsdServiceResolved(NsdServiceInfo service);

    public abstract void onNsdServiceLost(NsdServiceInfo service);
}

