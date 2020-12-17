package ru.inspirationpoint.remotecontrol.ui.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.Training;
import ru.inspirationpoint.remotecontrol.manager.helpers.Helper;

public class TrainingListAdapter extends RecyclerView.Adapter<TrainingListAdapter.OrdersViewHolder> {

    private final Activity mActivity;
    private OnItemClickListener mItemClickListener;
    private final int count;
    private Training[] mTrainingArray;

    public TrainingListAdapter(Activity activity, int count) {
        mActivity = activity;
        this.count = count;
        notifyDataSetChanged();
    }

    public void setTrainingArray(Training[] trainingArray) {
        if (trainingArray != null) {
//            for (int i = 0, j = trainingArray.length - 1; i < trainingArray.length / 2; i++, j--) {
//                Training tmp = trainingArray[i];
//                trainingArray[i] = trainingArray[j];
//                trainingArray[j] = tmp;
//            }
            mTrainingArray = new Training[trainingArray.length];
            System.arraycopy(trainingArray, 0, mTrainingArray, 0, trainingArray.length);
        }
        notifyDataSetChanged();
    }

    public Training[] getTrainingArray() {
        return mTrainingArray;
    }

    public boolean hasTrainings() {
        return mTrainingArray != null && mTrainingArray.length > 0;
    }

    @Override
    public int getItemCount() {
        if (mTrainingArray != null) {
            return mTrainingArray.length;
        }
        return 0;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View orderItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainind_list_item, parent, false);
        return new OrdersViewHolder(mActivity, orderItem);
    }

    @Override
    public void onBindViewHolder(final OrdersViewHolder holder, final int position) {
        Date date = Helper.serverStringToDate(mTrainingArray[position].date);
        if (date != null) {
            holder.mDate.setText(Helper.dateToString(date));
        } else {
            holder.mDate.setText("");
        }

        String address = mTrainingArray[position].address;
        if (TextUtils.isEmpty(address)) {
            address = mActivity.getString(R.string.not_detected);
        }
        holder.mPlace.setText(address);
        holder.mFightNumber.setText(mActivity.getString(R.string.fight_count, mTrainingArray[position].fightsCount));
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Training training);
    }

    public class OrdersViewHolder extends RecyclerView.ViewHolder {

        Activity mActivity;
        TextView mDate;
        TextView mPlace;
        TextView mFightNumber;

        OrdersViewHolder(Activity activity, View view) {
            super(view);

            mActivity = activity;
            mDate = view.findViewById(R.id.date);
            mPlace = view.findViewById(R.id.place);
            mFightNumber = view.findViewById(R.id.fight_number);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, mTrainingArray[getLayoutPosition()]);
                    }
                }
            });
        }
    }
}
