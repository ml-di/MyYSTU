package ru.ystu.myystu.Database.Converters;

import androidx.room.TypeConverter;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Objects;

import ru.ystu.myystu.AdaptersData.NewsItemsPhotoData;

public class ListJsonConverter {

    @TypeConverter
    public String toJson (ArrayList<NewsItemsPhotoData> list) {

        final JSONObject jsonObject = new JSONObject();
        final JSONArray jsonArray = new JSONArray();

        for (NewsItemsPhotoData parcel : list) {
            final int id = parcel.getId();
            final int uid = parcel.getUid();
            final int width = parcel.getWidth();
            final int height = parcel.getHeight();
            final String urlPreview = parcel.getUrlPreview();
            final String urlFull = parcel.getUrlFull();

            final JSONObject photoObject = new JSONObject();
            photoObject.put("id", id);
            photoObject.put("uid", uid);
            photoObject.put("width", width);
            photoObject.put("height", height);
            photoObject.put("urlPreview", urlPreview);
            photoObject.put("urlFull", urlFull);

            jsonArray.add(photoObject);
        }

        jsonObject.put("photos", jsonArray);

        return jsonObject.toJSONString();
    }

    @TypeConverter
    public ArrayList<NewsItemsPhotoData> toList (String json) {

        final JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;

        try {
            jsonObject = (JSONObject) jsonParser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = (JSONArray) jsonObject.get("photos");

        ArrayList<NewsItemsPhotoData> tempList = new ArrayList<>();

        if (jsonArray != null) {
            for (Object asd : jsonArray) {

                final int id = ((Long) Objects.requireNonNull(((JSONObject) asd).get("id"))).intValue();
                final int uid = ((Long) Objects.requireNonNull(((JSONObject) asd).get("uid"))).intValue();
                final int height = ((Long) Objects.requireNonNull(((JSONObject) asd).get("height"))).intValue();
                final int width = ((Long) Objects.requireNonNull(((JSONObject) asd).get("width"))).intValue();
                final String urlPreview = (String) ((JSONObject) asd).get("urlPreview");
                final String urlFull = (String) ((JSONObject) asd).get("urlFull");

                tempList.add(new NewsItemsPhotoData(id, uid, height, width, urlPreview, urlFull));
            }
        }

        return tempList;
    }

}
