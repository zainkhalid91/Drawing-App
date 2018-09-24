package smartboard.fyp.com.smartapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import Fragments.ProfileFragment;
import Fragments.SchedulesFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment();
            default:
            case 1:
                return new SchedulesFragment();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
