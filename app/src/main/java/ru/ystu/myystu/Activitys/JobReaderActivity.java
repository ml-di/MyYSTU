package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.StringFormatter;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;

public class JobReaderActivity extends AppCompatActivity {

    private String content;
    private String title;

    private AppCompatTextView text;
    private NestedScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_reader);

        Bundle mBundle = getIntent().getExtras();
        if(mBundle != null){
            content = mBundle.getString("content");
            title = mBundle.getString("title");
        }

        text = findViewById(R.id.jobReader_text);
        scroll = findViewById(R.id.jobReader_scroll);

        final Toolbar mToolbar = findViewById(R.id.toolBar_jobReader);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        mToolbar.setOnClickListener(e -> scroll.smoothScrollTo(0, 0));
        mToolbar.setTitle(title);

        final StringFormatter stringFormatter = new StringFormatter();
        Spanned spanText = Html.fromHtml(content);
        spanText = stringFormatter.getFormattedString(spanText.toString());
        text.setText(spanText);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putInt("position", scroll.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        scroll.smoothScrollTo(0, savedInstanceState.getInt("position", 0));
    }
}
