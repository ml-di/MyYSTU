package ru.ystu.myystu.Network;

import android.os.Parcelable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;
import ru.ystu.myystu.AdaptersData.UsersItemsData;

public class GetListUsersFromURL {

    public Single<ArrayList<Parcelable>> getSingleUsersList (String url, ArrayList<Parcelable> mList) {
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

                                Document doc = null;
                                try {
                                    doc = Jsoup.connect(url).get();
                                } catch (IOException e) {
                                    e.printStackTrace();

                                    if(!emitter.isDisposed())
                                        emitter.onError(e);
                                    client.dispatcher().executorService().shutdown();
                                    client.connectionPool().evictAll();
                                }
                                Elements els = null;
                                if (doc != null) {
                                    els = doc.getElementsByClass("user user--big");
                                }

                                if(mList.size() > 0)
                                    mList.clear();

                                mList.add(new ToolbarPlaceholderData(0));

                                String url;
                                String image;
                                String name;
                                String information;

                                if (els != null) {

                                    for (Element el : els) {
                                        url = "https://www.ystu.ru" + el.select("a").attr("href");

                                        try {
                                            image = el.getElementsByClass("user__image").get(0).attr("style");
                                            image = "https://www.ystu.ru" + image.substring(image.indexOf("url('") + 5, image.lastIndexOf("')"));
                                        } catch (Exception e) {
                                            image = null;
                                        }

                                        name = el.getElementsByClass("user-information__name").get(0).text();
                                        information = el.getElementsByClass("user-information__description user-information__description_color_black")
                                                .get(0).text();

                                        mList.add(new UsersItemsData(url, image, name, information));
                                    }

                                } else  {
                                    if(!emitter.isDisposed())
                                        emitter.onError(new IllegalArgumentException("Not found"));
                                }

                                if(!emitter.isDisposed()){
                                    if(mList.size() < 1)
                                        emitter.onError(new IllegalArgumentException("Not found"));
                                    else
                                        emitter.onSuccess(mList);
                                }

                            } catch (Exception e) {
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
