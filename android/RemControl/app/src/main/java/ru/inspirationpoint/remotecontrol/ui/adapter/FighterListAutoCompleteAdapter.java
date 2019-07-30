package ru.inspirationpoint.remotecontrol.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.ListUser;


public class FighterListAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private final Context mContext;
    private List<ListUser> mResults;
    private List<ListUser> mExcludeUserList;
    private boolean mIncludeMe;

    public FighterListAutoCompleteAdapter(Context context, boolean includeMe) {
        mContext = context;
        mIncludeMe = includeMe;
    }

    public void setExcludeUser(ListUser user) {
        if (user == null) {
            mExcludeUserList = null;
        } else {
            mExcludeUserList = new ArrayList<>();
            mExcludeUserList.add(user);
        }
    }

    public void setExcludeUserList(List<ListUser> userList) {
        mExcludeUserList = userList;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public ListUser getItem(int index) {
        return mResults.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.drop_down_item, parent, false);
        }
        ListUser listUser = getItem(position);
        ((TextView) convertView.findViewById(R.id.text)).setText(listUser.name);
        ((TextView) convertView.findViewById(R.id.nick)).setText(listUser.nick);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<ListUser> fighters = findFighters(constraint.toString());
                    if (fighters != null) {
                        // Assign the data to the FilterResults
                        filterResults.values = fighters;
                        filterResults.count = fighters.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<ListUser>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    /**
     * Returns a search result for the given fighter filter.
     */
    private List<ListUser> findFighters(String searchString) {
        List<ListUser> userList = null;
//        try {
//            userList = new ArrayList<>();
//            ListUser[] users = DataManager.instance().getUsersAddedMeList(mContext);
//            for (int i = 0; i < users.length && i < MAX_RESULTS; ++i) {
//                if (users[i].name.toLowerCase().contains(searchString.toLowerCase()) ||
//                        users[i].nick.toLowerCase().contains(searchString.toLowerCase())) {
//                    userList.add(users[i]);
//                }
//            }
//        } catch (NetworkException e) {
//        }
        return userList;
    }
}
