package ru.ystu.myystu.Network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.AdaptersData.OlympItemsData;

public class GetListOlympFromURL {

    public Observable<ArrayList<OlympItemsData>> getObservableOlympList (String url, ArrayList<OlympItemsData> mList){

        final Observable<ArrayList<OlympItemsData>> observableOlymp = Observable.create(emitter -> {

            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            emitter.onError(e);

                            client.dispatcher().executorService().shutdown();
                            client.connectionPool().evictAll();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            try {

                                final Document doc = Jsoup.connect(url).get();
                                final Elements els = doc.getElementById("izd").select("table").select("tbody").select("tr");
                                String title;
                                String textHtml;

                                for(int i = 1; i < els.size(); i++){

                                    title = els.get(i).select("td").get(0).text();
                                    textHtml = els.get(i).select("td").get(1).html();

                                    textHtml = textHtml.replaceAll("href=\"/files", "href=\"https://www.ystu.ru/files");

                                    mList.add(new OlympItemsData(i - 1, title, textHtml));
                                }

                                emitter.onNext(mList);
                                emitter.onComplete();

                            } catch (Exception e){
                                emitter.onError(e);
                            }  finally {
                                client.dispatcher().executorService().shutdown();
                                client.connectionPool().evictAll();
                            }

                        }
                    });
        });

        return observableOlymp;
    }

}