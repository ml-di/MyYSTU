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
import ru.ystu.myystu.Utils.FileInformation;

public class GetListStoFromURL {

    public Single<ArrayList<Parcelable>> getSingleStoList (String url) {

        final ArrayList<Parcelable> mList = new ArrayList<>();

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

                                int index = 0;
                                for (Element el : els) {
                                    // SUBTITLE
                                    if (el.is("h4") && !el.hasClass("detail-danger")) {
                                        mList.add(new StoItemsData_Subtitle(el.select("h4").first().text()));
                                    }

                                    if (el.is("span")) {
                                        if (el.is("span")) {
                                            for (Element e : el.getAllElements()) {
                                                if (e.is("a")) {
                                                    el = e;
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    if (el.is("a")  && el.attr("href").length() > 1) {

                                        // DOCUMENT
                                        String fileName = null;
                                        String summary = null;
                                        final Elements el_childrens = el.getAllElements();

                                        for (Element e : el_childrens) {
                                            if (e.is("span") && e.text().length() > 1) {
                                                fileName = e.text();
                                                break;
                                            }
                                        }
                                        final String url = "https://www.ystu.ru" + el.attr("href");
                                        final String fileExt = url.substring(url.lastIndexOf(".") + 1);

                                        // SUMMARY DOC
                                        if (els.get(index + 1).is("span") && els.get(index + 1).text().length() > 1) {
                                            summary = els.get(index + 1).text();
                                        }

                                        mList.add(new StoItemsData_Doc(fileName, fileExt, summary, url));
                                    }

                                    index++;
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
