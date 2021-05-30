package com.infosys.stocktake;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.infosys.stocktake.inventory.clubs.ClubFragment;

public class Pager extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public Pager(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return new ClubFragment();
//                case 1: // Fragment # 0 - This will show FirstFragment different title
//                    return FirstFragment.newInstance(1, "Page # 2");
//                case 2: // Fragment # 1 - This will show SecondFragment
//                    return SecondFragment.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

}
