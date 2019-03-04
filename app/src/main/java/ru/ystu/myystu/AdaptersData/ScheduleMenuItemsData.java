package ru.ystu.myystu.AdaptersData;

public class ScheduleMenuItemsData {

    private final int id;
    private final int icon;
    private final int color;
    private final String text;

    public ScheduleMenuItemsData(final int id,
                                 final int icon,
                                 final int color,
                                 final String text) {
        this.id = id;
        this.icon = icon;
        this.color = color;
        this.text = text;
    }

    public int getId() {
        return id;
    }
    public int getIcon() {
        return icon;
    }
    public int getColor() {
        return color;
    }
    public String getText() {
        return text;
    }
}
