package ru.ystu.myystu.utils;

import java.util.Objects;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UnixToString {

    private String monthStr;

    public String setUnixToString(String unixTime) {

        //Время записи
        final long timeTemp = Long.parseLong(unixTime);
        final long time = timeTemp * 1000L;

        final Date date = new Date(time);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        final String stringTimeTemp = simpleDateFormat.format(date);

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
                monthStr = "января";
                break;
            case 2:
                monthStr = "февраля";
                break;
            case 3:
                monthStr = "марта";
                break;
            case 4:
                monthStr = "апреля";
                break;
            case 5:
                monthStr = "мая";
                break;
            case 6:
                monthStr = "июня";
                break;
            case 7:
                monthStr = "июля";
                break;
            case 8:
                monthStr = "августа";
                break;
            case 9:
                monthStr = "сентября";
                break;
            case 10:
                monthStr = "октября";
                break;
            case 11:
                monthStr = "ноября";
                break;
            case 12:
                monthStr = "декабря";
                break;
        }


        String stringTime;
        if(Objects.equals(day, thisDay) && Objects.equals(month, thisMonth) && Objects.equals(year, thisYear))
            stringTime = "Сегодня | " + hour + ":" + minutes;
        else
        if(Objects.equals(day, thisDay -1) && Objects.equals(month, thisMonth) && Objects.equals(year, thisYear))
            stringTime = "Вчера | " + hour + ":" + minutes;
        else
        if(Objects.equals(year, thisYear))
            stringTime = day + " " + monthStr + " | " + hour + ":" + minutes;
        else
            stringTime = day + " " + monthStr + ", " + year + " | " + hour + ":" + minutes;

        return stringTime;
    }

}
