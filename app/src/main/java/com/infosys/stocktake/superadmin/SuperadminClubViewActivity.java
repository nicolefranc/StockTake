package com.infosys.stocktake.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.clubs.ClubRecyclerViewAdapter;
import com.infosys.stocktake.inventory.items.ItemRecyclerViewAdapter;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.User;

import java.util.ArrayList;

public class SuperadminClubViewActivity  extends AppCompatActivity {
    StockTakeFirebase<Club> clubStocktakeFirebase;
    final private String TAG = "SuperAdmin Club View";
    ArrayList<Club> mClubs;
    FloatingActionButton createClubBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_clubview);
        //Fetch everything from XML
        createClubBtn = findViewById(R.id.createClubBtn);
        clubStocktakeFirebase = new StockTakeFirebase<>(Club.class,"clubs");
        StockTakeFirebase<User> userStockTakeFirebase = new StockTakeFirebase<>(User.class,"users");
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();

        String userID = fbAuth.getCurrentUser().getUid();

        userStockTakeFirebase.query(userID).addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                if(user.isSuperAdmin()){
                    populateClubView();
                }
            }
        });

        createClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createClubIntent = new Intent(SuperadminClubViewActivity.this,AddClubDetailActivity.class);
                startActivity(createClubIntent);
            }
        });


        //TODO 4: implement a search bar

    }

    public void populateClubView(){
        Log.d(TAG,"Getting club collection");
        clubStocktakeFirebase.getCollection().addOnSuccessListener(new OnSuccessListener<ArrayList<Club>>() {
            @Override
            public void onSuccess(ArrayList<Club> clubs) {
                Log.d(TAG,"Populating clubs");
                mClubs = new ArrayList<>();
                mClubs.addAll(clubs);
                initRecyclerView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed to retrieve items :(, exception is: ", e);
            }
        });
    }
    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.clubRV);
        ClubRecyclerViewAdapter recyclerAdapter = new ClubRecyclerViewAdapter(mClubs, this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
    }
}
