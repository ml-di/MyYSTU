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

public class GetUserInformationFromURL {

    public Observable<String> getObservableUserInformation (String url) {
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

                                Elements user_contacts = null;
                                Elements user_detail = null;

                                if (doc != null) {
                                    user_contacts = doc.getElementsByClass("user-detail-contacts").get(0).children();
                                    user_detail = doc.getElementsByClass("user-layout__page").get(0).children();
                                }

                                if (!emitter.isDisposed()) {

                                    // Контакты
                                    if (user_contacts != null) {

                                        for (int i = 0; i < user_contacts.size(); i++) {

                                            if (user_contacts.get(i).text().contains("Адрес")) {
                                                emitter.onNext("adr:" + user_contacts.get(i).getElementsByClass("user-detail-contact__description").text());
                                            }
                                            if (user_contacts.get(i).text().contains("Телефон")) {
                                                emitter.onNext("tel:" + user_contacts.get(i).getElementsByClass("user-detail-contact__description").text());
                                            }
                                            if (user_contacts.get(i).text().contains("Почта")) {
                                                emitter.onNext("email:" + user_contacts.get(i).getElementsByClass("user-detail-contact__description").text());
                                            }
                                        }
                                    }
                                    // О сотруднике
                                    if (user_detail != null) {

                                        for (int i = 0; i < user_detail.size(); i++) {
                                            if (user_detail.get(i).text().contains("О сотруднике")) {
                                                int index = i + 1;
                                                while (true) {
                                                    if (user_detail.get(index).className().equals("user-detail__description")
                                                            && user_detail.get(index).text().length() > 1) {
                                                        emitter.onNext("userDetail:" + user_detail.get(index).text());
                                                        break;
                                                    } else
                                                        index++;
                                                }
                                                break;
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
