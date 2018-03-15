package ru.inspirationpoint.inspirationrc.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FightData;

public class FightListAdapter extends RecyclerView.Adapter<FightListAdapter.FightViewHolder> {

    private Activity mActivity;
    private OnItemClickListener mItemClickListener;

    public boolean isFromName = false;

    public void setFromName(boolean fromName) {
        isFromName = fromName;
    }

    private ArrayList<FightData> mFightDataList;

    public FightListAdapter(Activity activity) {
        mActivity = activity;
        notifyDataSetChanged();
    }

    public void setFightDataList(ArrayList<FightData> fightDataList) {
        mFightDataList = fightDataList;
        notifyDataSetChanged();
    }

    public ArrayList<FightData> getmFightDataList() {
        return mFightDataList;
    }

    public boolean hasFightData() {
        return mFightDataList != null && mFightDataList.size() > 0;
    }

    @Override
    public int getItemCount() {
        if (mFightDataList != null) {
            return mFightDataList.size();
        }
        return 0;
    }

    @Override
    public FightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View trainingSessionItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.fight_list_item, parent, false);
        return new FightViewHolder(mActivity, trainingSessionItem);
    }

    @Override
    public void onBindViewHolder(final FightViewHolder holder, final int position) {
        FightData fightData = mFightDataList.get(position);
        Date date = new Date();
        date.setTime(fightData.getTime().getTime());
        holder.mTime.setText((new SimpleDateFormat(isFromName ? "dd.MM" : "HH:mm", Locale.getDefault())).format(date));
        holder.mNames.setText(mActivity.getString(R.string.fighter_names, fightData.getLeftFighter().getName(), fightData.getRightFighter().getName()));
        holder.mScore.setText(mActivity.getString(R.string.fighter_score, fightData.getLeftFighter().getScore(), fightData.getRightFighter().getScore()));
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, FightData fightData);
    }

    class FightViewHolder extends RecyclerView.ViewHolder {
        Activity mActivity;
        TextView mTime;
        TextView mNames;
        TextView mScore;

        FightViewHolder(Activity activity, View view) {
            super(view);

            boolean isDark = SettingsManager.getValue(Constants.IS_DARK_THEME, false);
            mActivity = activity;
            mTime = (TextView) view.findViewById(R.id.time);
            mTime.setTextColor(isDark ? mActivity.getResources().getColor(R.color.calendar_color_dark_theme) :
                    mActivity.getResources().getColor(R.color.calendar_color));
            mNames = (TextView) view.findViewById(R.id.names);
            mNames.setTextColor(isDark ? mActivity.getResources().getColor(R.color.calendar_color_dark_theme) :
                    mActivity.getResources().getColor(R.color.calendar_color));
            mScore = (TextView) view.findViewById(R.id.score);
            mScore.setTextColor(isDark ? mActivity.getResources().getColor(R.color.calendar_color_dark_theme) :
                    mActivity.getResources().getColor(R.color.calendar_color));

            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, mFightDataList.get(getLayoutPosition()));
                    }
                }
            });
        }
    }
}
