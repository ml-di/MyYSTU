package ru.ystu.myystu.Utils;

import android.content.Context;

import java.util.Objects;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.ystu.myystu.R;

public class UnixToString {

    private String monthStr;

    public String setUnixToString(String unixTime, Context mContext) {

        //Время записи
        final long timeTemp = Long.parseLong(unixTime);
        final long time = timeTemp * 1000L;

        final Date mDate = new Date(time);
        final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        mSimpleDateFormat.setTimeZone(TimeZone.getDefault());
        final String stringTimeTemp = mSimpleDateFormat.format(mDate);

        String hour = stringTimeTemp.substring(0, 2);
        String minutes = stringTimeTemp.substring(3, 5);
        int day = Integer.valueOf(stringTimeTemp.substring(6, 8));
        int month = Integer.valueOf(stringTimeTemp.substring(9, 11));
        int year = Integer.valueOf(stringTimeTemp.substring(12, 16));

        // Время пользователя
        final Calendar now = Calendar.getInstance( TimeZone.getDefault() );
        final long thisMilliseconds = now.getTimeInMillis();
        now.setTimeInMillis(thisMilliseconds);


        int thisDay = now.get(Calendar.DAY_OF_MONTH);
        int thisMonth = now.get(Calendar.MONTH);
        int thisYear = now.get(Calendar.YEAR);

        thisMonth = thisMonth + 1;

        // 5:03 15:3 -> 05:03 15:03
        if(hour.length() == 1)
            hour = "0" + hour;
        if(minutes.length() == 1)
            minutes = "0" + minutes;

        // Строковый месяц
        switch (month){
            case 1:
                monthStr = mContext.getResources().getString(R.string.time_jan);
                break;
            case 2:
                monthStr = mContext.getResources().getString(R.string.time_feb);
                break;
            case 3:
                monthStr = mContext.getResources().getString(R.string.time_mar);
                break;
            case 4:
                monthStr = mContext.getResources().getString(R.string.time_apr);
                break;
            case 5:
                monthStr = mContext.getResources().getString(R.string.time_may);
                break;
            case 6:
                monthStr = mContext.getResources().getString(R.string.time_jun);
                break;
            case 7:
                monthStr = mContext.getResources().getString(R.string.time_jul);
                break;
            case 8:
                monthStr = mContext.getResources().getString(R.string.time_aug);
                break;
            case 9:
                monthStr = mContext.getResources().getString(R.string.time_sep);
                break;
            case 10:
                monthStr = mContext.getResources().getString(R.string.time_oct);
                break;
            case 11:
                monthStr = mContext.getResources().getString(R.string.time_nov);
                break;
            case 12:
                monthStr = mContext.getResources().getString(R.string.time_dec);
                break;
        }


        String stringTime;
        if(Objects.equals(day, thisDay) && Objects.equals(month, thisMonth) && Objects.equals(year, thisYear))
            stringTime = mContext.getResources().getString(R.string.time_today) + ", " + hour + ":" + minutes;
        else
        if(Objects.equals(day, thisDay -1) && Objects.equals(month, thisMonth) && Objects.equals(year, thisYear))
            stringTime = mContext.getResources().getString(R.string.time_yesterday) + "а, " + hour + ":" + minutes;
        else
        if(Objects.equals(year, thisYear))
            stringTime = day + " " + monthStr + ", " + hour + ":" + minutes;
        else
            stringTime = day + " " + monthStr + ", " + year + ", " + hour + ":" + minutes;

        return stringTime;
    }

}
