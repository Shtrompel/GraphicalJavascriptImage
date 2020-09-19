package com.igalblech.school.graphicaljavascriptcompiler.utils.front;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ProjectActivityPagerAdapter extends FragmentStatePagerAdapter {

    public static final int PAGE_SCRIPT = 0;
    public static final int PAGE_RENDER = 1;
    public static final int PAGE_ERROR = 2;
    public static final int PAGES_COUNT = 3;

    Fragment[] pages;
    public ProjectActivityPagerAdapter ( FragmentManager fm ) {
        super ( fm );
        this.pages = new Fragment[PAGES_COUNT];
    }

    public void setFragment(int i, Fragment fragment) {
        this.pages[i] = fragment;
    }

    @Override
    public Fragment getItem ( int position ) {
        if (0 <= position && position < PAGES_COUNT)
            return pages[position];
        return null;
    }

    @Override
    public int getCount ( ) {
        return PAGES_COUNT;
    }
}
