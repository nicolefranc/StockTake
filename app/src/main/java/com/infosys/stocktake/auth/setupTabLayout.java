package com.infosys.stocktake.auth;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.infosys.stocktake.R;

public class setupTabLayout extends AppCompatActivity {
    private void TabLayout() {
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
        });
    }

    private void onTabTapped(int position) {
        switch (position) {
            case 0:
                // Do something when first tab is tapped here
                break;
            default:
                Toast.makeText(this, "Tapped " + position, Toast.LENGTH_SHORT);
        }
    }

}
