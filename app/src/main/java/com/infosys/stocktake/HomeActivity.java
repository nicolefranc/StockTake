package com.infosys.stocktake;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.InventoryAdapter;
import com.infosys.stocktake.inventory.clubs.ClubFragment;
import com.infosys.stocktake.inventory.items.AddItemActivity;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;
import com.infosys.stocktake.qr.QrScannerActivity;


//import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import devlight.io.library.ntb.NavigationTabBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class HomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private InventoryAdapter inventoryAdapter;
    private BottomNavigationView bottomNavigationView;
    private MenuItem prevMenuItem;
    private int viewPosition;
    private CharSequence pageTitle;
    private TabLayout tabLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private User currentUser;
    private String currentClub;
    private Button qrScan;
    private Map<String, String> currentClubName;
    private final String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final String TAG = "InventoryActivity";
    private boolean toClub;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        getCurrentUser();
        getClubNames();
        Intent intent = getIntent();
        toClub = intent.getBooleanExtra("toClub", false);
        Log.d(TAG, "toClub: " + toClub);
        setContentView(R.layout.activity_home_screen);
        initUI();
    }

    private void getCurrentUser(){
        StockTakeFirebase<User> stockTakeFirebase = new StockTakeFirebase<>(User.class,"users");
        Task<User> userTask = stockTakeFirebase.query(userUID);
        userTask.addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                Log.d(TAG, "User is: " + user.getTelegramHandle());
//                setUpViews();
            }
        });

        userTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to get user");
            }
        });

    }

    private void getClubNames(){
        Log.d(TAG,"Populating items...");
        StockTakeFirebase<Club> clubStockTakeFirebase = new StockTakeFirebase<Club>(Club.class,"clubs");
        Task<ArrayList<Club>> populateTask = clubStockTakeFirebase.getCollection();

        currentClubName = new HashMap<String, String>();

        populateTask.addOnSuccessListener(new OnSuccessListener<ArrayList<Club>>() {
            @Override
            public void onSuccess(ArrayList<com.infosys.stocktake.models.Club> clubs) {
                Log.d(TAG,"Accessed firebase! populating clubs now...");

                for(Club club:clubs) {

//                    if (!club.getClubName().matches("Not a Club Exco")) { //change to top line after testing is done
                    currentClubName.put(club.getClubID(), club.getClubName());
                    Log.d(TAG, "club name populated: " + club.getClubID() + club.getClubName());

                }
                setUpViews();
            }
        });
        populateTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed to retrieve items :(, exception is: ", e);
            }
        });

    }

    private void setUpViews(){
        Map<String, Membership> membershipMap = currentUser.getClubMembership();
        Log.d(TAG, "MembershipMap: " + membershipMap.toString());
        Log.d(TAG, "setUpViews: " + membershipMap.entrySet().iterator().next().getKey().toString());
        if(membershipMap.size() != 0) {
            Map.Entry<String, Membership> entry = membershipMap.entrySet().iterator().next();
            currentClub = entry.getKey();
        }
        else{
            currentClub = "";
        }


        viewPager = findViewById(R.id.vp_horizontal_ntb);
        inventoryAdapter = new InventoryAdapter(getSupportFragmentManager(), currentUser, membershipMap, toClub, currentClubName);
        viewPager.setAdapter(inventoryAdapter);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);

                viewPosition = position;
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initUI() {
        final String[] colors = getResources().getStringArray(R.array.default_preview);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        pageTitle = item.getTitle();
                        switch (item.getItemId()) {
                            case R.id.nav_my_clubs:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.nav_other_clubs:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.nav_my_loans:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.nav_profile:
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });

        qrScan = findViewById(R.id.qrScan);
        qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), QrScannerActivity.class);
                startActivity(intent);
            }
        });


    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.my_club_item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.txt.setText(String.format("Navigation Item #%d", position));
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txt;

            public ViewHolder(final View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.txt_vp_item_list);
            }
        }
    }
}
