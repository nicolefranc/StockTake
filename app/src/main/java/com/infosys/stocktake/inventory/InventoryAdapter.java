package com.infosys.stocktake.inventory;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.infosys.stocktake.R;

import java.util.ArrayList;

public class InventoryAdapter extends FragmentPagerAdapter{

    public InventoryAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position ==0){
            InventoryFragment inventoryFragment = new InventoryFragment();
            position = position+1;
            Bundle bundle = new Bundle();
            bundle.putString("message", "Fragement: " + position);
            inventoryFragment.setArguments(bundle);
            return inventoryFragment;
        }
        else{
            ClubFragment clubFragment = new ClubFragment();
            position = position+1;
            Bundle bundle = new Bundle();
            bundle.putString("message", "Fragement: " + position);
            clubFragment.setArguments(bundle);
            return clubFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position ==0){
            return "My Club";
        }
        else{
            return "Other Clubs";
        }
    }
}