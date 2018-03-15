package ru.inspirationpoint.inspirationrc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.dataEntities.FightActionData;
import ru.inspirationpoint.inspirationrc.manager.helpers.Helper;
import ru.inspirationpoint.inspirationrc.manager.helpers.LocaleHelper;

import static ru.inspirationpoint.inspirationrc.manager.Constants.phrasesEN;
import static ru.inspirationpoint.inspirationrc.manager.Constants.phrasesRU;

public class FightActionsAdapter extends RecyclerView.Adapter<FightActionsAdapter.ItemHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<FightActionData> data;
    private ArrayList<FightActionData> list;
    private boolean isForPDF = false;
    private int left = 0;
    private int right = 0;
    private boolean withPhrases = false;

    public FightActionsAdapter(Context context, boolean isForPDF) {
        mContext = context;
        this.isForPDF = isForPDF;
        mLayoutInflater = LayoutInflater.from(context);
        data = new ArrayList<>();
    }

    public void setData(ArrayList<FightActionData> list) {
        this.list = list;
        data.clear();
        for (FightActionData data0 : list) {
            if (data0.equals(list.get(list.size()-1)) ) {
                data.add(data0);
            } else if (data0.getActionType() != FightActionData.ActionType.Start &&
                    data0.getActionType() != FightActionData.ActionType.Stop &&
                    data0.getActionType() != FightActionData.ActionType.RedCardLeft &&
                    data0.getActionType() != FightActionData.ActionType.RedCardRight) {
                data.add(data0);
            }
        }
        withPhrases = Boolean.parseBoolean(list.get(0).getComment());
        notifyDataSetChanged();
    }

    public void setStartScore (int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int[] getScore() {
        return new int[]{left, right};
    }

    @Override
    public FightActionsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(mLayoutInflater.inflate(R.layout.fight_result_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FightActionsAdapter.ItemHolder holder, int position) {
        FightActionData item = data.get(position);
        holder.setItem(item, position);
    }

    public ArrayList<FightActionData> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView mDate;
        private TextView mScore;
        private TextView mAction;
        private ImageView actionIcon;

        ItemHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(R.id.fight_result_time);
            mScore = (TextView) itemView.findViewById(R.id.fight_result_score);
            mAction = (TextView) itemView.findViewById(R.id.fight_result_action);
            actionIcon = (ImageView) itemView.findViewById(R.id.fight_action_label);
            if (isForPDF) {
                mDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                mScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                mAction.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            }
        }

        void setItem (FightActionData item, int position) {

            mDate.setText(Helper.timeToString(new Date(item.getTime())));
            for (int i = 0; i <= position; i++) {
                if (data.get(i).getActionType() == FightActionData.ActionType.SetScoreLeft) {
                    left = data.get(i).getScore();
                } else if (data.get(i).getActionType() == FightActionData.ActionType.SetScoreRight) {
                    right = data.get(i).getScore();
                }
            }

            mScore.setText(String.format("%s : %s", String.valueOf(left), String.valueOf(right)));
            switch (item.getActionType()) {
                case Start:
                    mAction.setText(mContext.getResources().getString(R.string.end_fight));
//                    mDate.setText(Helper.timeToString(new Date(list.get(list.size()-1).getTime() - list.get(0).getTime())));
                    break;
                case Stop:
                    mAction.setText(mContext.getResources().getString(R.string.end_fight));
//                    mDate.setText(Helper.timeToString(new Date(list.get(list.size()-1).getTime() - list.get(0).getTime())));
                    break;
                case Reset:
                    mAction.setText(mContext.getResources().getString(R.string.reset));
                    break;
                case YellowCardRight:
                    mAction.setText(mContext.getResources().getString(R.string.yc_right));
                    actionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.yellow));
                    break;
                case SetScoreRight:
                    if (item.getPhrase() == 10) {
                        mAction.setText(mContext.getResources().getString(R.string.rc_left));
                        actionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.red));
                    } else if(item.getPhrase() == 15) {
                        mAction.setText(mContext.getResources().getString(R.string.changing_score));
                    } else {
                        mAction.setText(withPhrases ? (LocaleHelper.getLanguage(mContext).equals("ru") ?
                                phrasesRU[item.getPhrase()] : phrasesEN[item.getPhrase()]) : mContext.getResources().getString(R.string.point_left));
                        actionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.green_arrow));
                    }
                    break;
                case SetTime:
                    mAction.setText(mContext.getResources().getString(R.string.set_time,
                            Helper.timeToString(new Date(item.getEstablishedTime()))));
                    mDate.setText(Helper.timeToString(new Date(item.getEstablishedTime())));
                    break;
                case SetPause:
                    mAction.setText(mContext.getResources().getString(R.string.pause_time));
                    break;
                case SetPeriod:
                    mAction.setText(mContext.getResources().getString(R.string.period, item.getFightPeriod()));
                    break;
//                case RedCardLeft:
//                    mAction.setText(mContext.getResources().getString(R.string.rc_left));
//                    actionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.red));
//                    break;
//                case RedCardRight:
//                    mAction.setText(mContext.getResources().getString(R.string.rc_right));
//                    actionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.red));
//                    break;
                case SetScoreLeft:
                    if (item.getPhrase() == 10) {
                        mAction.setText(mContext.getResources().getString(R.string.rc_right));
                        actionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.red));
                    } else if(item.getPhrase() == 15) {
                        mAction.setText(mContext.getResources().getString(R.string.changing_score));
                    } else {
                        mAction.setText(withPhrases ? (LocaleHelper.getLanguage(mContext).equals("ru") ?
                                phrasesRU[item.getPhrase()] : phrasesEN[item.getPhrase()]) : mContext.getResources().getString(R.string.point_right));
                        actionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.red_arrow));
                    }
                    break;
                case SetPriorityLeft:
                    mAction.setText(mContext.getResources().getString(R.string.priority_left));
                    break;
                case YellowCardLeft:
                    mAction.setText(mContext.getResources().getString(R.string.yc_left));
                    actionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.yellow));
                    break;
                case SetPriorityRight:
                    mAction.setText(mContext.getResources().getString(R.string.priority_right));
                    break;
            }
        }
    }
}
