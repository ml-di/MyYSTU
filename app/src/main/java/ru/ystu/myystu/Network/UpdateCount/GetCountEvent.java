package ru.ystu.myystu.Network.UpdateCount;

import android.database.sqlite.SQLiteException;
import androidx.annotation.NonNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;

public class GetCountEvent {

    public Single<String> getCountEvent (String url){

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

                                int index = 0;

                                final Document doc = Jsoup.connect(url).get();
                                final Elements events = doc.getElementsByClass("doing-item doing-item--page");

                                try {
                                    final AppDatabase db = Application.getInstance().getDatabase();
                                     for (Element e : events) {
                                         final String link = "https://www.ystu.ru" + e.select("a").attr("href");
                                         if (!db.eventsItemsDao().isExistsEventByLink(link) && link.contains("events")) {
                                             index++;
                                         }
                                     }
                                } catch (SQLiteException ignored) { }

                                if(!emitter.isDisposed()){
                                    emitter.onSuccess("EVENT:" + index);
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
