package ru.ystu.myystu.AdaptersData;

public class MenuItemsData {

    private final int id;
    private final int icon;
    private final String text;

    public MenuItemsData(final int id,
                         final int icon,
                         final String text) {
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
