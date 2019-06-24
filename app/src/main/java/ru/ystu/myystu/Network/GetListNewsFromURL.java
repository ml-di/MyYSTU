package ru.ystu.myystu.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.AdaptersData.NewsItemsData;
import ru.ystu.myystu.AdaptersData.NewsItemsData_DontAttach;
import ru.ystu.myystu.AdaptersData.NewsItemsData_Header;
import ru.ystu.myystu.AdaptersData.NewsItemsPhotoData;

public class GetListNewsFromURL {

    public Single<ArrayList<Parcelable>> getSingleNewsList (String url, boolean isOffset, ArrayList<Parcelable> mList, Context mContext){
        return Single.create(emitter -> {

            final OkHttpClient client = new OkHttpClient();
            final Request mRequest = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(mRequest)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            if(!emitter.isDisposed())
                                emitter.onError(e);

                            client.dispatcher().executorService().shutdown();
                            client.connectionPool().evictAll();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {

                            try {
                                String news_list_json = null;

                                if (response.body() != null)
                                    news_list_json = response.body().string();

                                final JSONParser pars = new JSONParser();
                                Object obj = null;
                                try {
                                    obj = pars.parse(news_list_json);
                                } catch (ParseException e) {
                                    e.printStackTrace();

                                    if(!emitter.isDisposed())
                                        emitter.onError(e);
                                    client.dispatcher().executorService().shutdown();
                                    client.connectionPool().evictAll();
                                }

                                final JSONObject response_object = (JSONObject) ((JSONObject) obj).get("response");
                                final JSONArray response_json = (JSONArray) response_object.get("items");

                                int id = 0;

                                if (mList.size() > 0){

                                    if(mList.get(mList.size() - 1) instanceof NewsItemsData)
                                        id = ((NewsItemsData) mList.get(mList.size() - 1)).getId();
                                    else if (mList.get(mList.size() - 1) instanceof NewsItemsData_DontAttach)
                                        id = ((NewsItemsData_DontAttach) mList.get(mList.size() - 1)).getId();

                                    if(!isOffset){
                                        mList.clear();
                                        id = 0;
                                        mList.add(new NewsItemsData_Header(0, "Тестирую header"));
                                    }
                                } else
                                    mList.add(new NewsItemsData_Header(0, "Тестирую header"));

                                if(response_json.size() < 1 && !isOffset && !emitter.isDisposed()){
                                    emitter.onError(new IllegalArgumentException("Not found"));
                                }

                                for (int i = 0; i < response_json.size(); i++) {

                                    /*
                                     *   Выбор только главных новостей:
                                     *   1) Запись не является рекламной
                                     *   2) Текст записи не является пустым
                                     *   3) Запись не является репостом
                                     */

                                    final JSONObject item = (JSONObject) response_json.get(i);

                                    final int isAdsPost = ((Long) Objects.requireNonNull(item.get("marked_as_ads"))).intValue();  // 1 если запись рекламная
                                    // Запись не является рекламной
                                    if (!Objects.equals(isAdsPost, 1)) {

                                        final String typePost = (String) Objects.requireNonNull(item.get("post_type"));           // Тип поста
                                        // Запись не является репостом
                                        if (Objects.equals(typePost, "post") && item.get("copy_history") == null) {

                                            final String textPost = (String) Objects.requireNonNull(item.get("text"));            // Текст поста
                                            // Текст записи не является пустым
                                            if (!Objects.equals(textPost, "")) {

                                                int isPinnedPost = 0;
                                                if (item.get("is_pinned") != null)
                                                    isPinnedPost = ((Long) Objects.requireNonNull(item.get("is_pinned"))).intValue();         // 1 если запись закреплена

                                                final int idPost = ((Long) Objects.requireNonNull(item.get("id"))).intValue();                // Id поста
                                                final int fromIdPost = ((Long) Objects.requireNonNull(item.get("from_id"))).intValue();       // Id отправителя
                                                final int datePost = ((Long) Objects.requireNonNull(item.get("date"))).intValue();            // Дата поста в формате unixtime
                                                final String urlPost = "https://vk.com/wall" + fromIdPost + "_" + idPost;

                                                int signer = -1;
                                                if(item.get("signer_id") != null)
                                                    signer = ((Long) Objects.requireNonNull(item.get("signer_id"))).intValue();                // Владелец записи, если есть

                                                // List с фото
                                                final ArrayList<NewsItemsPhotoData> photoList = new ArrayList<>();
                                                int indexPhoto = 0;
                                                // Фото записей
                                                if(item.get("attachments") != null){

                                                    final JSONArray attachmentsPhoto = (JSONArray)item.get("attachments");

                                                    for (int at = 0; at < attachmentsPhoto.size(); at++){

                                                        final JSONObject attachment = (JSONObject) attachmentsPhoto.get(at);

                                                        if(Objects.requireNonNull(attachment.get("type")).equals("photo")){

                                                            final JSONObject photo = (JSONObject) attachment.get("photo");

                                                            String urlFull = null;
                                                            String urlPreview = null;
                                                            int width = 0;
                                                            int height = 0;

                                                            final JSONArray photoSizes = (JSONArray) photo.get("sizes");

                                                            /*
                                                            *   Размеры фото
                                                            *   min - m
                                                            *   mid - x
                                                            *   high - z
                                                            *   max - w
                                                            * */

                                                            final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                                            final String sizePhoto = sharedPrefs.getString("preference_general_photoSize", "mid");

                                                            String sizePhotoType;

                                                            switch (sizePhoto) {
                                                                case "min":
                                                                    sizePhotoType = "m";
                                                                    break;
                                                                case "mid":
                                                                    sizePhotoType = "x";
                                                                    break;
                                                                case "high":
                                                                    sizePhotoType = "z";
                                                                    break;
                                                                case "max":
                                                                    sizePhotoType = "w";
                                                                    break;
                                                                default:
                                                                    sizePhotoType = "x";
                                                                    break;
                                                            }

                                                            // Все размеры для текущего изображения
                                                            String sizes = null;
                                                            for (Object sizeImage : photoSizes) {
                                                                sizes += ((JSONObject) sizeImage).get("type").toString();
                                                            }

                                                            // Нет нужного размера
                                                            if (!sizes.contains(sizePhotoType))
                                                                sizePhotoType = "x";

                                                            String sizePhotoTypeFull = "w";
                                                            if(!sizes.contains(sizePhotoTypeFull)) {
                                                                if (sizes.contains("z"))
                                                                    sizePhotoTypeFull = "z";
                                                                else if (sizes.contains("x"))
                                                                    sizePhotoTypeFull = "x";
                                                                else if (sizes.contains("m"))
                                                                    sizePhotoTypeFull = "m";
                                                            }

                                                            // Получение превью и оригинала
                                                            for (int p = 0; p < 2; p++) {
                                                                for (Object sizeImage : photoSizes) {
                                                                    final String sizeType = ((JSONObject) sizeImage).get("type").toString();
                                                                    if (p == 0) {
                                                                        if (sizeType.equals(sizePhotoType)) {
                                                                            width = ((Long) ((JSONObject) sizeImage).get("width")).intValue();
                                                                            height = ((Long) ((JSONObject) sizeImage).get("height")).intValue();
                                                                            urlPreview = ((JSONObject) sizeImage).get("url").toString();
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        if (sizeType.equals(sizePhotoTypeFull)) {
                                                                            urlFull = ((JSONObject) sizeImage).get("url").toString();
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            photoList.add(new NewsItemsPhotoData(indexPhoto, id, height, width, urlPreview, urlFull));
                                                            indexPhoto++;
                                                        }
                                                    }
                                                }

                                                id++;

                                                if (photoList.size() > 0)
                                                    mList.add(new NewsItemsData(id, isPinnedPost, signer, urlPost, String.valueOf(datePost), textPost, photoList));
                                                else
                                                    mList.add(new NewsItemsData_DontAttach (id, isPinnedPost, signer, urlPost, String.valueOf(datePost), textPost));
                                            }
                                        }
                                    }
                                }

                                if(!emitter.isDisposed()){
                                    if(mList.size() > 1)
                                        emitter.onSuccess(mList);
                                    else
                                        emitter.onError(new IllegalArgumentException("Not found"));
                                }

                            } catch (Exception e){
                                if(!emitter.isDisposed())
                                    emitter.onError(e);
                            } finally {
                                client.dispatcher().executorService().shutdown();
                                client.connectionPool().evictAll();
                            }
                        }
                    });
        });
    }

}
