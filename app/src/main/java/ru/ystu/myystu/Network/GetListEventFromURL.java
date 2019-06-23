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
import ru.ystu.myystu.AdaptersData.StringData;
import ru.ystu.myystu.AdaptersData.EventItemsData_Event;
import ru.ystu.myystu.AdaptersData.EventItemsData_Header;
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;

public class GetListEventFromURL {

    public Single<ArrayList<Parcelable>> getSingleEventList (String url, ArrayList<Parcelable> mList){

        return Single.create(emitter -> {

            final OkHttpClient client = new OkHttpClient();
            Request mRequest = new Request.Builder()
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

                                final Document doc = Jsoup.connect(url).get();

                                /*
                                 *       Ссылки в шапке
                                 */

                                final Elements els_header = doc.getElementsByClass("page-header-links").select("a");

                                final String[] titles = new String[els_header.size()];
                                final String[] links = new String[els_header.size()];
                                int selected_id = 0;

                                for (int i = 0; i < els_header.size(); i++) {
                                    titles[i] = "# " + els_header.get(i).text();
                                    links[i] = "https://www.ystu.ru" + els_header.get(i).attr("href");
                                    if(url.equals(links[i])){
                                        selected_id = i;
                                    }
                                }

                                int index = 1;

                                // Добавление пустого пространства под toolbar и шапки
                                mList.add(new ToolbarPlaceholderData(0));
                                mList.add(new EventItemsData_Header(index, titles, links, selected_id));
                                index++;

                                /*
                                 *       Ссылки
                                 */

                                final Elements els_dividers = doc.getElementsByClass("wrapper").select("h2");
                                final Elements els_links = doc.getElementsByClass("news-items wrapper");

                                for (int l = 0; l < els_dividers.size(); l++) {
                                    final String divider = els_dividers.get(l).text();
                                    mList.add(new StringData(index, divider));
                                    index++;

                                    for (Element el : els_links.get(l).children()) {
                                        final String link = "https://www.ystu.ru" + el.select("a").attr("href");
                                        final String title = el.getElementsByClass("doing-item__title").get(0).text();
                                        final String date = el.getElementsByClass("doing-item-image__text doing-item-image__text--date").get(0).text();
                                        final String location = el.getElementsByClass("doing-item-image__text doing-item-image__text--title").get(0).text();

                                        String photoUrl = el.getElementsByClass("doing-item-image").get(0).attr("style");
                                        photoUrl = "https://www.ystu.ru" + photoUrl.substring(photoUrl.indexOf("url('") + 5, photoUrl.lastIndexOf("')"));

                                        mList.add(new EventItemsData_Event(index, link, title, date, location, photoUrl));
                                        index++;
                                    }
                                }

                                if(!emitter.isDisposed()){
                                    if(mList.size() < 1 )
                                        emitter.onError(new IllegalArgumentException("Not found"));
                                    else
                                        emitter.onSuccess(mList);
                                }

                            } catch (Exception e){
                                if(!emitter.isDisposed())
                                    emitter.onError(e);
                            }  finally {
                                client.dispatcher().executorService().shutdown();
                                client.connectionPool().evictAll();
                            }
                        }
                    });
        });
    }
}
