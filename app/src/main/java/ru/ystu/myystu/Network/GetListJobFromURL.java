package ru.ystu.myystu.Network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.AdaptersData.JobItemsData;

public class GetListJobFromURL {

    public Observable<ArrayList<JobItemsData>> getObservableJobList (String url, ArrayList<JobItemsData> mList){

        final Observable<ArrayList<JobItemsData>> observableJob = Observable.create(emitter -> {

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

                                Document doc = null;
                                try {
                                    doc = Jsoup.connect(url).get();
                                } catch (IOException e) {
                                    e.printStackTrace();

                                    emitter.onError(e);
                                    client.dispatcher().executorService().shutdown();
                                    client.connectionPool().evictAll();
                                }
                                Elements els = null;
                                if (doc != null) {
                                    els = doc.getElementsByClass("Tabs").select("table")
                                            .get(1).select("tbody").select("tr");
                                }

                                if(mList.size() > 0)
                                    mList.clear();

                                String organization;
                                String post;
                                String url;
                                String date;
                                String fileType;

                                int id = 0;

                                if (els != null) {
                                    for (int i = 1; i < els.size(); i++) {
                                        // Если вакансии заполнены по правильному шаблону
                                        if(els.get(i).select("td").get(0).text().equals("")){
                                            if(els.get(i).select("td").get(1) != null
                                                    && els.get(i).select("td").get(2) != null){
                                                // Отлавливал такое что название организации было в дополнительной таблице, по этому проверку на всякий ¯\_(ツ)_/¯
                                                if(els.get(i).select("td").get(1).childNodeSize() < 2){

                                                    organization = els.get(i).select("td").get(1).text();
                                                    post = els.get(i).select("td").get(2).text();
                                                    url = els.get(i).select("td").get(2).select("a").attr("href");

                                                    if(url.startsWith("/files")){
                                                        url = "https://www.ystu.ru" + url;
                                                        fileType = "FILE";
                                                    } else
                                                        fileType = "LINK";

                                                    date = els.get(i).select("td").get(3).text();
                                                    mList.add(new JobItemsData(id, organization, post, url, date, fileType));
                                                    id++;
                                                }
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

       return observableJob;
    }
}
