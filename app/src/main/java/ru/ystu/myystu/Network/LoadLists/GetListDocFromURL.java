package ru.ystu.myystu.Network.LoadLists;

import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.AdaptersData.StoItemsData_Doc;
import ru.ystu.myystu.AdaptersData.StoItemsData_Subtitle;
import ru.ystu.myystu.AdaptersData.StoItemsData_Title;

public class GetListDocFromURL {

    public Single<ArrayList<Parcelable>> getSingleDocList (String url, ArrayList<Parcelable> mList) {

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

                                final Elements els = doc.getElementsByClass("single-page-description").first().children();

                                // TITLE
                                if (els.select("h4.detail-danger") != null && els.select("h4.detail-danger").first().text().length() > 0) {
                                    mList.add(new StoItemsData_Title(els.select("h4.detail-danger").first().text()));
                                }

                                for (Element el : els) {
                                    // SUBTITLE
                                    if (el.is("p")
                                            && el.text().length() > 5) {
                                        mList.add(new StoItemsData_Subtitle(el.text()));
                                    }

                                    // DOCUMENTS
                                    if (el.is("div.page-main-content__spoiler.js-spoiler")) {

                                        for (Element e : el.children()) {

                                            if (e.is("p") && !e.children().isEmpty()) {
                                                String url = "https://www.ystu.ru";
                                                String summary = null;
                                                String fileName = "";
                                                String fileExt = null;

                                                for (Element doc_e : e.getAllElements()) {

                                                    // URL
                                                    if (doc_e.is("a") && doc_e.attr("href").length() > 1) {
                                                        url += doc_e.attr("href");
                                                        fileExt = url.substring(url.lastIndexOf(".") + 1);

                                                        if (doc_e.text().length() > 1) {
                                                            fileName = doc_e.text().replaceAll("&nbsp;", "");
                                                        }
                                                    }
                                                }

                                                for (Element doc_e : e.children()) {
                                                    // SUMMARY
                                                    if (doc_e.is("span")
                                                            && doc_e.children().isEmpty()
                                                            && doc_e.text().length() > 1) {
                                                        summary = doc_e.text();
                                                    }
                                                }

                                                mList.add(new StoItemsData_Doc(fileName, fileExt, summary, url));

                                                int test = 0;
                                            }
                                        }
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
