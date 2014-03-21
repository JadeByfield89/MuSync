
package com.jbsoft.musync.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments;

    private FragmentManager fm;

    private static final String[] CONTENT = new String[] {
            "Now Playing", "Genres", "Albums", "Artists", "Songs"
    };

    public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);

        this.fragments = fragments;

        this.fm = fm;

    }

    @Override
    public Fragment getItem(int position) {

        return this.fragments.get(position);

    }

    @Override
    public int getCount() {

        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length];
    }

    // @Override
    /*
     * public void destroyItem(ViewGroup container, int position, Object object)
     * { super.destroyItem(container, position, object); //
     * fm.executePendingTransactions();
     * //fm.saveFragmentInstanceState(fragments.get(position)); }
     */

}
