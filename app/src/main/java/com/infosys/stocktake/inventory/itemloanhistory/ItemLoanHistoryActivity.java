package com.infosys.stocktake.inventory.itemloanhistory;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.Loan;
import com.infosys.stocktake.models.User;

import java.util.ArrayList;
import java.util.Date;

public class ItemLoanHistoryActivity extends AppCompatActivity {
    private Item item;
    private ArrayList<Loan> mLoans= new ArrayList<>();
    StockTakeFirebase<Loan> itemStockTakeFirebase;
    private StockTakeFirebase<Club> clubFirebase = new StockTakeFirebase<Club>(Club.class, "clubs");
    private StockTakeFirebase<User> userFirebase = new StockTakeFirebase<User>(User.class, "users");
    private static final String TAG = "ViewLoanHistory: ";
    private ArrayList<String> clubNames = new ArrayList<String>();
    private ArrayList<String> userNames = new ArrayList<String>();
    private ArrayList<Date> dueDates = new ArrayList<Date>();
    private ArrayList<Date> returnDates = new ArrayList<Date>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_list);
        // Populate components with Item data from passed Intent
        item = (Item) getIntent().getSerializableExtra("itemIntent");
        itemStockTakeFirebase = new StockTakeFirebase<Loan>(Loan.class, "loans");
        populateItems();
    }

    private void populateItems(){
        Log.d(TAG,"Populating items...");
        Task<ArrayList<Loan>> populateTask = itemStockTakeFirebase.compoundQuery("itemID", item.getItemID());
        populateTask.addOnSuccessListener(new OnSuccessListener<ArrayList<Loan>>() {
            @Override
            public void onSuccess(ArrayList<Loan> loans) {
                if(loans != null) {
                    mLoans = loans;
                }
                else{
                    mLoans = new ArrayList<Loan>();
                }
                Log.d(TAG,"populateItems: Accessed firebase! populating items now... " + mLoans.size());
                getClubNames();
            }
        });
        populateTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed to retrieve items :(, exception is: ", e);
            }
        });

    }

    private void getClubNames(){
        for(int i = 0; i<mLoans.size();i++){
            Loan loan = mLoans.get(i);
            Task<Club> clubTask = clubFirebase.query(loan.getClubID());
            dueDates.add(loan.getDueDate());
            returnDates.add(loan.getReturnDate());

            if (i!=mLoans.size()-1) {
                clubTask.addOnSuccessListener(new OnSuccessListener<Club>() {
                    @Override
                    public void onSuccess(Club club) {
                        clubNames.add(club.getClubName());
                        Log.d(TAG, "getClubNames: got club "+ club.getClubName());
                    }
                });
            }
            else {
                clubTask.addOnSuccessListener(new OnSuccessListener<Club>() {
                    @Override
                    public void onSuccess(Club club) {
                        clubNames.add(club.getClubName());
                        getUserNames();
                    }
                });
            }
        }
    }

    private void getUserNames(){
        for(int i = 0; i<mLoans.size();i++) {
            Loan loan = mLoans.get(i);
            Task<User> userTask = userFirebase.query(loan.getLoaneeID());
            if (i!=mLoans.size()-1) {
                userTask.addOnSuccessListener(new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user != null) {
                            Log.d(TAG, "getUserNames: got user "+ user.getFullName());
                            userNames.add(user.getFullName());
                        }
                        else{
                            userNames.add("Deleted User");
                        }
                    }
                });
            }
            else{
                Log.d(TAG, "getUserNames: else loop entered");
                userTask.addOnSuccessListener(new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user != null) {
                            userNames.add(user.getFullName());
                            Log.d(TAG, "onSuccess: number of user entries: " + userNames.size());
                            initRecyclerView();
                        }
                        else{
                            Log.d(TAG, "onSuccess: user is null");
                            userNames.add("Deleted User");
                            initRecyclerView();
                        }
                    }
                });
                userTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: unable to get some users");
                        initRecyclerView();
                    }
                });
            }
        }
    }

    private void initRecyclerView(){
        Log.d(TAG,"Initializing recycler view...");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LoanRecyclerViewAdapter recyclerAdapter = new LoanRecyclerViewAdapter(mLoans, clubNames, userNames, dueDates, returnDates, this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}