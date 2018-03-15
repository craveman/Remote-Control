package ru.inspirationpoint.inspirationrc.ui.adapter;

import android.content.Context;
import android.support.v4.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.Constants;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;

public class ClubSpinnerAdapter extends ArrayAdapter<Pair<String, String>> {

    private Context context;

    public ClubSpinnerAdapter(Context context, int resourceId, ArrayList<Pair<String, String>> list) {
        super(context, resourceId, list);
        this.context = context;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(pos, convertView, parent);
        textView.setTextColor(context.getResources().getColor(R.color.textColorSecondary));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, context.getResources().getDimension(R.dimen.large_font_size) / context.getResources().getDisplayMetrics().density);
        textView.setText(getItem(pos).second);
        int paddingPixel = 3;
        float density = context.getResources().getDisplayMetrics().density;
        int paddingDp = (int)(paddingPixel * density);
        textView.setPadding(paddingDp, 0, 0, 0);
        return textView;
    }

    @Override
    public View getDropDownView(int pos, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(pos, convertView, parent);
        textView.setText(getItem(pos).second);
        textView.setTextColor(!SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                context.getResources().getColor(R.color.textColorPrimary) : context.getResources().getColor(R.color.whiteCard));
        textView.setBackgroundColor(!SettingsManager.getValue(Constants.IS_DARK_THEME, false) ?
                context.getResources().getColor(R.color.colorPrimary) : context.getResources().getColor(R.color.colorPrimaryThemeDark));
        return textView;
    }

}
