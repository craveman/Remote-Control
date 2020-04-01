package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.ui.adapter.VideoReplaysAdapter;

public class ReplaysDialog extends DialogFragment implements VideoReplaysAdapter.OnReplayClickListener {

    private ReplaysListener listener;

    private static final String ITEMS = "items";

    public static ReplaysDialog show(FragmentActivity fragmentActivity, JSONArray array) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        ReplaysDialog myDialogFragment = new ReplaysDialog();
        Bundle params = new Bundle();
        ArrayList<String> itemsArray = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                itemsArray.add(array.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        params.putStringArrayList(ITEMS, itemsArray);
        myDialogFragment.setArguments(params);
        myDialogFragment.show(manager, "ReplaysDialog");
        return myDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
            AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                    new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                    new AlertDialog.Builder(getActivity());

            View contentView = inflater.inflate(R.layout.dlg_title_layout, null);
            ArrayList<String> items = Objects.requireNonNull(getArguments()).getStringArrayList(ITEMS);
            VideoReplaysAdapter adapter = new VideoReplaysAdapter();
            adapter.setClickListener(this);
            adapter.setItems(items);
            DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL);
            decoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getActivity(), R.color.white)));
            RecyclerView recyclerView = contentView.findViewById(R.id.videos_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
            TextView btnClose = contentView.findViewById(R.id.videos_cancel);
            btnClose.setOnClickListener(view -> dismiss());
        return builder.setView(contentView).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ReplaysListener) {
            listener= (ReplaysListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }

    @Override
    public void onReplayClick(String itemName) {
        if (listener != null) {
            listener.onReplaySelected(itemName);
        }
    }

    public interface ReplaysListener {
        void onReplaySelected (String name);
    }
}
