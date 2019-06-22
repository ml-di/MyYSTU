package ru.ystu.myystu.Database.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_full")
public class UserFullData {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "detail")
    private String detail;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
}
