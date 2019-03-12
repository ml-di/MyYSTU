package ru.ystu.myystu.Network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.AdaptersData.OlympItemsData;

public class GetListOlympFromURL {

    public Single<ArrayList<OlympItemsData>> getSingleOlympList (String url, ArrayList<OlympItemsData> mList){

        return Single.create(emitter -> {

            final OkHttpClient client = new OkHttpClient();
            Request mRequest = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(mRequest)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if(!emitter.isDisposed())
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

                                if(!emitter.isDisposed())
                                    emitter.onSuccess(mList);

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
