package ru.inspirationpoint.remotecontrol.manager;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.rc.ui.adapter.FightersAutoCompleteAdapter;
import ru.inspirationpoint.inspirationrc.rc.ui.view.FightersAutoCompleteTextView;
import ru.inspirationpoint.remotecontrol.InspirationDayApplication;

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
