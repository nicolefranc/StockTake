package com.infosys.stocktake.loans;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Loan;

import java.util.ArrayList;

public class PersonalLoanHistoryActivity extends AppCompatActivity {
    public final static String TAG = "Personal Loan History Activity";
    FirebaseUser currentUser;
    ArrayList<Loan> personalLoans;
    RecyclerView loanRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_loans);

        //Instantiate current user and firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        StockTakeFirebase<Loan> stockTakeFirebaseLoan = new StockTakeFirebase<>(Loan.class, "loans");

        //Compound query the loans based on loanee id
        Task<ArrayList<Loan>> queryLoans = stockTakeFirebaseLoan.compoundQuery("loaneeID",currentUser.getUid());

        //On success: initialize the recycler view
        queryLoans
        .addOnSuccessListener(loans -> {
            personalLoans = loans;
            initRecyclerView();
        })
        .addOnFailureListener(e -> {
            Log.w(TAG,"Failure to fetch loan history");
        }
        );
    }

    private void initRecyclerView() {
        loanRecyclerView = findViewById(R.id.loanRecyclerView);

    }
}
