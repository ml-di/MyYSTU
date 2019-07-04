package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.AdaptersData.NewsItemsData;
import ru.ystu.myystu.AdaptersData.NewsItemsData_DontAttach;
import ru.ystu.myystu.AdaptersData.NewsItemsData_Header;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.DataFragments.DataFragment_News_List;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.NewsItemsAdapter;
import ru.ystu.myystu.Network.GetListNewsFromURL;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.SettingsController;

public class NewsFragment extends Fragment {

    private Context mContext;
    private CoordinatorLayout mainLayout;
    private int OFFSET = 0;                                                                         // Смещение для следующей порции новостей (не менять)
    private int POST_COUNT_LOAD = 20;                                                               // Количество загружаемых постов за раз
    private String OWNER_ID = "-28414014";                                                          // id группы вуза через дефис
    //private String OWNER_ID = "-178529732";                                                       // id группы тестовой
    private String VK_API_VERSION = "5.95";                                                         // Версия API
    private String SERVICE_KEY
            = "7c2b4e597c2b4e597c2b4e59ef7c43691577c2b7c2b4e5920683355158fece460f119b9";            // Сервисный ключ доступа

    private boolean isLoad = false;
    private boolean isEnd = false;

    private StringBuilder urlBuilder = new StringBuilder();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Parcelable> mList;
    private Parcelable mRecyclerState;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CompositeDisposable mDisposables;
    private GetListNewsFromURL getListNewsFromURL;

