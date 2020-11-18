package com.infosys.stocktake.inventory;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.infosys.stocktake.R;

import java.util.ArrayList;

public class InventoryAdapter extends FragmentPagerAdapter{

    public InventoryAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        InventoryFragment inventoryFragment = new InventoryFragment();
        position = position+1;
        Bundle bundle = new Bundle();
        bundle.putString("message", "Fragement: " + position);
        inventoryFragment.setArguments(bundle);
        return inventoryFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        position = position+1;
        return "Fragment " + position;
    }
}