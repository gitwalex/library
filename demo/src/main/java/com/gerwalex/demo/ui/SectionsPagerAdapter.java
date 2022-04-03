package com.gerwalex.demo.ui;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gerwalex.demo.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

    private static final int ITEM_COUNT = 4;
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};

    public SectionsPagerAdapter(FragmentActivity f) {
        super(f);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment f;
        int reverse = getItemCount() -1 - position;
        switch (reverse) {
            case 0:
                f = PlaceholderFragment.newInstance(position + 1);
                break;
            case 1:
                f = PlaceholderFragment.newInstance(position + 1);
                break;
            case 2:
                f = new FragmentImageDecoView();
                break;
            case 3:
                f = new FragmentFirework();
                break;

            default:
                throw new IllegalStateException("Fragment für Position nicht bekannt");
        }
        return f;
    }

    @Override
    public int getItemCount() {
        // Show 2 total pages.
        return ITEM_COUNT;
    }
}