package ru.ystu.myystu.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import ru.ystu.myystu.Fragments.ScheduleTabOneFragment;
import ru.ystu.myystu.Fragments.ScheduleTabTwoFragment;
import ru.ystu.myystu.R;

public class SchedulePagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SchedulePagerAdapter(@NonNull FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new ScheduleTabTwoFragment();
        }
        return new ScheduleTabOneFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.tab_schedule_one);
            case 1:
                return mContext.getString(R.string.tab_schedule_two);
            default:
                return null;
        }
    }
}
