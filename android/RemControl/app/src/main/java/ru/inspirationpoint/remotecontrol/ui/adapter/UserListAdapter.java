package ru.inspirationpoint.remotecontrol.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.ListUser;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UsersViewHolder> {

    private Activity mActivity;
    private ArrayList<ListUser> mUserList;
    private OnUserRemoveListener mUserRemoveListener;

    public void setOnUserRemoveListener(final OnUserRemoveListener userRemoveListener) {
        mUserRemoveListener = userRemoveListener;
    }

    public interface OnUserRemoveListener {
        void onUserRemove(int position, ListUser user);
    }

    public UserListAdapter(Activity activity) {
        mActivity = activity;
        notifyDataSetChanged();
    }

    public void setUserList(ArrayList<ListUser> userList) {
        mUserList = userList;
        notifyDataSetChanged();
    }

    public ArrayList<ListUser> getUserList() {
        return mUserList;
    }

    public int addUser(ListUser user) {
        int pos = mUserList.size();
        mUserList.add(user);
        notifyItemInserted(pos);
        return pos;
    }

    @Override
    public int getItemCount() {
        if (mUserList == null)
            return 0;
        return mUserList.size();
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View orderItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new UsersViewHolder(mActivity, orderItem);
    }

    @Override
    public void onBindViewHolder(final UsersViewHolder holder, final int position) {
        holder.mUserName.setText(mUserList.get(position).name);
    }

    class UsersViewHolder extends RecyclerView.ViewHolder {

        Activity mActivity;
        TextView mUserName;

        UsersViewHolder(Activity activity, View view) {
            super(view);

            mActivity = activity;
            mUserName = view.findViewById(R.id.user_name);
            mUserName.setTextColor(mActivity.getResources().getColor(R.color.textColorSecondary));

            mUserName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    ListUser user = mUserList.remove(pos);
                    notifyItemRemoved(pos);
                    if (mUserRemoveListener != null) {
                        mUserRemoveListener.onUserRemove(pos, user);
                    }
                }
            });
        }
    }
}
