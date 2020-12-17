package ru.inspirationpoint.remotecontrol.ui.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.inspirationpoint.remotecontrol.databinding.CameraItemBinding;
import ru.inspirationpoint.remotecontrol.manager.coreObjects.Device;

public class CamerasAdapter extends RecyclerView.Adapter<CamerasAdapter.CamerasHolder>{

    private final ArrayList<Device> cameras = new ArrayList<>();
    private OnCamClickListener listener;

    public void setCameras(ArrayList<Device> input) {
        cameras.clear();
        cameras.addAll(input);
        notifyDataSetChanged();
    }

    public void setListener(OnCamClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CamerasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CameraItemBinding binding = CameraItemBinding.inflate(inflater, parent, false);
        return new CamerasHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CamerasHolder holder, int position) {
        holder.binding.setCamera(cameras.get(position));
    }

    @Override
    public int getItemCount() {
        return cameras.size();
    }

    class CamerasHolder extends RecyclerView.ViewHolder{

        CameraItemBinding binding;

        public CamerasHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(v -> listener.onCameraClick(cameras.get(getLayoutPosition())));
        }
    }

    public interface OnCamClickListener {
        void onCameraClick(Device camera);
    }
}
