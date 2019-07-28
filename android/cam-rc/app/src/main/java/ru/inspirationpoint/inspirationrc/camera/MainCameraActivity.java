package ru.inspirationpoint.inspirationrc.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.camera.utils.AutoFitTextureView;
import ru.inspirationpoint.inspirationrc.camera.utils.VideoHandler;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.cloudManager.CloudRequestManager;
import ru.inspirationpoint.inspirationrc.manager.ftp.ClientRxThread;
import ru.inspirationpoint.inspirationrc.manager.helpers.UDPHelper;

import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.FIGHT_STOP_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.FILE_SENT_TO_SM;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.PING_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.RESET_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.SEND_TO_CLOUD;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.SEND_TO_REP_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.SEND_TO_SM_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.STATE_REQUEST_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.TIMER_START_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.TIMER_STOP_UDP;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.UDPCommands.VIDEO_CUTTED_UDP;


public class MainCameraActivity extends Activity implements VideoHandler.VideoListener,
        UDPHelper.BroadcastListener, ClientRxThread.FileUploadListener {

    private boolean isStopped = false;
    private boolean isBusy = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onVideoCutted(String path) {
        cuttedPath = path;
        isReadyToUpload = true;
        isBusy = false;
        if (!isStopped) {
            startRecordingVideo();
            try {
                udpHelper.sendTargetMessage(VIDEO_CUTTED_UDP, InspirationDayApplication.getApplication().getRcIpForCam());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onVideoMerged() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(String[] msg, String ip) {
        Log.wtf("IN CAMERA UDP", Arrays.toString(msg));
        switch (msg[0]) {
            case RESET_UDP:
                InspirationDayApplication.getApplication().setSmIpForCam("");
                InspirationDayApplication.getApplication().setRcIpForCam("");
                if (mIsRecordingVideo) {
                    mainHandler.sendEmptyMessage(MainHandler.MSG_VIDEO_FINISH);
                }
                Intent intent2 = new Intent(MainCameraActivity.this, CameraStartActivity.class);
                startActivity(intent2);
                finish();
                break;
            case STATE_REQUEST_UDP:
                new Thread(() -> {
                    try {
                        udpHelper.sendTargetMessage(InspirationDayApplication.getApplication().getSmIpForCam(), ip);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            case TIMER_START_UDP:
                startRecordingVideo();
                break;
            case FIGHT_STOP_UDP:
                isStopped = true;
                if (mIsRecordingVideo) {
                    mainHandler.sendEmptyMessage(MainHandler.MSG_VIDEO_FINISH);
                }
                if (!SettingsManager.getValue("REFMODE", "").equals("")) {
                    try {
                        udpHelper.sendTargetMessage("FIEND" ,
                                SettingsManager.getValue("REFMODE", ""));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mainHandler.removeCallbacksAndMessages(null);
                SettingsManager.removeValue("REFMODE");
                Intent intent = new Intent(MainCameraActivity.this, CameraStartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case TIMER_STOP_UDP:
                if (!isBusy) {
                    isBusy = true;
                    Log.wtf("PAUSEF COMM", String.valueOf(mIsRecordingVideo));
                    if (mIsRecordingVideo) {
                        fileName = msg[1];
                        stopRecordingVideo();
                    }
                }
                break;
            case PING_UDP:
                new Thread(() -> {
                    try {
                        udpHelper.sendTargetMessage(String.valueOf(InspirationDayApplication.getApplication().getCamId())
                                + "\0VCAM\0" + InspirationDayApplication.getApplication().getSmIpForCam(), ip);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            case SEND_TO_SM_UDP:

                fileThread = new ClientRxThread(InspirationDayApplication.getApplication().getSmIpForCam(),
                        "test", cuttedPath, MainCameraActivity.this);
                fileThread.start();
                break;
            case SEND_TO_REP_UDP:
                FTPClient con1 = null;

                try {
                    con1 = new FTPClient();
                    con1.connect(msg[1]);
                    con1.setBufferSize(1048576);
                    con1.enterLocalPassiveMode(); // important!

                    if (con1.login("upload", "123")) {
                        con1.setFileType(FTP.BINARY_FILE_TYPE);
                        File temp = new File(cuttedPath);
                        FileInputStream in = new FileInputStream(temp);
                        boolean result = con1.storeFile(temp.getName(), in);
                        in.close();
                        con1.logout();
                        con1.disconnect();
                        new Thread(() -> {
                            try {
                                udpHelper.sendTargetMessage("VIDRDY\0" + temp.getName(), InspirationDayApplication.getApplication().getRcIpForCam());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();

                    } else {
                        Log.wtf("FTP LOGIN", "FAIL");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SEND_TO_CLOUD:
                if (!isCurrentSaved) {
                    if (isReadyToUpload) {
                        Log.wtf("RDY", "+");
                        uploadToCloud();
                    } else {
                        videoHandler.setNeedToUpload(true);
                        Log.wtf("RDY", "-");
                    }
                }
                break;
        }
    }

    public void uploadToCloud() {
        isCurrentSaved = true;
        Log.wtf("UPL TO CLOUD", "CALL");
        new Thread(() -> CloudRequestManager.uploadFile(cuttedPath)).start();
    }

    @Override
    public void onCreated() {

    }

    private boolean isReadyToUpload = false;

    @Override
    public void onUpload() {
        new Thread(() -> {
            try {
                udpHelper.sendTargetMessage(FILE_SENT_TO_SM, InspirationDayApplication.getApplication().getRcIpForCam());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onError() {

    }

    private class MainHandler extends Handler {
        static final int MSG_VIDEO_COMMAND = 0;
        static final int MSG_VIDEO_FINISH = 1;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_VIDEO_COMMAND: {
                    mIsRecordingVideo = false;
                    try {
                        mPreviewSession.stopRepeating();
                        mPreviewSession.abortCaptures();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                    try{
                        mMediaRecorder.stop();
                    }catch(RuntimeException e){
                        //handle the exception
                    }
                    mMediaRecorder.reset();
                    videoHandler.onSegmentCommand(mVideoSegmentPath, fileName);
                    break;
                }
                case MSG_VIDEO_FINISH: {
                    mIsRecordingVideo = false;
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    videoHandler.onSegmentCommand(mVideoSegmentPath, fileName);
                    break;
                }
                default:
                    throw new RuntimeException("Unknown message " + msg.what);
            }
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            openCamera(width, height);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    private Size mPreviewSize;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private boolean mIsRecordingVideo;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private static final String TAG = "Camera2VideoFragment";
    //TODO various resolution
    private int selectedWidth = 1280;
    private int selectedHeight = 720;

    private VideoHandler videoHandler;
    private UDPHelper udpHelper;
    private String cuttedPath;

    private MainHandler mainHandler;

    private int partNum = 0;
    private AutoFitTextureView mTextureView;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mPreviewSession;

    private Integer mSensorOrientation;
    private String mVideoSegmentPath;
    private CaptureRequest.Builder mPreviewBuilder;
    private int rotationAngle = 0;

    private String fileName;

    private ClientRxThread fileThread;

    private boolean isCurrentSaved = false;

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
            mCameraOpenCloseLock.release();
            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Intent intent = new Intent(MainCameraActivity.this, CameraStartActivity.class);
            startActivity(intent);
            finish();
        }

    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        myOrientationEventListener
//                = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL){
//
//            @Override
//            public void onOrientationChanged(int arg0) {
//                if (arg0 > 0 && arg0 < 181) {
//                    rotationAngle = 90;
//                } else {
//                    rotationAngle = 270;
//                }
//                myOrientationEventListener.disable();
//            }};
//
//        if (myOrientationEventListener.canDetectOrientation()){
//            myOrientationEventListener.enable();
//        }
        mainHandler = new MainHandler();
        videoHandler = new VideoHandler(this);
        videoHandler.setListener(this);
        udpHelper = InspirationDayApplication.getApplication().getUdpHelper();
        udpHelper.setListener(this);
        mTextureView = findViewById(R.id.continuousCapture_surfaceView);
        mVideoSize = new Size(selectedWidth, selectedHeight);
        //TODO do resolution change option
//        selectedHeight = getIntent().getIntExtra("height", 720);
//        selectedWidth = getIntent().getIntExtra("width", 1280);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openCamera(int width, int height) {

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.d(TAG, "tryAcquire");
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            assert manager != null;
            String cameraId = manager.getCameraIdList()[0];

            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (map == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }
            mPreviewSize = new Size(selectedWidth, selectedHeight);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            configureTransform(width, height);
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            Toast.makeText(MainCameraActivity.this, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            MainCameraActivity.this.finish();
        } catch (NullPointerException e) {
            Log.wtf("OPEN CAM ERROR", e.getLocalizedMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            synchronized (mCameraOpenCloseLock) {
                                mPreviewSession = session;
                                updatePreview();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(MainCameraActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureTransform(int viewWidth, int viewHeight) {
        int rotation = MainCameraActivity.this.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpMediaRecorder() throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Log.wtf("MR ERR", what + " | " + extra);
            }
        });
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mVideoSegmentPath = getVideoFilePath();
        mMediaRecorder.setOutputFile(mVideoSegmentPath);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(4000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
//        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.prepare();
        Log.wtf("PREPARE", "+");
    }

    private String getVideoFilePath() {
        partNum++;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/MyFolder/";
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        String postfix = ((dir.getAbsolutePath() + "/")
        + "segment" + partNum + ".mp4");
        return postfix;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Log.wtf("CAM CONFIGURED", "+");
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIsRecordingVideo = true;

                            // Start recording
                            mMediaRecorder.start();
                            Log.wtf("TIME START", DateFormat.getTimeInstance().format(new Date(System.currentTimeMillis())));
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, mBackgroundHandler);
        } catch (CameraAccessException | IOException e) {
            Log.wtf("CAM PROBLEM", e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    private void stopRecordingVideo() {
        mainHandler.removeCallbacksAndMessages(null);
        mainHandler.sendEmptyMessageDelayed(MainHandler.MSG_VIDEO_COMMAND, 2700);
        isCurrentSaved = false;
        isReadyToUpload = false;
    }


    @Override
    protected void onDestroy() {
        Log.wtf("ON DESTROY", "++");
        super.onDestroy();
    }
}
//        SurfaceHolder.Callback, Camera.PreviewCallback, UDPHelper.BroadcastListener {
//
//    private static int VIDEO_WIDTH = 640;  // dimensions for 720p video
//    private static int VIDEO_HEIGHT = 480;
//    private static int DESIRED_PREVIEW_FPS = 30;
//
//    private CameraHandlerThread mThread = null;
//
//    private boolean isFirstFrame = true;
//
//    private int previewFormat;
//    private int frameWidth;
//    private int frameHeight;
//    private Camera.Parameters cameraParam;
//    private Rect frameRect;
//
//    private int partNum;
//
//    private Camera mCamera;
//
//    String szBoundaryStart = "\r\n\r\n--myboundary\r\nContent-Type: image/jpeg\r\nContent-Length: ";
//    String szBoundaryDeltaTime = "\r\nDelta-time: 33";
//    String szBoundaryEnd = "\r\n\r\n";
//
//    private File mOutputFile;
//    private boolean mFileSaveInProgress;
//
//    private byte[][] framesArray = new byte[151][];
//    private int framesCounter = 0;
//
//    FileOutputStream fos;
//    BufferedOutputStream bos;
//
//    private SurfaceHolder sh;
//    private boolean isRecording;
//
//
//    private MediaRecorder mMediaRecorder;
//
//    private int stopCounter = 0;
//    private boolean isFightInProgress = true;
//    private UDPHelper udpHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_main_camera);
//
//        VIDEO_WIDTH = getIntent().getIntExtra("width", 1280);
//        VIDEO_HEIGHT = getIntent().getIntExtra("height", 720);
//
//        SurfaceView sv = findViewById(R.id.continuousCapture_surfaceView);
//        sh = sv.getHolder();
//        sh.addCallback(this);
//    }
//
//    private void stopRecord() {
//        if (isRecording) {
//            try {
//                mMediaRecorder.stop();  // stop the recording
//            } catch (RuntimeException e) {
//                Log.wtf("RECORDER STOP", "RuntimeException: stop() is called immediately after start()");
//            }
//            releaseMediaRecorder();
//            mCamera.lock();
//
//            isRecording = false;
//            Toast.makeText(MainCameraActivity.this, "RECORD STOP", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        releaseCamera();
//    }
//
//    @Override
//    public void onBackPressed() {
//        InspirationDayApplication.getApplication().setSmIpForCam("");
//        Intent intent = new Intent(this, CameraStartActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void openCamera() {
//        if (mCamera != null) {
//            throw new RuntimeException("camera already initialized");
//        }
//
//        Camera.CameraInfo info = new Camera.CameraInfo();
//
//        int numCameras = Camera.getNumberOfCameras();
//        for (int i = 0; i < numCameras; i++) {
//            Camera.getCameraInfo(i, info);
//            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                mCamera = Camera.open(i);
//                break;
//            }
//        }
//
//        Camera.Parameters parms = mCamera.getParameters();
//        Log.wtf("PARAMS", parms.getSupportedVideoSizes().toString());
//
////        CameraUtils.choosePreviewSize(parms, desiredWidth, desiredHeight);
//        parms.setFocusMode(FOCUS_MODE_CONTINUOUS_VIDEO);
//        parms.setVideoStabilization(true);
//        parms.setPreviewSize(VIDEO_WIDTH, VIDEO_HEIGHT);
//        if (VIDEO_HEIGHT > 1080) {
//            parms.set("cam_mode", 1);
//        }
//
//        int mCameraPreviewThousandFps = CameraUtils.chooseFixedPreviewFps(parms, DESIRED_PREVIEW_FPS * 1000);
//        Log.wtf("SELECTED FPS", mCameraPreviewThousandFps / 1000 + "");
//
//        mCamera.setParameters(parms);
//        cameraParam = mCamera.getParameters();
//
//        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
//
//        if (display.getRotation() == Surface.ROTATION_0) {
//            mCamera.setDisplayOrientation(90);
//        } else if (display.getRotation() == Surface.ROTATION_270) {
//            mCamera.setDisplayOrientation(180);
//        }
//    }
//
//    private void releaseCamera() {
//        if (mCamera != null) {
//            mCamera.stopPreview();
//            mCamera.release();
//            mCamera = null;
//        }
//        if (mThread != null) {
//            mThread.quit();
//        }
//    }
//
//    private void releaseMediaRecorder() {
//        if (mMediaRecorder != null) {
//            mMediaRecorder.reset();
//            mMediaRecorder.release();
//            mMediaRecorder = null;
//        }
//    }
//
//    private boolean prepareVideoRecorder() {
//        mMediaRecorder = new MediaRecorder();
//
//        mCamera.unlock();
//
//        mMediaRecorder.setCamera(mCamera);
//        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mMediaRecorder.setOutputFile(getVideoFilePath(false));
//        switch (VIDEO_HEIGHT) {
//            case 480:
//                mMediaRecorder.setVideoEncodingBitRate(1700000);
//                break;
//            case 720:
//                mMediaRecorder.setVideoEncodingBitRate(3700000);
//                break;
//            case 1080:
//                mMediaRecorder.setVideoEncodingBitRate(5700000);
//                break;
//            case 1440:
//                mMediaRecorder.setVideoEncodingBitRate(12700000);
//                break;
//            case 2160:
//                mMediaRecorder.setVideoEncodingBitRate(32700000);
//                break;
//        }
//        mMediaRecorder.setVideoFrameRate(30);
//        mMediaRecorder.setVideoSize(VIDEO_WIDTH, VIDEO_HEIGHT);
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//
//        try {
//            mMediaRecorder.prepare();
//        } catch (IllegalStateException e) {
//            Log.wtf("RECORDER START", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
//            releaseMediaRecorder();
//            return false;
//        } catch (IOException e) {
//            Log.wtf("RECORDER START", "IOException preparing MediaRecorder: " + e.getMessage());
//            releaseMediaRecorder();
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onReceive(String[] msg, String ip) {
//        Log.wtf("IN CAMERA UDP", Arrays.toString(msg));
//        switch (msg[0]) {
//            case "RESE":
//                InspirationDayApplication.getApplication().setSmIpForCam("");
//                InspirationDayApplication.getApplication().setRcIpForCam("");
//                stopRecord();
//                Intent intent2 = new Intent(MainCameraActivity.this, CameraStartActivity.class);
//                startActivity(intent2);
//                finish();
//                break;
//            case "STAT":
//                new Thread(() -> {
//                    try {
//                        udpHelper.sendTargetMessage(InspirationDayApplication.getApplication().getSmIpForCam(), ip);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }).start();
//                break;
//            case "STARTF":
//                MainCameraActivity.this.runOnUiThread(() -> {
//                    if (!isRecording) {
//                        new MediaPrepareTask().execute(null, null, null);
//                        isRecording = true;
//                        Toast.makeText(MainCameraActivity.this, "RECORD START", Toast.LENGTH_LONG).show();
//                    }
//                });
//                break;
//            case "STOPF":
//                stopRecord();
//                Intent intent = new Intent(MainCameraActivity.this, CameraStartActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//                break;
//            case "PAUSEF":
//                mThread.createPart();
//                break;
//            case "PING":
//                new Thread(() -> {
//                    try {
//                        udpHelper.sendTargetMessage("VCAM\0" +
//                                String.valueOf(InspirationDayApplication.getApplication().getCamId()) +
//                                InspirationDayApplication.getApplication().getSmIpForCam(), ip);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }).start();
//                break;
//        }
//    }
//
//    @Override
//    public void onCreated() {
//
//    }
//
//    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            if (prepareVideoRecorder()) {
//                mMediaRecorder.start();
//
//                try {
//                    mCamera.reconnect();
//                    isRecording = true;
//                    mCamera.setPreviewCallback(MainCameraActivity.this);
//                    sh.addCallback(MainCameraActivity.this);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                releaseMediaRecorder();
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//
//        }
//    }
//
//    private void startPreview() {
//        if (mCamera != null) {
//            try {
//                mCamera.setPreviewDisplay(sh);
//            } catch (IOException ioe) {
//                throw new RuntimeException(ioe);
//            }
//            mCamera.startPreview();
//            mCamera.setPreviewCallback(this);
//            mCamera.setErrorCallback((error, camera) -> Log.wtf("ERROR CAM", error + ""));
//            new Thread(() -> InspirationDayApplication.getApplication().getUdpHelper().setListener(this)).start();
//        } else {
//            Log.wtf("CAM", "null");
//        }
//    }
//
//    private String getVideoFilePath(boolean part) {
//        if (part) {
//            partNum++;
//        }
//        final File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
//                + (part ? "part" + partNum + ".mjpeg" : "full.mp4");
//    }
//
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        Log.wtf("SURF", "CHANGED");
//        newOpenCamera();
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.d("Surface: ", "surfaceDestroyed holder=" + holder);
//    }
//
//    @Override
//    public void onPreviewFrame(byte[] data, Camera camera) {
////        mCamera.addCallbackBuffer(data);
//        if (isRecording) {
//            if (isFirstFrame) {
//                Camera.Size previewSize = cameraParam.getPreviewSize();
//                previewFormat = cameraParam.getPreviewFormat();
//                frameWidth = previewSize.width;
//                frameHeight = previewSize.height;
//                frameRect = new Rect(0, 0, frameWidth, frameHeight);
//                isFirstFrame = false;
//            }
//            ByteArrayOutputStream jpegByteArrayOutputStream = new ByteArrayOutputStream();
//            YuvImage previewImage = new YuvImage(data, previewFormat, frameWidth, frameHeight, null);
//            previewImage.compressToJpeg(frameRect, 80, jpegByteArrayOutputStream);
//            byte[] jpegByteArray = jpegByteArrayOutputStream.toByteArray();
//            Log.wtf("ORIGINAL", data.length + "|" + previewImage.getYuvData().length + "|COMPRESSED:" + jpegByteArray.length);
//            framesArray[framesCounter] = jpegByteArray;
//            if (framesCounter < 150) {
//                framesCounter++;
//            } else {
//                framesCounter = 0;
//            }
////            Log.wtf("FRAMES", framesCounter + "");
//        }
//    }
//
//    private class CameraHandlerThread extends HandlerThread {
//        Handler mHandler;
//
//        CameraHandlerThread() {
//            super("CameraHandlerThread");
//            start();
//            mHandler = new Handler(getLooper());
//        }
//
//        synchronized void notifyCameraOpened() {
//            notify();
//        }
//
//        void openCam() {
//            mHandler.post(() -> {
//                openCamera();
//                notifyCameraOpened();
//                startPreview();
//            });
//            try {
//                wait();
//            } catch (InterruptedException e) {
//                Log.wtf("NEW CAMERA OPEN", "wait was interrupted");
//            }
//        }
//
//        void releaseCam() {
//
//        }
//
//        void createPart() {
//            Log.wtf("SAVE", "START");
//            mHandler.postDelayed(() -> {
//                isRecording = false;
//                mOutputFile = new File(getVideoFilePath(true));
//                try {
//                    fos = new FileOutputStream(mOutputFile);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                bos = new BufferedOutputStream(fos);
//                for (int i = framesCounter; i < 151 + framesCounter; i++) {
//                    if (framesArray[i < 151 ? i : i - 151] != null) {
//                        byte[] boundaryBytes = (szBoundaryStart + framesArray[i < 151 ? i : i - 151].length + szBoundaryDeltaTime + szBoundaryEnd).getBytes();
//                        try {
//                            bos.write(boundaryBytes);
//                            bos.write(framesArray[i < 151 ? i : i - 151]);
//                            bos.flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                try {
//                    bos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                FTPClient con = null;
//
//                try
//                {
//                    con = new FTPClient();
//                    con.connect(InspirationDayApplication.getApplication().getSmIpForCam());
//                    con.enterLocalPassiveMode(); // important!
//
//                    if (con.login("upload", "123"))
//                    {
//                        con.setFileType(FTP.BINARY_FILE_TYPE);
//                        FileInputStream in = new FileInputStream(mOutputFile);
//                        boolean result = con.storeFile(mOutputFile.getName(), in);
//                        in.close();
//                        if (result) Log.wtf("upload result", "succeeded" + mOutputFile.getName());
//                        con.logout();
//                        con.disconnect();
//                    }
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//                isRecording = true;
//                new Thread(() -> {
//                    try {
//                        InspirationDayApplication.getApplication().getUdpHelper().sendTargetMessage("VIDRDY\0" + mOutputFile.getName(), InspirationDayApplication.getApplication().getRcIpForCam());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }).start();
//                runOnUiThread(() -> Toast.makeText(MainCameraActivity.this, "SAVE COMPLETE:" + mOutputFile.getPath(), Toast.LENGTH_LONG).show());
//                Log.wtf("SAVE", "COMPLETE:" + mOutputFile.getPath());
//            }, 1000);
//        }
//    }
//
//
//    private void newOpenCamera() {
//        if (mThread == null) {
//            mThread = new CameraHandlerThread();
//        }
//
//        synchronized (mThread) {
//            mThread.openCam();
//        }
//    }
//
//    private void newReleaseCamera() {
//        if (mThread != null) {
//            synchronized (mThread) {
//                mThread.releaseCam();
//            }
//        }
//    }
//}