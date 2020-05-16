package ru.inspirationpoint.remotecontrol.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import ru.inspirationpoint.remotecontrol.R;

public class VideoReplaysAdapter extends RecyclerView.Adapter<VideoReplaysAdapter.VideoReplayHolder> {

    private ArrayList<String> items;
    private OnReplayClickListener listener;

    public void setItems (ArrayList<String> list) {
        items = list;
        Collections.sort(items, (s, t1) -> {
            String param1 = s.split("#")[1];
            String param2 = t1.split("#")[1];
            SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss", Locale.getDefault());
            Long val1 = 0L;
            Long val2 = 0L;
            try {
                val1 = Objects.requireNonNull(sdf.parse(param1)).getTime();
                val2 = Objects.requireNonNull(sdf.parse(param2)).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return val1.compareTo(val2);
        });
        notifyDataSetChanged();
    }

    public void setClickListener(OnReplayClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoReplayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View replayItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_replays_dialog, parent, false);
        return new VideoReplayHolder(replayItem);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoReplayHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VideoReplayHolder extends RecyclerView.ViewHolder {

        private String item;
        private TextView scoreText;
        private TextView timeText;

        public void setItem (String item) {
            this.item = item;
            scoreText.setText(item.split("#")[0]);
            timeText.setText(item.split("#")[1].replace("_", ":").replace(".mp4", ""));
        }

        VideoReplayHolder(View itemView) {
            super(itemView);
            scoreText = itemView.findViewById(R.id.tv_video_score);
            timeText = itemView.findViewById(R.id.tv_video_time);
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onReplayClick(item);
                }
            });

        }
    }

    public interface OnReplayClickListener {
        void onReplayClick (String itemName);
    }
}
