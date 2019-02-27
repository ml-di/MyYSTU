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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.OlympItemsAdapter;
import ru.ystu.myystu.AdaptersData.OlympItemsData;

public class OlympActivity extends AppCompatActivity {

    private String url = "https://www.ystu.ru/science/olimp/"; // Url страницы олимпиады сайта ЯГТУ
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressJob;
    private ArrayList<OlympItemsData> mList;
    private Parcelable mRecyclerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olymp);

        //region настройка ToolBar
        Toolbar toolbar = findViewById(R.id.toolBar_olymp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //endregion

        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerView = findViewById(R.id.recycler_olymp_items);
        progressJob = findViewById(R.id.progress_olymp);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        if(savedInstanceState == null){
            new Thread(new Runnable() {
                public void run() {
                    try {
                        mList = new ArrayList<>();
                        new GetHtmlTask().execute(url).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerViewAdapter = new OlympItemsAdapter(mList, getApplicationContext());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_olymp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_olymp_openInBrowser) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
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

    class GetHtmlTask extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressJob.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mRecyclerViewAdapter = new OlympItemsAdapter(mList, getApplicationContext());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            progressJob.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... urls) {

            try {
                Document doc = Jsoup.connect(urls[0]).get();
                Elements els = doc.getElementById("izd").select("table").select("tbody").select("tr");
                //Elements els = doc.select("table").get(1).select("tbody").select("tr");
                String title;
                String textHtml;

                for(int i = 1; i < els.size(); i++){

                    title = els.get(i).select("td").get(0).text();
                    textHtml = els.get(i).select("td").get(1).html();

                    textHtml = textHtml.replaceAll("href=\"/files", "href=\"https://www.ystu.ru/files");

                    mList.add(new OlympItemsData(i - 1, title, textHtml));
                }

            }catch (Exception ignored) { }

            return null;
        }
    }
}
