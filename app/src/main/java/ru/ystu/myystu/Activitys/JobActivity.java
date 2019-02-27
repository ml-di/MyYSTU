package ru.ystu.myystu.Activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.JobItemsAdapter;
import ru.ystu.myystu.AdaptersData.JobItemsData;

public class JobActivity extends AppCompatActivity {

    private final String url = "https://www.ystu.ru/learning/placement/"; // Url страницы трудоустройство сайта ЯГТУ
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressJob;
    private ArrayList<JobItemsData> mList;
    private Parcelable mRecyclerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        final Toolbar toolbar = findViewById(R.id.toolBar_job);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerView = findViewById(R.id.recycler_job_items);
        progressJob = findViewById(R.id.progress_job);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        if(savedInstanceState == null){
            new Thread(() -> {
                try {
                    mList = new ArrayList<>();
                    new GetHtmlTask().execute(url).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).start();
        } else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerViewAdapter = new JobItemsAdapter(mList, getApplicationContext());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mRecyclerState != null)
            mLayoutManager.onRestoreInstanceState(mRecyclerState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_job, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_job_openInBrowser) {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", mList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
    }

    // Загрузка html страницы и ее парсинг
    class GetHtmlTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressJob.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mRecyclerViewAdapter = new JobItemsAdapter(mList, getApplicationContext());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            progressJob.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(final String... urls) {

            Document doc = null;
            try {
                doc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements els = null;
            if (doc != null) {
                els = doc.getElementsByClass("Tabs").select("table")
                        .get(1).select("tbody").select("tr");
            }

            String organization;
            String post;
            String url;
            String date;

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

                                if(url.startsWith("/files"))
                                    url = "https://www.ystu.ru" + url;

                                date = els.get(i).select("td").get(3).text();
                                mList.add(new JobItemsData(id, organization, post, url, date));
                                id++;
                            }
                        }
                    }
                }
            }

            return null;
        }
    }


}
