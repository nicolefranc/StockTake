package com.infosys.stocktake.inventory.itemloanhistory;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.items.ItemRecyclerViewAdapter;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.Loan;
import com.infosys.stocktake.models.QrCode;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemLoanHistoryActivity extends AppCompatActivity {
    private Item item;
    private ArrayList<Loan> mLoans= new ArrayList<>();
    StockTakeFirebase<Loan> itemStockTakeFirebase;
    private static final String TAG = "ViewLoanHistory: ";

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
                initRecyclerView();
            }
        });
        populateTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed to retrieve items :(, exception is: ", e);
            }
        });

    }

    private void initRecyclerView(){
        Log.d(TAG,"Initializing recycler view...");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LoanRecyclerViewAdapter recyclerAdapter = new ItemRecyclerViewAdapter(mLoans,this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}