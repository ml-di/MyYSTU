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
    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
