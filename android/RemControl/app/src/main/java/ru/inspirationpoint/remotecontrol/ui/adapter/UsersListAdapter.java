package ru.inspirationpoint.remotecontrol.ui.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.Fighter;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.OrdersViewHolder> {

    private final Activity mActivity;
    private OnItemClickListener mItemClickListener;
    private Fighter[] mUserArray;

    public UsersListAdapter(Activity activity) {
        mActivity = activity;
        notifyDataSetChanged();
    }

    public void setUsers(Fighter[] userArray) {
        mUserArray = userArray;
        notifyDataSetChanged();
    }

    public boolean hasUsers() {
        return mUserArray != null && mUserArray.length > 0;
    }

    @Override
    public int getItemCount() {
        if (mUserArray != null) {
            return mUserArray.length;
        }
        return 0;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View orderItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_list_item, parent, false);
        return new OrdersViewHolder(mActivity, orderItem);
    }

    @Override
    public void onBindViewHolder(final OrdersViewHolder holder, final int position) {
        holder.mUserName.setText(mUserArray[position].name);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Fighter fighter);
    }

    class OrdersViewHolder extends RecyclerView.ViewHolder {

        Activity mActivity;
        TextView mUserName;

        OrdersViewHolder(Activity activity, View view) {
            super(view);

            mActivity = activity;
            mUserName = view.findViewById(R.id.user_name);

            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, mUserArray[getLayoutPosition()]);
                    }
                }
            });
        }
    }
}
