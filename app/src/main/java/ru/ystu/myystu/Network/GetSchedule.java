package ru.ystu.myystu.Network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetSchedule {

    final String url = "https://www.ystu.ru/information/students/raspisanie-zanyatiy/";
    // TODO фейк ссылка с расписанием
    //final String url = "https://myystu.000webhostapp.com/myystu/schedule.txt";

    public Observable<String> getLink (int id){

        return Observable.create(emitter -> {

            final OkHttpClient client = new OkHttpClient();
            final String[] prefix_f = new String[]{"строительства и транспорта", "архитектуры и дизайна", "Инженерно", "Автомеханический",
                    "Машиностроительный", "Химико", "Заочный факультет", "Отделение ускоренных"};

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
                                    els = doc.getElementsByClass("single-page-description").select("h3");
                                }

                                if (els != null) {
                                    for (int i = 0; i < els.size(); i++) {
                                        if (els.get(i).text().contains(prefix_f[id])) {
                                            int index = els.get(i).elementSiblingIndex();
                                            final Elements el = doc.getElementsByClass("single-page-description").get(0).children();

                                            Element temp;
                                            while (true) {
                                                temp = el.get(index);
                                                if (temp.tagName().equals("div") && temp.text().length() > 3) {
                                                    break;
                                                }
                                                index++;
                                            }

                                            final Elements files = temp.getAllElements().select("a");
                                            final Elements changes = temp.getAllElements().select("span");

                                            // Ссылки
                                            for (Element lin : files) {
                                                String link = lin.attr("href");

                                                if (link.startsWith("/")) {
                                                    link = "https://www.ystu.ru" + link;
                                                }

                                                final String text = lin.select("a").text();

                                                if (!emitter.isDisposed() && !text.equals("")) {
                                                    emitter.onNext("links:" + link + "*" + text);
                                                }
                                            }

                                            // Изменения
                                            for (Element change : changes) {
                                                if (change.text().length() > 9
                                                        && change.text().contains(".")
                                                        && change.attr("href").isEmpty()) {

                                                    if (!emitter.isDisposed()) {
                                                        emitter.onNext("change:" + change.select("span").text());
                                                    }
                                                }
                                            }

                                            break;
                                        }
                                    }

                                    if (!emitter.isDisposed()) {
                                        emitter.onComplete();
                                    }
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
