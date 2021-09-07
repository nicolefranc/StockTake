package com.infosys.stocktake.inventory;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.infosys.stocktake.inventory.clubs.ClubFragment;
import com.infosys.stocktake.inventory.itemloanhistory.ItemLoanHistoryFragment;
import com.infosys.stocktake.inventory.items.InventoryFragment;
import com.infosys.stocktake.inventory.profile.ProfileFragment;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;

import java.util.Map;

public class InventoryAdapter extends FragmentPagerAdapter{

    private User currentUser;
    private Map<String, Membership> currentClub;
    private boolean hasClub;
    private boolean toClub;
    private static final String TAG = InventoryAdapter.class.getSimpleName();

    public InventoryAdapter(@NonNull FragmentManager fm, User user, Map<String, Membership> club, Boolean toClub) {
        super(fm);
        currentUser = user;
        currentClub = club;
        this.toClub = toClub;
        if (currentClub.entrySet().iterator().next().getKey().toString() == ""){
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
            Log.d(TAG, "Inventory Adapter:"+ "My Club " + position);
            InventoryFragment inventoryFragment = new InventoryFragment();
            position = position+1;
            Bundle bundle = new Bundle();
            bundle.putString("message", "Fragement: " + position);
            bundle.putSerializable("user", currentUser);
//            bundle.putString("club", currentClub.entrySet().iterator().next().getKey().toString());
            bundle.putString("club", currentClub.entrySet().iterator().next().getKey().toString());
            inventoryFragment.setArguments(bundle);
            return inventoryFragment;
        }
        else if (position == 1){
            Log.d(TAG, "Inventory Adapter:"+ "Other Clubs " + position);
            ClubFragment clubFragment = new ClubFragment();
            position = position+1;
            Bundle bundle = new Bundle();
            bundle.putString("message", "Fragement: " + position);
            bundle.putSerializable("user", currentUser);
            bundle.putString("club", currentClub.entrySet().iterator().next().getKey().toString());
            clubFragment.setArguments(bundle);
            return clubFragment;
        }
        else if (position == 2){
            Log.d(TAG, "Inventory Adapter:"+ "Loan History " + position);
            ItemLoanHistoryFragment itemLoanHistoryFragment = new ItemLoanHistoryFragment();
            position = position+1;
            Bundle bundle = new Bundle();
            // TODO: 7/9/2021 Add the args u need here
            itemLoanHistoryFragment.setArguments(bundle);
            return itemLoanHistoryFragment;
        }
        else{
            Log.d(TAG, "Inventory Adapter:"+ "Profile" + position);
            ProfileFragment profileFragment = new ProfileFragment();
            position = position+1;
            Bundle bundle = new Bundle();
            // TODO: 7/9/2021 Add the args u need here
            profileFragment.setArguments(bundle);
            return profileFragment;
        }
    }

    @Override
    public int getCount() {
        if(hasClub) {
            return 4;
        }
        else{
            return 3;
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