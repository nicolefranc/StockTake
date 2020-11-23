package com.infosys.stocktake.inventory;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

//import info.androidhive.viewpager2.databinding.ActivityFragmentViewPagerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infosys.stocktake.R;
//import com.infosys.stocktake.inventory.databinding.ActivityFragmentViewPagerBinding;

public class InventoryActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private InventoryAdapter inventoryAdapter;
    private TabLayout tabLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inventory_base);
        viewPager = findViewById(R.id.view_pager);
        inventoryAdapter = new InventoryAdapter(getSupportFragmentManager());
        viewPager.setAdapter(inventoryAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

}