    private DataFragment_News_List dataFragment_news_list;
    private AppDatabase db;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);

        mContext = getActivity();
        mDisposables = new CompositeDisposable();
        getListNewsFromURL = new GetListNewsFromURL();
        final FragmentManager mFragmentManager = getFragmentManager();

        if (db == null || !db.isOpen())
            db = Application.getInstance().getDatabase();

        if (mFragmentManager != null) {
            dataFragment_news_list = (DataFragment_News_List) mFragmentManager.findFragmentByTag("news_list");

            if (dataFragment_news_list == null) {
                dataFragment_news_list = new DataFragment_News_List();
                mFragmentManager.beginTransaction().add(dataFragment_news_list, "news_list").commit();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isLoad = false;
        final ImagePipeline mImagePipeline = Fresco.getImagePipeline();
        mImagePipeline.clearMemoryCaches();

        if (mDisposables != null)
            mDisposables.dispose();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);

        if(savedInstanceState == null){
            getNews(false);
        } else {
            mList = dataFragment_news_list.getList();
            mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
            mLayoutManager.onRestoreInstanceState(mRecyclerState);
            mRecyclerViewAdapter = new NewsItemsAdapter(mList, getContext());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
            OFFSET = savedInstanceState.getInt("offset");
        }

        mSwipeRefreshLayout.setOnRefreshListener(() -> getNews(false));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Прокрутили список до конца (5 элемент с конца)
                if( ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition()
                        >= mLayoutManager.getItemCount() - 5
                        && mLayoutManager.getItemCount() > 0
                        && !isLoad
                        && !isEnd){
                    getNews(true);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putInt("offset", OFFSET);

        dataFragment_news_list.setList(mList);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_news, container, false);

        if(mView != null){
            mRecyclerView = mView.findViewById(R.id.recycler_news_items);
            mSwipeRefreshLayout = mView.findViewById(R.id.refresh_news);
            mainLayout = getActivity().findViewById(R.id.contentContainer);
        }

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mList = new ArrayList<>();

        return mView;
    }

    public void scrollTopRecyclerView() {

        if(mRecyclerView != null){
            if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() > 0){
                if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() < 10)
                    mRecyclerView.smoothScrollToPosition(0);
                else{
                    mRecyclerView.scrollToPosition(5);
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }
        }
    }

    // Запрос к API
    private String getUrl(final boolean isOffset){

        if(isOffset)
            OFFSET += POST_COUNT_LOAD + 1;
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
                .append("&v=")
                .append(VK_API_VERSION);

        return urlBuilder.toString();
    }

    private void getNews(final boolean isOffset){
        if(NetworkInformation.hasConnection(mContext)){

            final String url = getUrl(isOffset);
            int listCount = mList.size();

            if(!isLoad) {
                isLoad = true;
                mSwipeRefreshLayout.setRefreshing(true);

                final Single<ArrayList<Parcelable>> singleNewsList
                        = getListNewsFromURL.getSingleNewsList(url, isOffset, mList, mContext);
                mDisposables.add(singleNewsList
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ArrayList<Parcelable>>(){

                            @Override
                            public void onSuccess(ArrayList<Parcelable> parcelables) {
                                mList = parcelables;
                                isLoad = false;

                                if (isOffset) {
                                    mRecyclerViewAdapter.notifyItemRangeInserted(listCount,
                                            mList.size() - listCount);
                                } else {
                                    mRecyclerViewAdapter = new NewsItemsAdapter(mList, getContext());
                                    mRecyclerViewAdapter.setHasStableIds(true);
                                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                    setRecyclerViewAnim(mRecyclerView);
                                }
                                mSwipeRefreshLayout.setRefreshing(false);
                                // Конец списка новостей
                                if(isOffset)
                                    isEnd = mList.size() <= listCount;
                                else
                                    mRecyclerView.scheduleLayoutAnimation();

                                new Thread(() -> {
                                    try {
                                        if (db.getOpenHelper().getWritableDatabase().isOpen()) {
                                            // Удаляем все записи, если они есть
                                            if (db.newsItemsDao().getCountNewsAttach() > 0)
                                                db.newsItemsDao().deleteNewsAttach();
                                            if (db.newsItemsDao().getCountNewsDontAttach() > 0)
                                                db.newsItemsDao().deleteNewsDontAttach();
                                            if (db.newsItemsDao().getCountPhotos() > 0)
                                                db.newsItemsDao().deleteNewsAllPhotos();

                                            // Добавляем новые записи
                                            for (Parcelable p : parcelables) {
                                                if (p instanceof NewsItemsData_DontAttach) {
                                                    db.newsItemsDao().insertNewsDontAttach((NewsItemsData_DontAttach) p);
                                                } else if (p instanceof NewsItemsData) {
                                                    db.newsItemsDao().insertNewsAttach((NewsItemsData) p);
                                                }
                                            }
                                        }
                                    } catch (SQLiteException e) {
                                        if (isAdded() && getActivity() != null) {
                                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());
                                        }
                                    }
                                }).start();
                            }

                            @Override
                            public void onError(Throwable e) {

                                isLoad = false;
                                mSwipeRefreshLayout.setRefreshing(false);

                                if(isOffset)
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                else {
                                    if(e.getMessage() != null && e.getMessage().equals("Not found")){
                                        ErrorMessage.showToFragment(mainLayout, 1,
                                                getResources().getString(R.string.error_message_news_not_found_post),
                                                mContext, getTag());
                                    }
                                    else
                                        ErrorMessage.showToFragment(mainLayout, -1, e.getMessage(), mContext, getTag());
                                }
                            }
                        }));
            }
        } else {
            if(!isOffset){
                new Thread(() -> {
                    try {
                        if (db.getOpenHelper().getReadableDatabase().isOpen()) {
                            final int count = db.newsItemsDao().getCountNewsAttach() + db.newsItemsDao().getCountNewsDontAttach();

                            if (count > 0) {
                                if (mList.size() > 0)
                                    mList.clear();

                                mList.add(new NewsItemsData_Header(-1, "Тестирую header"));

                                for (int i = 0; i < count; i++) {

                                    if (db.newsItemsDao().isExistsDontAttach(i)) {
                                        mList.add(db.newsItemsDao().getNewsDontAttach(i));
                                    } else if (db.newsItemsDao().isExistsAttach(i)) {
                                        mList.add(db.newsItemsDao().getNewsAttach(i));
                                    }
                                }

                                mRecyclerViewAdapter = new NewsItemsAdapter(mList, getContext());
                                mRecyclerViewAdapter.setHasStableIds(true);
                                mRecyclerView.post(() -> {
                                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                    setRecyclerViewAnim(mRecyclerView);
                                    Toast.makeText(getContext(), getResources().getString(R.string.toast_no_connection_the_internet), Toast.LENGTH_LONG).show();
                                    mSwipeRefreshLayout.setRefreshing(false);
                                });
                            } else {
                                if(isAdded() && getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        ErrorMessage.showToFragment(mainLayout, 0, null, mContext, getTag());
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    });
                                }
                            }
                        } else {
                            if(isAdded() && getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    ErrorMessage.showToFragment(mainLayout, 0, null, mContext, getTag());
                                    mSwipeRefreshLayout.setRefreshing(false);
                                });
                            }
                        }
                    } catch (SQLiteException e) {
                        if(isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                ErrorMessage.showToFragment(mainLayout, -1, e.getMessage(), mContext, getTag());
                                mSwipeRefreshLayout.setRefreshing(false);
                            });
                        }
                    }
                }).start();
            }
        }
    }

    private void setRecyclerViewAnim (final RecyclerView recyclerView) {
        if (SettingsController.isEnabledAnim(mContext)) {
            final Context context = recyclerView.getContext();
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.layout_news_recyclerview_show);
            recyclerView.setLayoutAnimation(controller);
        } else {
            recyclerView.clearAnimation();
        }
    }
}
