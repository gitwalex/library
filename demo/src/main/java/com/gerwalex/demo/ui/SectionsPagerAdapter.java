package com.gerwalex.demo.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

    private static final int ITEM_COUNT = 4;

    private static final String[] TAB_TITLES = new String[]{"ImageDecoView", "Firework", "Konfetti", "Permission-Demo"};

    public SectionsPagerAdapter(FragmentActivity f) {
        super(f);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment f;
        int reverse = getItemCount() - 1 - position;
        switch (reverse) {
            case 0:
                f = new FragmentImageDecoView();
                break;
            case 1:
                f = new FragmentFirework();
                break;
            case 2:
                f = new FragmentKonfetti();
                break;
            case 3:
                f = new FragmentPermission();
                break;
            default:
                throw new IllegalStateException("Fragment für Position nicht bekannt");
        }
        return f;
    }

    public String getFragmentTitle(int position) {
        return TAB_TITLES[ITEM_COUNT - 1 - position];
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }
}