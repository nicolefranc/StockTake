package com.infosys.stocktake.inventory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//import info.androidhive.viewpager2.databinding.ActivityFragmentViewPagerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.infosys.stocktake.MainActivity;
import com.infosys.stocktake.Profile;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.items.AddItemActivity;
import com.infosys.stocktake.inventory.items.ItemDetailsActivity;
import com.infosys.stocktake.loans.PersonalLoanHistoryActivity;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;
import com.infosys.stocktake.qr.QrScannerActivity;

import java.util.Map;
//import com.infosys.stocktake.inventory.databinding.ActivityFragmentViewPagerBinding;

public class InventoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager viewPager;
    private InventoryAdapter inventoryAdapter;
    private TabLayout tabLayout;
    private User currentUser;
    private String currentClub;
    private final String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final String TAG = "Inventory Activity: ";
    private boolean toClub;

    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_base);
        getCurrentUser();
        Intent intent = getIntent();
        toClub = intent.getBooleanExtra("toClub", false);

        setContentView(R.layout.inventory_base);

        drawerLayout = findViewById(R.id.drawer_layout);

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);


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


        viewPager = findViewById(R.id.view_pager);
        inventoryAdapter = new InventoryAdapter(getSupportFragmentManager(), currentUser, currentClub, toClub);
        viewPager.setAdapter(inventoryAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        if(toClub){
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tabLayout.selectTab(tab);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_profile: {
                Toast.makeText(this,"nav profile",Toast.LENGTH_LONG);
                startActivity(new Intent(this, Profile.class));

                break;
            }

            case R.id.nav_qr: {
                Toast.makeText(this,"nav qr",Toast.LENGTH_LONG);
                startActivity(new Intent(this, QrScannerActivity.class));

                break;
            }

            case R.id.nav_personal_loans: {
                Toast.makeText(this,"nav personal loan",Toast.LENGTH_LONG);
                startActivity(new Intent(this, PersonalLoanHistoryActivity.class));

                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(tabLayout.getSelectedTabPosition()==1){
            TabLayout.Tab home = tabLayout.getTabAt(0);
            tabLayout.selectTab(home);
        } else {
            InventoryActivity.this.finishAffinity();
            System.exit(0);
        }
    }
}