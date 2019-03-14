package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.ystu.myystu.Fragments.NewsFragment;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.NetworkInformation;

import android.os.Bundle;

public class ScheduleChangeActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_change);

        final ConstraintLayout mainLayout = findViewById(R.id.main_layout_schedule_change);
        final Toolbar mToolbar = findViewById(R.id.toolBar_schedule_change);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        mFragmentManager = getSupportFragmentManager();
        NewsFragment newsFragment = new NewsFragment();


        // TODO Отображение изменений расписания
        if(NetworkInformation.hasConnection(getApplicationContext())){
            // Изменения
            mFragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(mainLayout.getId(), newsFragment, "NEWS_FRAGMENT")
                    .commit();
        } else {
            // Фрагмент ошибки
            // TODO вызов ошибки
            ErrorMessage.show(mainLayout, 0, null, this, newsFragment.getId());
        }
    }
}
