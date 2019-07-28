package ru.inspirationpoint.inspirationrc.camera.utils;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CameraUtils {
    private static final String TAG = "CamUtils";

    private static final double ASPECT_TOLERANCE=0.1;

    public static Size getOptimalPreviewSize(int displayOrientation,
                                                    int width,
                                                    int height,
                                                    Camera.Parameters parameters) {
        double targetRatio=(double)width / height;
        List<Size> sizes=parameters.getSupportedPreviewSizes();
        Size optimalSize=null;
        double minDiff=Double.MAX_VALUE;
        int targetHeight=height;

        if (displayOrientation == 90 || displayOrientation == 270) {
            targetRatio=(double)height / width;
        }

        // Try to find an size match aspect ratio and size

        for (Size size : sizes) {
            double ratio=(double)size.width / size.height;

            if (Math.abs(ratio - targetRatio) <= ASPECT_TOLERANCE) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize=size;
                    minDiff=Math.abs(size.height - targetHeight);
                }
            }
        }

        // Cannot find the one match the aspect ratio, ignore
        // the requirement

        if (optimalSize == null) {
            minDiff=Double.MAX_VALUE;

            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize=size;
                    minDiff=Math.abs(size.height - targetHeight);
                }
            }
        }

        return(optimalSize);
    }

    public static Size getBestAspectPreviewSize(int displayOrientation,
                                                       int width,
                                                       int height,
                                                       Camera.Parameters parameters) {
        return(getBestAspectPreviewSize(displayOrientation, width, height,
                parameters, 0.0d));
    }

    public static Size getBestAspectPreviewSize(int displayOrientation,
                                                       int width,
                                                       int height,
                                                       Camera.Parameters parameters,
                                                       double closeEnough) {
        double targetRatio=(double)width / height;
        Size optimalSize=null;
        double minDiff=Double.MAX_VALUE;

        if (displayOrientation == 90 || displayOrientation == 270) {
            targetRatio=(double)height / width;
        }

        List<Size> sizes=parameters.getSupportedPreviewSizes();

        Collections.sort(sizes,
                Collections.reverseOrder(new SizeComparator()));

        for (Size size : sizes) {
            double ratio=(double)size.width / size.height;

            if (Math.abs(ratio - targetRatio) < minDiff) {
                optimalSize=size;
                minDiff=Math.abs(ratio - targetRatio);
            }

            if (minDiff < closeEnough) {
                break;
            }
        }

        return(optimalSize);
    }

    private static class SizeComparator implements
            Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            int left=lhs.width * lhs.height;
            int right=rhs.width * rhs.height;

            if (left < right) {
                return(-1);
            }
            else if (left > right) {
                return(1);
            }

            return(0);
        }
    }

    public static void choosePreviewSize(Camera.Parameters parms, int width, int height) {
        // We should make sure that the requested MPEG size is less than the preferred
        // size, and has the same aspect ratio.
        Size ppsfv = parms.getPreferredPreviewSizeForVideo();
        if (ppsfv != null) {
            Log.wtf(TAG, "Camera preferred preview size for video is " +
                    ppsfv.width + "x" + ppsfv.height);
        }

        //for (Camera.Size size : parms.getSupportedPreviewSizes()) {
        //    Log.d(TAG, "supported: " + size.width + "x" + size.height);
        //}

        for (Size size : parms.getSupportedPreviewSizes()) {
            if (size.width == width && size.height == height) {
                parms.setPreviewSize(width, height);
                return;
            }
        }

        Log.wtf(TAG, "Unable to set preview size to " + width + "x" + height);
        if (ppsfv != null) {
            parms.setPreviewSize(ppsfv.width, ppsfv.height);
        }
        // else use whatever the default size is
    }

    public static int chooseFixedPreviewFps(Camera.Parameters parms, int desiredThousandFps) {
        List<int[]> supported = parms.getSupportedPreviewFpsRange();

        for (int[] entry : supported) {
            //Log.d(TAG, "entry: " + entry[0] + " - " + entry[1]);
            if ((entry[0] == entry[1]) && (entry[0] == desiredThousandFps)) {
                parms.setPreviewFpsRange(entry[0], entry[1]);
                return entry[0];
            }
        }

        int[] tmp = new int[2];
        parms.getPreviewFpsRange(tmp);
        int guess;
        if (tmp[0] == tmp[1]) {
            guess = tmp[0];
        } else {
            guess = tmp[1] / 2;     // shrug
        }

        Log.wtf(TAG, "Couldn't find match for " + desiredThousandFps + ", using " + guess);
        return guess;
    }
}
