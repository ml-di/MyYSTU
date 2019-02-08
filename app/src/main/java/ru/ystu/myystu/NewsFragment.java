package ru.ystu.myystu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.adapters.NewsItemsAdapter;
import ru.ystu.myystu.adaptersData.NewsItemsData;

public class NewsFragment extends Fragment {

    private int PHOTO_SIZE = 100;           // Качество загружаемых картинок (50, 100, 200)
    private int POST_COUNT_LOAD = 50;       // Количество загружаемых постов за раз
    private String OWNER_ID = "-28414014";  // id группы вуза через дефис
    private String VK_API_VERSION = "5.92"; // Версия API
    private String SERVICE_KEY = "7c2b4e597c2b4e597c2b4e59ef7c43691577c2b7c2b4e5920683355158fece460f119b9"; // Сервисный ключ доступа

    private StringBuilder urlBuilder = new StringBuilder();
    private String url;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<NewsItemsData> mList;
    private Parcelable mRecyclerState;

    public static NewsFragment newInstance(String param1, String param2) {

        return new NewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);

        urlBuilder
                .append("https://api.vk.com/method/wall.get?owner_id=")
                .append(OWNER_ID)
                .append("&count=")
                .append(POST_COUNT_LOAD)
                .append("&filter=owner")
                .append("&access_token=")
                .append(SERVICE_KEY)
                .append("&version=")
                .append(VK_API_VERSION);

        url = urlBuilder.toString();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState == null){
            new Thread(new Runnable() {
                public void run() {

                    try {
                        doGetJsonRequest(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        } else {
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
            mLayoutManager.onRestoreInstanceState(mRecyclerState);
            mRecyclerViewAdapter = new NewsItemsAdapter(mList, getContext());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", mList);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

        if(view != null){
            mRecyclerView = view.findViewById(R.id.recycler_news_items);
        }

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mList = new ArrayList<>();
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void scrollTopRecyclerView() {
        if(mRecyclerView != null && ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() > 0)
            mRecyclerView.smoothScrollToPosition(0);
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    private final OkHttpClient client = new OkHttpClient();
    private void doGetJsonRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull final Call call, @NonNull IOException e) {
                        // Ошибка
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

                        String news_list_json = null;

                        if (response.body() != null) {
                            news_list_json = response.body().string();
                        }

                        JSONParser pars = new JSONParser();
                        Object obj = null;
                        try {
                            obj = pars.parse(news_list_json);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        JSONObject response_object = (JSONObject) obj;
                        JSONArray response_json = null;
                        if (response_object != null) {
                            response_json = (JSONArray) response_object.get("response");
                        }

                        if (response_json != null) {

                            if(mList.size() > 0)
                                mList.clear();

                            int id = 0;

                            for (int i=1; i<response_json.size(); i++){

                                /*
                                *   Выбор только главных новостей:
                                *   1) Запись не является рекламной
                                *   2) Текст записи не является пустым
                                *   3) Запись не является репостом
                                */

                                JSONObject item = (JSONObject) response_json.get(i);

                                int isAdsPost = ((Long) Objects.requireNonNull(item.get("marked_as_ads"))).intValue();  // 1 исли запись рекламная
                                // Запись не является рекламной
                                if(!Objects.equals(isAdsPost, 1)){
                                    String typePost = (String) Objects.requireNonNull(item.get("post_type"));           // Тип поста
                                    // Запись не является репостом
                                    if(Objects.equals(typePost, "post")){
                                        String textPost = (String) Objects.requireNonNull(item.get("text"));            // Текст поста
                                        // Текст записи не является пустым
                                        if(!Objects.equals(textPost, "")){
                                            int isPinnedPost = 0;
                                            if(item.get("is_pinned") != null)
                                                isPinnedPost = ((Long) Objects.requireNonNull(item.get("is_pinned"))).intValue();   // 1 если запись закреплена

                                            int idPost = ((Long) Objects.requireNonNull(item.get("id"))).intValue();                // Id поста
                                            int fromIdPost = ((Long) Objects.requireNonNull(item.get("from_id"))).intValue();       // Id отправителя
                                            int datePost = ((Long) Objects.requireNonNull(item.get("date"))).intValue();            // Дата поста в формате unixtime
                                            String urlPost = "https://vk.com/ystu?w=wall" + fromIdPost + "_" + idPost;

                                            id++;
                                            mList.add(new NewsItemsData(id, 0, isPinnedPost, urlPost, String.valueOf(datePost), textPost, null));
                                        }
                                    }
                                }
                            }

                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerViewAdapter = new NewsItemsAdapter(mList, getContext());
                                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                }
                            });
                        }

                    }
                });
    }
}
