package ru.inspirationpoint.remotecontrol.ui.adapter;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ru.inspirationpoint.remotecontrol.R;

public class SMAdapter extends RecyclerView.Adapter<SMAdapter.SMViewHolder> {

    private final ArrayList<NsdServiceInfo> items = new ArrayList<>();
    private OnSMChooseListener listener;
    private final Context context;

    public SMAdapter(Context context) {
        this.context = context;
    }

    public void addItem(NsdServiceInfo item) {
        boolean exists = false;
        for (int i = 0; i < items.size(); i++) {
            NsdServiceInfo current = items.get(i);
            if (current.getServiceName().equals(item.getServiceName()) &&
            current.getAttributes().equals(item.getAttributes())) {
                exists = true;
            }
        }
        if (!exists) {
            items.add(item);
            notifyDataSetChanged();
        }
    }

    public void removeItem(NsdServiceInfo item) {
        for (int i = 0; i < items.size(); i++) {
            NsdServiceInfo current = items.get(i);
            if (current.getServiceName().equals(item.getServiceName())) {
                items.remove(current);
                notifyDataSetChanged();
            }
        }
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void setClickListener(OnSMChooseListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SMViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View smItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sm_revealed_item, parent, false);
        return new SMViewHolder(smItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SMViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class SMViewHolder extends RecyclerView.ViewHolder {

        private NsdServiceInfo item;
        private final TextView smNameText;
        private final View view;

        public void setItem(NsdServiceInfo info) {
            item = info;
            smNameText.setText(new String(item.getAttributes().get("ID"), StandardCharsets.UTF_8));
            if (new String(item.getAttributes().get("RC"), StandardCharsets.UTF_8).equals("NO")) {
                view.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_sm_active, null));
                view.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onSMChoose(item);
                    }
                });
            } else {
                view.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.button_sm_inactive, null));
                view.setOnClickListener(null);
            }
        }

        public SMViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            smNameText = itemView.findViewById(R.id.sm_name);
        }
    }

    public interface OnSMChooseListener {
        void onSMChoose (NsdServiceInfo itemName);
    }
}
