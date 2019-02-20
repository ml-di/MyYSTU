package ru.ystu.myystu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.adapters.NewsItemsAdapter;
import ru.ystu.myystu.adaptersData.NewsItemsData_Header;
import ru.ystu.myystu.network.GetListNewsFromURL;

public class NewsFragment extends Fragment {

    private int PHOTO_SIZE = 100;           // Качество загружаемых картинок (50, 100, 200)
    private int OFFSET = 0;                 // Смещение для следующей порции новостей (не менять)
    private int POST_COUNT_LOAD = 20;       // Количество загружаемых постов за раз
    private String OWNER_ID = "-28414014";  // id группы вуза через дефис
    //private String OWNER_ID = "-178529732";  // id группы тестовой
    private String VK_API_VERSION = "5.92"; // Версия API
    private String SERVICE_KEY = "7c2b4e597c2b4e597c2b4e59ef7c43691577c2b7c2b4e5920683355158fece460f119b9"; // Сервисный ключ доступа

    private int postionScroll = 0;
    private boolean isLoad = false;

    private StringBuilder urlBuilder = new StringBuilder();

    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Parcelable> mList;
    private Parcelable mRecyclerState;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CompositeDisposable disposables;
    private GetListNewsFromURL getListNewsFromURL;

    public static NewsFragment newInstance(String param1, String param2) {

        return new NewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);

        disposables = new CompositeDisposable();
        getListNewsFromURL = new GetListNewsFromURL();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disposables.dispose();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mList.add(new NewsItemsData_Header(0, "Тестирую header"));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);


        if(savedInstanceState == null){
            getNews(false);
        } else {
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
            mLayoutManager.onRestoreInstanceState(mRecyclerState);
            mRecyclerViewAdapter = new NewsItemsAdapter(mList, getContext());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
            postionScroll = savedInstanceState.getInt("postionScroll");
        }

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            getNews(false);
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Прокрутили список до конца (5 элемент с конца)
                if( ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() >= mLayoutManager.getItemCount() - 5 && mLayoutManager.getItemCount() > 0 && !isLoad){
                    getNews(true);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", mList);
        outState.putInt("postionScroll", postionScroll);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

        if(view != null){
            mRecyclerView = view.findViewById(R.id.recycler_news_items);
            mSwipeRefreshLayout = view.findViewById(R.id.refresh_news);
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

        if(mRecyclerView != null){
            if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() > 0){
                postionScroll = ((LinearLayoutManager)mLayoutManager).findFirstCompletelyVisibleItemPosition();
                if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() < 10)
                    mRecyclerView.smoothScrollToPosition(0);
                else{
                    mRecyclerView.scrollToPosition(5);
                    mRecyclerView.smoothScrollToPosition(0);
                }
            } else{
                if(postionScroll > 0){
                    mRecyclerView.scrollToPosition(postionScroll - 1);
                    mRecyclerView.smoothScrollToPosition(postionScroll);
                }
            }
        }
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }


    // Запрос к API
    private String getUrl(boolean isOffset){

        if(isOffset)
            OFFSET += POST_COUNT_LOAD;
        else
            OFFSET = 0;

        if(urlBuilder.length() > 0)
            urlBuilder.setLength(0);

        urlBuilder
                .append("https://api.vk.com/method/wall.get?owner_id=")
                .append(OWNER_ID)
                .append("&count=")
                .append(POST_COUNT_LOAD)
                .append("&filter=owner")
                .append("&offset=")
                .append(OFFSET)
                .append("&access_token=")
                .append(SERVICE_KEY)
                .append("&version=")
                .append(VK_API_VERSION);

        return urlBuilder.toString();

    }

    private void getNews(boolean isOffset){

        String url = getUrl(isOffset);

        if(!isLoad) {
            isLoad = true;
            mSwipeRefreshLayout.setRefreshing(true);

            Observable<ArrayList<Parcelable>> observableNewsList = getListNewsFromURL.getObservableNewsList(url, isOffset, mList);
            disposables.add(observableNewsList
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ArrayList<Parcelable>>(){
                @Override
                public void onNext(ArrayList<Parcelable> parcelables) {
                    mList = parcelables;
                }

                @Override
                public void onError(Throwable e) {

                    try {

                        if(mRecyclerViewAdapter == null){
                            mRecyclerViewAdapter = new NewsItemsAdapter(mList, getContext());
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                        }

                        isLoad = false;

                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);

                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    } finally {
                        dispose();
                    }

                }

                @Override
                public void onComplete() {
                    try {

                        isLoad = false;

                        if (isOffset) {
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            mRecyclerViewAdapter = new NewsItemsAdapter(mList, getContext());
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                        }

                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);

                    } finally {
                        dispose();
                    }
                }
            }));

        }
    }
}
