package ru.ystu.myystu.Database.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "schedule_change")
public class ScheduleChangeBDData {

    @PrimaryKey
    @ColumnInfo(name = "uid")
    private int uid;

    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "text")
    private String text;

    public ScheduleChangeBDData(int uid,
                                int id,
                                String text) {
        this.uid = uid;
        this.id = id;
        this.text = text;
    }


    public int getUid() {
        return uid;
    }
    public int getId() {
        return id;
    }
    public String getText() {
        return text;
    }
}
