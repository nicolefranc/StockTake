package com.infosys.stocktake.inventory;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.infosys.stocktake.inventory.clubs.ClubFragment;
import com.infosys.stocktake.inventory.items.InventoryFragment;
import com.infosys.stocktake.models.User;

import java.io.Serializable;

public class InventoryAdapter extends FragmentPagerAdapter{

    private User currentUser;
    private String currentClub;
    private boolean hasClub;

    public InventoryAdapter(@NonNull FragmentManager fm, User user, String club) {
        super(fm);
        currentUser = user;
        currentClub = club;
        if (currentClub == ""){
            hasClub = false;
        }
        else{
            hasClub = true;
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position ==0 && hasClub){
            InventoryFragment inventoryFragment = new InventoryFragment();
            position = position+1;
            Bundle bundle = new Bundle();
            bundle.putString("message", "Fragement: " + position);
            bundle.putSerializable("user", currentUser);
            bundle.putString("club", currentClub);
            inventoryFragment.setArguments(bundle);
            return inventoryFragment;
        }
        else{
            ClubFragment clubFragment = new ClubFragment();
            position = position+1;
            Bundle bundle = new Bundle();
            bundle.putString("message", "Fragement: " + position);
            bundle.putSerializable("user", currentUser);
            bundle.putString("club", currentClub);
            clubFragment.setArguments(bundle);
            return clubFragment;
        }
    }

    @Override
    public int getCount() {
        if(hasClub) {
            return 2;
        }
        else{
            return 1;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position ==0 && hasClub){
            return "My Club";
        }
        else if(position == 0 && !hasClub){
            return "Clubs";
        }
        else{
            return "Other Clubs";
        }
    }
}