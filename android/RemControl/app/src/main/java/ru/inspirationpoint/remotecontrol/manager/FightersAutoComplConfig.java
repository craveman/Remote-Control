package ru.inspirationpoint.remotecontrol.manager;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import ru.inspirationpoint.remotecontrol.InspirationDayApplication;
import ru.inspirationpoint.remotecontrol.R;
import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.ui.adapter.FightersAutoCompleteAdapter;
import ru.inspirationpoint.remotecontrol.ui.view.FightersAutoCompleteTextView;

public class FightersAutoComplConfig extends BaseObservable {

    private int treshold = 1;
    private FightersAutoCompleteAdapter adapter;
    private ProgressBar indicator;
    private AdapterView.OnItemClickListener listener;
    private TextWatcher watcher;

    @Bindable
    public int getTreshold() {
        return treshold;
    }

    public void setTreshold(int treshold) {
        this.treshold = treshold;
    }

    @Bindable
    public FightersAutoCompleteAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FightersAutoCompleteAdapter adapter) {
        this.adapter = adapter;
    }

    @Bindable
    public ProgressBar getIndicator() {
        return indicator;
    }

    public void setIndicator(ProgressBar indicator) {
        this.indicator = indicator;
    }

    @Bindable
    public AdapterView.OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Bindable
    public TextWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(TextWatcher watcher) {
        this.watcher = watcher;
    }

    @BindingAdapter("app:configuration")
    public static void configureAutoComplete (AppCompatActivity activity, FightersAutoCompleteTextView textView, FightersAutoComplConfig config) {
        if (SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false)) {
            textView.setDropDownBackgroundResource(R.color.colorPrimaryVeryDark);
            textView.setTextColor(InspirationDayApplication.getApplication().getResources().getColor(R.color.whiteCard));
        }
        textView.setThreshold(config.getTreshold());
        textView.setAdapter(config.getAdapter());
        textView.setLoadingIndicator(config.getIndicator());
        textView.setOnItemClickListener(config.getListener());
        textView.addTextChangedListener(config.getWatcher());
    }
}
