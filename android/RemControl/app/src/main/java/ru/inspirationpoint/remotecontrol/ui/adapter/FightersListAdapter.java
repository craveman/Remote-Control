package ru.inspirationpoint.remotecontrol.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.ListUser;


public class FightersListAdapter extends RecyclerView.Adapter<FightersListAdapter.FightersListViewHolder> {

    private ArrayList<ListUser> statArrayList;
    private OnFighterClickListener mItemClickListener;

    public FightersListAdapter() {
        notifyDataSetChanged();
    }

    public void setData (ArrayList<ListUser> statArrayList) {
        if (statArrayList != null) {
//            Collections.sort(statArrayList, new FightersComparator());
            this.statArrayList = statArrayList;
        }
        notifyDataSetChanged();
    }

//    private class FightersComparator implements Comparator<FighterStat> {
//        public int compare(FighterStat left, FighterStat right) {
//            return right.fightCount - left.fightCount;
//        }
//    }

    public boolean hasFighters () {
        return statArrayList != null && statArrayList.size() > 0;
    }

    @Override
    public FightersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View fighterListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new FightersListViewHolder(fighterListItem);
    }

    @Override
    public void onBindViewHolder(FightersListViewHolder holder, int position) {
        holder.textView.setText(statArrayList.get(position).name);
        holder.textView.setTag(statArrayList.get(position)._id);
    }

    @Override
    public int getItemCount() {
        if (statArrayList != null) {
            return statArrayList.size();
        }
        return 0;
    }

    public void setOnItemClickListener(final OnFighterClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnFighterClickListener {
        void onItemClick(View view, ListUser stat);
    }

    class FightersListViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public FightersListViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.user_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(view, statArrayList.get(getLayoutPosition()));
                    }
                }
            });
        }
    }
}
