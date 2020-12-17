package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.Camera;

public class VideoDialog extends DialogFragment {

    private ArrayList<Camera> cameras = new ArrayList<>();
    private Map<String, Integer> devices = new HashMap<>();
    private AlertDialog videoDialog;
    private VideoListener mListener;
    private boolean isPlay = false;
    private TextView camerasText;
    private RadioButton camOneBtn;
    private RadioButton camTwoBtn;
    private RadioButton camThreeBtn;


    public void setCameras(ArrayList<Camera> cameras) {
        this.cameras = cameras;
        camerasText.setText(getActivity().getResources().getString(R.string.selected_cam, cameras.get(0).name.get()));
        camOneBtn.setText(cameras.get(0).name.get());
        if (cameras.size() == 2) {
            camTwoBtn.setText(cameras.get(1).name.get());
            camThreeBtn.setVisibility(View.GONE);
        } if (cameras.size() == 3) {
            camThreeBtn.setText(cameras.get(2).name.get());
        } else {
            camThreeBtn.setVisibility(View.GONE);
            camTwoBtn.setVisibility(View.GONE);
        }
    }

    public void setDevices(Map<String, Integer> devices) {
        this.devices = devices;
    }

    public void changeState () {
        getActivity().runOnUiThread(() -> {
            Log.wtf("CHANGE STATE", "IN DLG ++");
            videoDialog.findViewById(R.id.video_dlg_content_lay).setVisibility(View.VISIBLE);
            videoDialog.findViewById(R.id.video_upload_pb).setVisibility(View.GONE);
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());
        View contentView = inflater.inflate(R.layout.video_dlg, null);

        camerasText = contentView.findViewById(R.id.tv_cameras);
        TextView devicesText = contentView.findViewById(R.id.tv_devices);
        TextView speedText = contentView.findViewById(R.id.tv_speed);
        ImageButton playBtn = contentView.findViewById(R.id.btn_play_dlg);
        SeekBar speedSeekBar = contentView.findViewById(R.id.seekbar_speed);
        RadioGroup camGroup = contentView.findViewById(R.id.cameras_group);
        camOneBtn = contentView.findViewById(R.id.btn_cam1);
        camTwoBtn = contentView.findViewById(R.id.btn_cam2);
        camThreeBtn = contentView.findViewById(R.id.btn_cam3);
        camGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.btn_cam1:
                    mListener.onCameraSelect(cameras.get(0));
                    break;
                case R.id.btn_cam2:
                    mListener.onCameraSelect(cameras.get(1));
                    break;
                case R.id.btn_cam3:
                    mListener.onCameraSelect(cameras.get(2));
                    break;
            }
        });
        speedSeekBar.setMax(10);
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedText.setText(getActivity().getResources().getString(R.string.video_speed, progress * 10 + "%"));
                mListener.onVideoSpeedChange(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        speedSeekBar.setProgress(10);
        contentView.findViewById(R.id.btn_cameras).setOnClickListener(v -> {
            camGroup.setVisibility(View.VISIBLE);
        });
        contentView.findViewById(R.id.btn_devices).setOnClickListener(v -> {

        });
        contentView.findViewById(R.id.btn_speed).setOnClickListener(v -> {
            speedSeekBar.setVisibility(View.VISIBLE);
        });
        contentView.findViewById(R.id.btn_close_dlg).setOnClickListener(v -> {
            mListener.onVideoStop();
            videoDialog.dismiss();
        });
        playBtn.setOnClickListener(v -> {
            if (!isPlay) {
                mListener.onVideoPlay();
                isPlay = true;
                playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.dlg_video_pause));
            } else {
                mListener.onVideoPause();
                isPlay = false;
                playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.dlg_video_play));
            }
        });
        videoDialog = builder.setView(contentView).show();
        videoDialog.setCanceledOnTouchOutside(false);
        return videoDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof VideoListener) {
            mListener = (VideoListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public interface VideoListener {
        void onCameraSelect(Camera selectedCamera);
        void onPlayDeviceSelect(int[] devices);
        void onVideoPlay();
        void onVideoPause();
        void onVideoSpeedChange(int speed);
        void onVideoStop();
    }
}
