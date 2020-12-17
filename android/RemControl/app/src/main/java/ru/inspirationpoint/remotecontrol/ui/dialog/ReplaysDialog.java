package ru.inspirationpoint.remotecontrol.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

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

        View contentView = inflater.inflate(R.layout.dlg_videos, null);
        ArrayList<String> items = Objects.requireNonNull(getArguments()).getStringArrayList(ITEMS);
        VideoReplaysAdapter adapter = new VideoReplaysAdapter();
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        RecyclerView recyclerView = contentView.findViewById(R.id.videos_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);
        adapter.setClickListener(this);
        adapter.setItems(items);
        Log.wtf("ADAPTER", adapter.getItemCount() + "");
        return builder.setView(contentView).setNeutralButton(getResources().getString(R.string.cancel),
                (dialogInterface, i) -> dismiss()).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ReplaysListener) {
            listener = (ReplaysListener) context;
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
            dismiss();
        }
    }

    public interface ReplaysListener {
        void onReplaySelected(String name);
    }
}
