package ru.inspirationpoint.inspirationrc.rc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.List;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.SettingsManager;
import ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants;

public class CalendarDialog extends DialogFragment {

    private DateListener mDateListener;
    private MaterialCalendarView view;
    private boolean mulltiple = true;

    public static void show(FragmentActivity fragmentActivity, boolean multiple) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        CalendarDialog dialog = new CalendarDialog();
        Bundle params = new Bundle();
        params.putBoolean("multiple", multiple);
        dialog.setArguments(params);
        dialog.show(manager, "Calendar");
    }

//    @Override
//    public void show(FragmentManager manager, String tag) {
//        try {
//            FragmentTransaction ft = manager.beginTransaction();
//            ft.add(this, tag);
//            ft.commit();
//        } catch (IllegalStateException e) {
//            Log.d("Dialog", "Exception", e);
//        }
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false) ?
                new AlertDialog.Builder(getActivity(), R.style.AppThemeDark_AlertDialogTheme) :
                new AlertDialog.Builder(getActivity());

        View contentView = inflater.inflate(R.layout.calendar_dlg, null);

        mulltiple = getArguments().getBoolean("multiple", true);
        view = contentView.findViewById(R.id.calendarView);
        view.setSelectionMode(mulltiple? MaterialCalendarView.SELECTION_MODE_MULTIPLE : MaterialCalendarView.SELECTION_MODE_SINGLE);
        boolean isDark = SettingsManager.getValue(CommonConstants.IS_DARK_THEME, false);
        view.setArrowColor(isDark ? getResources().getColor(R.color.whiteCard) : getResources().getColor(R.color.colorPrimaryThemeDark));
        view.setHeaderTextAppearance(isDark ? R.style.CustomTextAppearanceDark : R.style.CustomTextAppearance);
        view.setWeekDayTextAppearance(isDark ? R.style.CustomTextAppearanceDark : R.style.CustomTextAppearance);
        view.setDateTextAppearance(isDark ? R.style.CustomTextAppearanceDark : R.style.CustomTextAppearance);

        view.invalidateDecorators();


        TextView titleTextView = contentView.findViewById(R.id.title);
//        titleTextView.setText(R.string.date_pick);
        titleTextView.setVisibility(View.GONE);
        ImageButton closeButton = contentView.findViewById(R.id.close);
        closeButton.setVisibility(View.GONE);

        Dialog d = builder.setView(contentView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDateListener != null) {
                            mDateListener.onDatesSet(view.getSelectedDates());
                        }
                    }
                }).show();

        Button btn1 = d.getWindow().findViewById(android.R.id.button1);

        btn1.setTypeface(InspirationDayApplication.getCustomFontTypeface());
        btn1.setTextColor(getActivity().getResources().getColor(R.color.textColorSecondary));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn1.getLayoutParams();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        btn1.setLayoutParams(params);

        return d;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DateListener) {
            mDateListener = (DateListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mDateListener = null;
    }

    public interface DateListener {
        void onDatesSet(List<CalendarDay> days);
    }

}
