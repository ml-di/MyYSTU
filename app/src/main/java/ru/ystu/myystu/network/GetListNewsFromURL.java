package ru.ystu.myystu.network;

import android.os.Parcelable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.adaptersData.NewsItemsData_DontAttach;

public class GetListNewsFromURL {
    private final OkHttpClient client = new OkHttpClient();

    public ArrayList<Parcelable> testRequest(String url, boolean isOffset, ArrayList<Parcelable> mList){

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull final Call call, @NonNull IOException e) {
                        // Ошибка
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

                        String news_list_json = null;

                        if (response.body() != null) {
                            news_list_json = response.body().string();
                        }

                        JSONParser pars = new JSONParser();
                        Object obj = null;
                        try {
                            obj = pars.parse(news_list_json);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        JSONObject response_object = (JSONObject) obj;
                        JSONArray response_json = null;
                        if (response_object != null) {
                            response_json = (JSONArray) response_object.get("response");
                        }

                        if (response_json != null) {

                            if(mList.size() > 1 && !isOffset)
                                mList.clear();

                            int id = 0;

                            for (int i=1; i<response_json.size(); i++){

                                /*
                                 *   Выбор только главных новостей:
                                 *   1) Запись не является рекламной
                                 *   2) Текст записи не является пустым
                                 *   3) Запись не является репостом
                                 */

                                JSONObject item = (JSONObject) response_json.get(i);

                                int isAdsPost = ((Long) Objects.requireNonNull(item.get("marked_as_ads"))).intValue();  // 1 если запись рекламная
                                // Запись не является рекламной
                                if(!Objects.equals(isAdsPost, 1)){
                                    String typePost = (String) Objects.requireNonNull(item.get("post_type"));           // Тип поста
                                    // Запись не является репостом
                                    if(Objects.equals(typePost, "post")){
                                        String textPost = (String) Objects.requireNonNull(item.get("text"));            // Текст поста
                                        // Текст записи не является пустым
                                        if(!Objects.equals(textPost, "")){
                                            int isPinnedPost = 0;
                                            if(item.get("is_pinned") != null)
                                                isPinnedPost = ((Long) Objects.requireNonNull(item.get("is_pinned"))).intValue();   // 1 если запись закреплена

                                            int idPost = ((Long) Objects.requireNonNull(item.get("id"))).intValue();                // Id поста
                                            int fromIdPost = ((Long) Objects.requireNonNull(item.get("from_id"))).intValue();       // Id отправителя
                                            int datePost = ((Long) Objects.requireNonNull(item.get("date"))).intValue();            // Дата поста в формате unixtime
                                            String urlPost = "https://vk.com/ystu?w=wall" + fromIdPost + "_" + idPost;

                                            id++;
                                            mList.add(new NewsItemsData_DontAttach(id, 0, isPinnedPost, urlPost, String.valueOf(datePost), textPost, null));
                                        }
                                    }
                                }
                            }
                        }

                    }
                });

        return mList;
    }


}
