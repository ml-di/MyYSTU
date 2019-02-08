package ru.ystu.myystu.adaptersData;

public class MenuItemsData {

    private int id;
    private int icon;
    private String text;

    public MenuItemsData(int id, int icon, String text) {
        this.id = id;
        this.icon = icon;
        this.text = text;
    }

    public int getId() {
        return id;
    }
    public int getIcon() {
        return icon;
    }
    public String getText() {
        return text;
    }
}
