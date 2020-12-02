package com.infosys.stocktake.inventory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//import info.androidhive.viewpager2.databinding.ActivityFragmentViewPagerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.infosys.stocktake.MainActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.items.AddItemActivity;
import com.infosys.stocktake.inventory.items.ItemDetailsActivity;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;

import java.util.Map;
//import com.infosys.stocktake.inventory.databinding.ActivityFragmentViewPagerBinding;

public class InventoryActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private InventoryAdapter inventoryAdapter;
    private TabLayout tabLayout;
    private User currentUser;
    private String currentClub;
    private final String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final String TAG = "Inventory Activity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_base);
        getCurrentUser();
    }

    private void getCurrentUser(){
        StockTakeFirebase<User> stockTakeFirebase = new StockTakeFirebase<>(User.class,"users");
        Task<User> userTask = stockTakeFirebase.query(userUID);
        userTask.addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                Log.d(TAG, "User is: " + user.getTelegramHandle());
                setUpViews();
            }
        });
        userTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to get user");
            }
        });

    }

    private void setUpViews(){
        Map<String,Membership> membershipMap = currentUser.getClubMembership();
        Log.d(TAG, "setUpViews: " + membershipMap.toString());
        if(membershipMap.size() != 0) {
            Map.Entry<String, Membership> entry = membershipMap.entrySet().iterator().next();
            currentClub = entry.getKey();
        }
        else{
            currentClub = "";
        }

        setContentView(R.layout.inventory_base);
        viewPager = findViewById(R.id.view_pager);
        inventoryAdapter = new InventoryAdapter(getSupportFragmentManager(), currentUser, currentClub);
        viewPager.setAdapter(inventoryAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent itemIntent = new Intent(InventoryActivity.this, MainActivity.class);
        startActivity(itemIntent);
    }
}
