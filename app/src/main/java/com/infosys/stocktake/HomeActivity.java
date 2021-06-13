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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.InventoryAdapter;
import com.infosys.stocktake.inventory.clubs.ClubFragment;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;


//import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import devlight.io.library.ntb.NavigationTabBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class HomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private InventoryAdapter inventoryAdapter;
    private TabLayout tabLayout;
    private User currentUser;
    private String currentClub;
    private final String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final String TAG = "Inventory Activity: ";
    private boolean toClub;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        getCurrentUser();
        Intent intent = getIntent();
        toClub = intent.getBooleanExtra("toClub", false);
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
        Map<String, Membership> membershipMap = currentUser.getClubMembership();
        Log.d(TAG, "setUpViews: " + membershipMap.entrySet().iterator().next().getKey().toString());
        if(membershipMap.size() != 0) {
            Map.Entry<String, Membership> entry = membershipMap.entrySet().iterator().next();
            currentClub = entry.getKey();
        }
        else{
            currentClub = "";
        }


        viewPager = findViewById(R.id.vp_horizontal_ntb);
        inventoryAdapter = new InventoryAdapter(getSupportFragmentManager(), currentUser, currentClub, toClub);
        viewPager.setAdapter(inventoryAdapter);
    }

    private void initUI() {
        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_baseline_heart_24),
                        Color.parseColor(colors[0]))
                        .title("My Club")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_baseline_search_24),
                        Color.parseColor(colors[1]))
                        .title("Other Club")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_baseline_shopping_cart_24),
                        Color.parseColor(colors[2]))
                        .title("My Loans")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_baseline_account_circle_24),
                        Color.parseColor(colors[3]))
                        .title("Profile")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 2);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(true);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.parent);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final String title = String.valueOf(new Random().nextInt(15));
                            if (!model.isBadgeShowed()) {
                                model.setBadgeTitle(title);
                                model.showBadge();
                            } else model.updateBadgeTitle(title);
                        }
                    }, i * 100);
                }

//
            }
        });

        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#009F90AF"));
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#9f90af"));
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
