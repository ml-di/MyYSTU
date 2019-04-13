package ru.ystu.myystu.Network;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetFullEventFromURL {

    public Observable<String> getObservableEventFull (String url) {
        return Observable.create(emitter -> {
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
                                    els = doc.getElementsByClass("page-main-content-event__description");
                                }

                                if (els != null) {
                                    emitter.onNext("title: " + els.text());

                                    els = doc.getElementsByClass("page-main-content-event-speeker").get(0).children();
                                    els.last().children().remove();
                                    els.select("img").remove();

                                    emitter.onNext(els.html() + "<br><br>");
                                }

                                if(!emitter.isDisposed()){
                                    emitter.onComplete();
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
