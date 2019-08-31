package ru.ystu.myystu.Utils;

import android.content.Context;
import java.util.Calendar;

import ru.ystu.myystu.R;

public class BellHelper {

    /*
     *     Звонки
     *     8.30-10.00
     *     10.10-11.40
     *     11.50-12-20
     *     12.20-13.50
     *     14.00-15.30
     *     15.40-17.10
     *     17.30-19.00
     */

    private Context mContext;

    public BellHelper(Context mContext) {
        this.mContext = mContext;
    }

    public String getHalfYear(){

        final Calendar mCalendar = Calendar.getInstance();
        final int month =  mCalendar.get(Calendar.MONTH) + 1;

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
        final int month = mCalendar.get(Calendar.MONTH) + 1;
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

                mCalendar.clear(Calendar.DAY_OF_MONTH);

                if(temp == 1)
                    temp = 6;
                else
                    temp = temp - 1;

                if(temp > 3)
                    mCalendar.set(Calendar.WEEK_OF_MONTH, 1);
                else
                    mCalendar.set(Calendar.WEEK_OF_MONTH, 0);

                final int oldWeekOfYear = mCalendar.get(Calendar.WEEK_OF_YEAR) - 1;
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

        final Calendar mCalendar = Calendar.getInstance();
        final int month = mCalendar.get(Calendar.MONTH) + 1;
        if (month > 6 && month < 9) {
            return "-";
        } else {
            // Воскресенье
            if(mCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){

                final int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                final int minute = mCalendar.get(Calendar.MINUTE);
                final int time = hour * 60 + minute;

                if(time >= 510 && time < 600){
                    return "1";
                } else if (time >= 600 && time < 610){
                    return mContext.getResources().getString(R.string.bell_break_one);
                } else if (time >= 610 && time < 700){
                    return "2";
                } else if (time >= 700 && time < 740) {
                    return mContext.getResources().getString(R.string.bell_break_two);
                } else if (time >= 740 && time < 830) {
                    return "3";
                } else if (time >= 830 && time < 840) {
                    return mContext.getResources().getString(R.string.bell_break_one);
                } else if (time >= 840 && time < 930) {
                    return "4";
                } else if (time >= 930 && time < 940) {
                    return mContext.getResources().getString(R.string.bell_break_one);
                } else if (time >= 940 && time < 1030) {
                    return "5";
                } else if (time >= 1030 && time < 1050) {
                    return mContext.getResources().getString(R.string.bell_break_one);
                } else if (time >= 1050 && time < 1140) {
                    return "6";
                } else
                    return "-";
            } else
                return mContext.getResources().getString(R.string.bell_day_off);
        }
    }

    public String getTime() {
        final Calendar mCalendar = Calendar.getInstance();
        final int month = mCalendar.get(Calendar.MONTH) + 1;
        if (month > 6 && month < 9) {
            return "-";
        } else {
            // Воскресенье
            if(mCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){

                final int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                final int minute = mCalendar.get(Calendar.MINUTE);
                final int time = hour * 60 + minute;

                if(time >= 420 && time < 510){
                    return "8:30";
                } else if (time >= 510 && time < 600){
                    return "10:00";
                } else if (time >= 600 && time < 610){
                    return "10:10";
                } else if (time >= 610 && time < 700){
                    return "11:40";
                } else if (time >= 700 && time < 740) {
                    return "12:20";
                } else if (time >= 740 && time < 830) {
                    return "13:50";
                } else if (time >= 830 && time < 840) {
                    return "14:00";
                } else if (time >= 840 && time < 930) {
                    return "15:30";
                } else if (time >= 930 && time < 940) {
                    return "15:40";
                } else if (time >= 940 && time < 1030) {
                    return "17:10";
                } else if (time >= 1030 && time < 1050) {
                    return "17:30";
                } else if (time >= 1050 && time < 1140) {
                    return "19:00";
                } else
                    return "-";
            } else
                return "-";
        }
    }
}
