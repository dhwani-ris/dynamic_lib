package com.dhwaniris.dynamicForm.customViews;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.interfaces.SelectListener;

import java.util.Calendar;


public class CustomDatePicker extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1900;

    public final static int YY = 11;
    public final static int MY = 12;

    private int minYear = 1900, minMonth = 1;

    private QuestionBean questionBean;

    private SelectListener listener;
    private int maxYear = 0;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.dy_date_picker_dialog, null);
//        ButterKnife.bind(this, dialog);
        final NumberPicker pickerDay = (NumberPicker) dialog.findViewById(R.id.picker_day);
        final NumberPicker pickerMonth = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker pickerYear = (NumberPicker) dialog.findViewById(R.id.picker_year);
        final LinearLayout yearP = (LinearLayout) dialog.findViewById(R.id.yearP);
        final LinearLayout monthP = (LinearLayout) dialog.findViewById(R.id.monthP);
        final LinearLayout dayP = (LinearLayout) dialog.findViewById(R.id.dayP);

        final int year = cal.get(Calendar.YEAR);

        pickerDay.setMinValue(1);
        pickerDay.setMaxValue(31);

        pickerDay.setValue(cal.get(Calendar.DAY_OF_MONTH));

        if (year == minYear && minMonth != 12) {
            pickerMonth.setMinValue(minMonth + 1);
        } else {
            pickerMonth.setMinValue(1);
            if (minMonth == 12) {
                minYear++;
            }
        }
        pickerMonth.setMaxValue(12);
        pickerMonth.setValue(cal.get(Calendar.MONTH) + 1);

        if (maxYear != 0) {
            pickerYear.setMaxValue(maxYear);
        } else {
            pickerYear.setMaxValue(year);
        }

        if (minYear != 0) {
            pickerYear.setMinValue(minYear);
        } else {
            pickerYear.setMinValue(year);
        }
        pickerYear.setValue(year);
        int mod = year % 4;
        if (mod == 0 && pickerMonth.getValue() == 2) {
            pickerDay.setMaxValue(29);
        } else if (pickerMonth.getValue() == 2) {
            pickerDay.setMaxValue(28);
        }

        pickerMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int moda = year % 4;
                if (moda == 0 && pickerMonth.getValue() == 2) {
                    pickerDay.setMaxValue(29);
                    pickerMonth.invalidate();
                } else if (pickerMonth.getValue() == 2) {
                    pickerDay.setMaxValue(28);
                    pickerMonth.invalidate();
                } else if (pickerMonth.getValue() == 4 || pickerMonth.getValue() == 6 ||
                        pickerMonth.getValue() == 9 || pickerMonth.getValue() == 11) {
                    pickerDay.setMaxValue(30);
                    pickerMonth.invalidate();
                } else {
                    pickerDay.setMaxValue(31);
                    pickerMonth.invalidate();
                }
            }
        });

        pickerYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                int mod = pickerYear.getValue() % 4;
                if (mod == 0 && pickerMonth.getValue() == 2) {
                    pickerDay.setMaxValue(29);
                    pickerMonth.invalidate();
                } else if (pickerMonth.getValue() == 2) {
                    pickerDay.setMaxValue(28);
                    pickerMonth.invalidate();
                } else if (pickerMonth.getValue() == 4 || pickerMonth.getValue() == 6 ||
                        pickerMonth.getValue() == 9 || pickerMonth.getValue() == 11) {
                    pickerDay.setMaxValue(30);
                    pickerMonth.invalidate();
                } else {
                    pickerDay.setMaxValue(31);
                    pickerMonth.invalidate();
                }

                if (minMonth == 12) {
                    minYear--;
                }
                if (newVal == minYear && minYear != 0) {
                    pickerMonth.setMinValue(minMonth + 1);
                    pickerMonth.setMaxValue(12);
                    pickerMonth.invalidate();
                } else if (minYear != 0) {
                    pickerMonth.setMinValue(1);
                    pickerMonth.setMaxValue(12);
                    pickerMonth.invalidate();
                }
            }
        });

        pickerMonth.setDisplayedValues(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "June", "July",
                "Aug", "Sep", "Oct", "Nov", "Dec"});

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        listener.DateSelector("" + (pickerDay.getValue() < 10 ? 0 : "") + pickerDay.getValue(), "" + (pickerMonth.getValue() < 10 ? 0 : "") + pickerMonth.getValue(),
                                "" + pickerYear.getValue(), questionBean);

                       /* listener.DateSelector("" + pickerDay.getValue(), "" + pickerMonth.getValue(),
                                "" + pickerYear.getValue(), questionBean);
                  */
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CustomDatePicker.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public void setListener(SelectListener listener, QuestionBean questionBean, int maxYear) {
        this.listener = listener;
        this.questionBean = questionBean;
        this.maxYear = maxYear;
    }

    public void setListener(SelectListener listener, QuestionBean questionBean, int minMonth, int minYear, int maxYear) {
        this.listener = listener;
        this.questionBean = questionBean;
        this.minMonth = minMonth;
        this.minYear = minYear;
        this.maxYear = maxYear;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View rootView = super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
