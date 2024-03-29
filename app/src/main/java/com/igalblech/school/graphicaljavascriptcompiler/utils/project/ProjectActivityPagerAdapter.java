package com.igalblech.school.graphicaljavascriptcompiler.utils.project;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Adapter for projecy activity.
 * @see com.igalblech.school.graphicaljavascriptcompiler.ActivityProject
 */
public class ProjectActivityPagerAdapter extends FragmentStatePagerAdapter {

    public static final int PAGE_SCRIPT = 0;
    public static final int PAGE_RENDER = 1;
    public static final int PAGE_ERROR = 2;
    public static final int PAGES_COUNT = 3;

    final Fragment[] pages;
    public ProjectActivityPagerAdapter ( FragmentManager fm ) {
        super ( fm );
        this.pages = new Fragment[PAGES_COUNT];
    }

    public void setFragment(int i, Fragment fragment) {
        this.pages[i] = fragment;
    }

    @Override
    @NonNull
    public Fragment getItem ( int position ) {
        if (0 <= position && position < PAGES_COUNT)
            return pages[position];
        return pages[0];
    }

    @Override
    public int getCount ( ) {
        return PAGES_COUNT;
    }
}
