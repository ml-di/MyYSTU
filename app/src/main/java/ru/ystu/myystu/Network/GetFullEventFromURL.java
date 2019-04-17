package ru.ystu.myystu.Network;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

                                Elements els_title = null;
                                Elements els_content = null;
                                Elements els_additionals = null;
                                Elements els_documents = null;

                                if (doc != null) {
                                    els_title = doc.getElementsByClass("page-main-content-event__description");
                                    els_content = doc.getElementsByClass("page-main-content-event-speeker").get(0).children();

                                    els_additionals = doc.getElementsByClass("page-main-sitebar-event").get(0).children();
                                    if (doc.getElementsByClass("page-main-sitebar-event").size() > 1) {
                                        for (int i = 1; i < doc.getElementsByClass("page-main-sitebar-event").size(); i++) {
                                            els_additionals.add(doc.getElementsByClass("page-main-sitebar-event").get(i).select("h2").get(0));
                                            els_additionals.add(doc.getElementsByClass("page-main-sitebar-event").get(i).select("p").get(0));
                                        }
                                    }

                                    if(doc.getElementsByClass("page-main-sitebar-documents page-main-sitebar-documents--event-detail").size() > 0) {
                                        els_documents = doc.getElementsByClass("page-main-sitebar-documents page-main-sitebar-documents--event-detail").get(0).children();
                                    }
                                }

                                if(!emitter.isDisposed()) {
                                    // Заголовок
                                    if (els_title != null) {
                                        emitter.onNext("title: " + els_title.text());
                                    }

                                    // Контент
                                    if(els_content != null) {
                                        els_content.last().children().remove();
                                        els_content.select("img").remove();
                                        emitter.onNext("cont: " + els_content.html());
                                    }

                                    // Доп информация
                                    if(els_additionals != null) {
                                        for (int i = 0; i < els_additionals.size(); i += 2) {
                                            final String title = els_additionals.get(i).select("h2").text();
                                            final String description = els_additionals.get(i + 1).select("p").text();
                                            emitter.onNext("addit: " + title + "*" + description);
                                        }
                                    }

                                    // Документы
                                    if(els_documents != null) {

                                        emitter.onNext("doc_title: " + els_documents.select("div").first().text());

                                        if(els_documents.size() > 1) {
                                            for (int i = 1; i < els_documents.size(); i ++) {
                                                final String link = "http://www.ystu.ru" + els_documents.get(i).select("a").attr("href");
                                                final String name = els_documents.get(i).getElementsByClass("page-main-sitebar-document__name").text();
                                                final String info = els_documents.get(i).getElementsByClass("page-main-sitebar-document__information").text();
                                                emitter.onNext("doc_file: " + name + "*" + link + "`" + info);
                                            }
                                        }
                                    }

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
