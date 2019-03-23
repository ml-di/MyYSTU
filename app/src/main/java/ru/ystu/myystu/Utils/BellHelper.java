package ru.ystu.myystu.Utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.ystu.myystu.R;

public class BellHelper {

    private String stringTimeTemp;
    private Context mContext;
    private int month;

    public BellHelper(Context mContext) {

        this.mContext = mContext;

        final Date mDate = Calendar.getInstance().getTime();
        final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        mSimpleDateFormat.setTimeZone(TimeZone.getDefault());
        stringTimeTemp = mSimpleDateFormat.format(mDate);
        month = Integer.parseInt(stringTimeTemp.substring(stringTimeTemp.indexOf("/") + 1, stringTimeTemp.lastIndexOf("/")));
    }

    public String getHalfYear(){

        if(month > 1 && month < 6)
            return mContext.getResources().getString(R.string.bell_title_two);
        else if (month > 8)
            return mContext.getResources().getString(R.string.bell_title_one);
        else if(month == 1)
            return mContext.getResources().getString(R.string.bell_title_session_one);
        else if(month == 6)
            return mContext.getResources().getString(R.string.bell_title_session_two);
        else return mContext.getResources().getString(R.string.bell_title_three);

    }

    public String getCountWeek() {

        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

        // Второе учебное полугодие
        if(month > 1 && month < 7){
            mCalendar.set(Calendar.MONTH, 1);
            mCalendar.set(Calendar.WEEK_OF_MONTH, 1);
            final int oldWeekOfYear = mCalendar.get(Calendar.WEEK_OF_YEAR);
            mCalendar.clear(Calendar.WEEK_OF_MONTH);

            mCalendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
            mCalendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            final int nowWeekOfYear = mCalendar.get(Calendar.WEEK_OF_YEAR);

            final int week = nowWeekOfYear - oldWeekOfYear;
            if(week > 17 || week < 1)
                return "-";
            else
                return String.valueOf(week);
        }
        // Первое учебное полугодие
        else if (month > 8 || month < 2){

            if(month < 2) {
                return "-";
            } else {
                mCalendar.set(Calendar.MONTH, 8);
                mCalendar.set(Calendar.DAY_OF_MONTH, 1);
                int temp = mCalendar.get(Calendar.DAY_OF_WEEK);

                //mCalendar.clear(Calendar.MONTH);
                mCalendar.clear(Calendar.DAY_OF_MONTH);

                if(temp == 1)
                    temp = 6;
                else
                    temp = temp - 1;

                if(temp > 3)
                    mCalendar.set(Calendar.WEEK_OF_MONTH, 1);
                else
                    mCalendar.set(Calendar.WEEK_OF_MONTH, 0);

                final int oldWeekOfYear = mCalendar.get(Calendar.WEEK_OF_YEAR);
                mCalendar.clear(Calendar.WEEK_OF_MONTH);

                mCalendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
                mCalendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                final int nowWeekOfYear = mCalendar.get(Calendar.WEEK_OF_YEAR);

                final int week = nowWeekOfYear - oldWeekOfYear;
                if(week > 17 || week < 1)
                    return "-";
                else
                    return String.valueOf(week);
            }
        }
        // Лето
        else {
            return "-";
        }
    }

    public String getCountLesson() {

        int hour = Integer.parseInt(stringTimeTemp.substring(0, stringTimeTemp.indexOf(":")));
        int minutes = Integer.parseInt(stringTimeTemp.substring(stringTimeTemp.indexOf(":") + 1, stringTimeTemp.indexOf(" ")));

        /*
        *
        * 8.30-10.00
        * 10.10-11.40
        * 11.50-12-20
        * 12.20-13.50
        * 14.00-15.30
        * 15.40-17.10
        * 17.30-19.00
        *
        * */

        return "";
    }

}
