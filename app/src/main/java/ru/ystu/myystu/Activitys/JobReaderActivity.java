package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.PaddingHelper;
import ru.ystu.myystu.Utils.SettingsController;
import ru.ystu.myystu.Utils.StringFormatter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class JobReaderActivity extends AppCompatActivity {

    private String content;
    private String title;

    private AppCompatTextView text;
    private NestedScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_reader);

        LightStatusBar.setLight(true, true, this, true);

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

        PaddingHelper.setPaddingStatusBarAndToolBar(this, scroll, true);

        final StringFormatter stringFormatter = new StringFormatter();
        Spanned spanText = Html.fromHtml(content);
        spanText = stringFormatter.getFormattedString(spanText.toString());
        text.setText(spanText);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()) {
            if (SettingsController.isEnabledAnim(this)) {
                overridePendingTransition(R.anim.activity_slide_right_show_reverse, R.anim.activity_slide_left_out_reverse);
            } else {
                overridePendingTransition(0, 0);
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_job_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_job_reader_copyText:

                final ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                final ClipData mClipData = ClipData.newPlainText("job_text", text.getText().toString());
                mClipboardManager.setPrimaryClip(mClipData);

                Toast.makeText(this, getResources().getString(R.string.toast_isCopyText), Toast.LENGTH_SHORT).show();

                return true;

            case R.id.menu_job_reader_share:
                CharSequence title = "";
                if (getSupportActionBar() != null) {
                    title = getSupportActionBar().getTitle();
                }
                IntentHelper.shareText(this, title + "\n\n" + text.getText().toString());
                return true;
        }

        return false;
    }
}
