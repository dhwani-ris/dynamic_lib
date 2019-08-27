package com.dhwaniris.dynamicForm.utils;


import android.content.Context;

import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

public class DateUtility {

    /**
     * method to get the expiry date in days and hours format for forms
     *
     * @param value
     * @return
     */
    public static void expiryTimeDifference(long value, TextView textView,
                                            Context context) {
        Date userDate = new Date(value);

        int minimumDaysFormExpiry = 10;
        long difference = userDate.getTime() - Calendar.getInstance().getTimeInMillis();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        long elapsedHours = difference / hoursInMilli;

        if (elapsedDays <= minimumDaysFormExpiry) {
            String concatDayString;
            if (elapsedDays == 1) {
                concatDayString = " day left";
                textView.setTextColor(ContextCompat.getColor(context, R.color.red));
                setTextInTextView(elapsedDays + concatDayString, textView);
            } else if (elapsedDays > 1) {
                concatDayString = " days left";
                textView.setTextColor(ContextCompat.getColor(context, R.color.green));
                setTextInTextView(elapsedDays + concatDayString, textView);
            } else {
                String concatHourString;
                textView.setTextColor(ContextCompat.getColor(context, R.color.red));
                if (elapsedHours == 1) {
                    concatHourString = " hour left";
                    setTextInTextView(elapsedHours + concatHourString, textView);
                } else if (elapsedHours > 1) {
                    concatHourString = " hours left";
                    setTextInTextView(elapsedHours + concatHourString, textView);
                } else {
                    textView.setVisibility(View.GONE);
                }
            }
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.green));
            showDateInNormalFormat(userDate, textView);
        }

    }

    private static void showDateInNormalFormat(Date date, TextView textView) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String dateString = format.format(date);
        textView.setText(dateString);
    }

    private static void setTextInTextView(String text, TextView tv) {
        tv.setText(text);
    }

    public static long todaysDateMillis() {
        return System.currentTimeMillis();
    }

    public static String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date currenTimeZone = calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
            ;
        }
        return "";
    }

    public static String getISO8601StringForDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public static long iso8601StringToUnixTime(String timeStamp) {
        long unixTime = -1;
        TimeZone tzGMT = TimeZone.getTimeZone("GMT");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(tzGMT);
        try {
            // Reminder: getTime returns the number of milliseconds from the
            // Unix epoch.
            unixTime = format.parse(timeStamp).getTime() / 1000;
        } catch (ParseException e) {
            // Return initialized value of -1;
        }
        return unixTime;
    }

    public static String getAgeFromDob(int year, int month, int day) {
        String ages = "0";

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month - 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        ages = ageInt.toString();

        return ages;
    }
}
