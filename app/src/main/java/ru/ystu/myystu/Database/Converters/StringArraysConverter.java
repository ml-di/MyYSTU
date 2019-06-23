package ru.ystu.myystu.Database.Converters;

import androidx.room.TypeConverter;

public class StringArraysConverter {

    @TypeConverter
    public String toString (String[] array) {
        String response = "";
        for (String s : array)
            response += s + "`";

        return response.substring(0, response.length() - 1);
    }

    @TypeConverter
    public String[] toStringArray (String str) {
        return str.split("`");
    }

}
