package ru.ystu.myystu.Network;

import android.os.Parcelable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.AdaptersData.NewsItemsData;
import ru.ystu.myystu.AdaptersData.NewsItemsData_DontAttach;
import ru.ystu.myystu.AdaptersData.NewsItemsPhotoData;

public class GetListNewsFromURL {

    public Observable<ArrayList<Parcelable>> getObservableNewsList (String url, boolean isOffset, ArrayList<Parcelable> mList){

        final Observable<ArrayList<Parcelable>> observableNews = Observable.create(emitter -> {

            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
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

                                    emitter.onError(e);
                                    client.dispatcher().executorService().shutdown();
                                    client.connectionPool().evictAll();
                                }

                                final JSONObject response_object = (JSONObject) obj;
                                final JSONArray response_json = (JSONArray) response_object.get("response");

                                int id = 0;

                                if (mList.size() > 1){

                                    if(mList.get(mList.size() - 1) instanceof NewsItemsData)
                                        id = ((NewsItemsData) mList.get(mList.size() - 1)).getId();
                                    else if (mList.get(mList.size() - 1) instanceof NewsItemsData_DontAttach)
                                        id = ((NewsItemsData_DontAttach) mList.get(mList.size() - 1)).getId();

                                    if(!isOffset){
                                        mList.clear();
                                        id = 0;
                                    }
                                }

                                for (int i = 1; i < response_json.size(); i++) {

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
                                        if (Objects.equals(typePost, "post")) {

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

                                                // Фото записей
                                                if(item.get("attachments") != null){

                                                    final JSONArray attachmentsPhoto = (JSONArray)item.get("attachments");

                                                    for (int at = 0; at < attachmentsPhoto.size(); at++){

                                                        final JSONObject attachment = (JSONObject) attachmentsPhoto.get(at);

                                                        if(Objects.requireNonNull(attachment.get("type")).equals("photo")){

                                                            final JSONObject photo = (JSONObject) attachment.get("photo");

                                                            final String urlFull;
                                                            final String urlPreview = (String) Objects.requireNonNull(photo).get("src_big");
                                                            final int width = ((Long) Objects.requireNonNull(photo.get("width"))).intValue();
                                                            final int height = ((Long) Objects.requireNonNull(photo.get("height"))).intValue();

                                                            // Получение оригинала
                                                            if(photo.get("src_xxxbig") != null)
                                                                urlFull  = (String) Objects.requireNonNull(photo).get("src_xxxbig");
                                                            else if (photo.get("src_xxbig") != null)
                                                                urlFull  = (String) Objects.requireNonNull(photo).get("src_xxbig");
                                                            else if (photo.get("src_xbig") != null)
                                                                urlFull  = (String) Objects.requireNonNull(photo).get("src_xbig");
                                                            else
                                                                urlFull  = (String) Objects.requireNonNull(photo).get("src_big");

                                                            photoList.add(new NewsItemsPhotoData(height, width, urlPreview, urlFull));
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

                                emitter.onNext(mList);
                                emitter.onComplete();

                            } catch (Exception e){
                                emitter.onError(e);
                            } finally {
                                client.dispatcher().executorService().shutdown();
                                client.connectionPool().evictAll();
                            }
                        }
                    });
        });

        return observableNews;
    }

}
