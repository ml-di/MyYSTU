package ru.ystu.myystu.Network.LoadLists;

import android.os.Parcelable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;

import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.AdaptersData.JobItemsData;
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;

public class GetListJobFromURL {

    public Single<List<Parcelable>> getSingleJobList (String url, List<Parcelable> mList){

        return Single.create(emitter -> {

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
                                    els = doc.getElementsByClass("single-page-description").select("h3,h5");
                                }

                                if(mList.size() > 0)
                                    mList.clear();

                                mList.add(new ToolbarPlaceholderData(0));

                                String organization;
                                String post;
                                String url;
                                String fileType;

                                if (els != null) {

                                    for(int i = 0; i < els.size(); i++){

                                        if(!els.get(i).text().equals("")){

                                            organization = els.get(i).text();

                                            final Element post_el = doc
                                                    .getElementsByClass("single-page-description")
                                                    .get(0);

                                            int a = els.get(i).elementSiblingIndex();

                                            while(true){
                                                if(post_el.child(a).tagName().equals("div")){
                                                    post = post_el.child(a).toString();
                                                    fileType = "TEXT";
                                                    url = null;
                                                    break;
                                                } else if (post_el.child(a).tagName().equals("span")) {
                                                    if(!post_el.child(a).select("a").attr("href").isEmpty()){
                                                        post = post_el.child(a).select("a").attr("href").substring(1);
                                                        fileType = "FILE";
                                                        url = "https://www.ystu.ru" + post_el.child(a).select("a").attr("href");
                                                        break;
                                                    }
                                                }
                                                a++;
                                            }
                                            mList.add(new JobItemsData(i, organization, post, url, fileType));
                                        }
                                    }
                                } else {
                                    if(!emitter.isDisposed())
                                        emitter.onError(new IllegalArgumentException("Not found"));
                                }

                                if(!emitter.isDisposed()){
                                    if(mList.size() < 1)
                                        emitter.onError(new IllegalArgumentException("Not found"));
                                    else
                                        emitter.onSuccess(mList);
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
