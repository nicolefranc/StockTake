package com.infosys.stocktake.loans;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.Loan;

import java.util.ArrayList;
import java.util.Date;


public class PersonalLoanHistoryActivity extends Fragment {
    public final static String TAG = "Personal Loan History Activity";
    FirebaseUser currentUser;
    private ArrayList<Loan> personalLoans;
    private ArrayList<String> itemNames;
    private ArrayList<String> itemImages;
    private ArrayList<Integer> loanQuantities;
    private ArrayList<Date> loanDates;
    StockTakeFirebase<Loan> stockTakeFirebaseLoan = new StockTakeFirebase<>(Loan.class, "loans");
    StockTakeFirebase<Item> stockTakeFirebaseItem = new StockTakeFirebase<>(Item.class, "items");
    LoanAdapter loanAdapter;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_personal_loans,container,false);

        //Instantiate current user and firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        getLoans(view);
        return view;
    };

    private void getLoans(View view){
        personalLoans = new ArrayList<>();
        //Compound query the loans based on loanee id
        Task<ArrayList<Loan>> queryLoans = stockTakeFirebaseLoan.compoundQuery("loaneeID",currentUser.getUid()) ;
        //On success: initialize the recycler view
        queryLoans
        .addOnSuccessListener(loans -> {
            if(loans!=null){
                personalLoans = loans;
            }
            getItemDetails(view);
        })
        .addOnFailureListener(e -> {
            Log.w(TAG,"Failure to fetch loan history");
        }
        );
    }
    private void getItemDetails(View view){
        itemNames = new ArrayList<>();
        itemImages = new ArrayList<>();
        int i;
        for(i =0;i<personalLoans.size()-1;i++){
            if (!personalLoans.get(i).getReturned()) {
                Log.d("PERSONAL LOAN HISTORY",  personalLoans.get(i).getReturned().toString());;


                stockTakeFirebaseItem.query(personalLoans.get(i).getItemID()).addOnSuccessListener(new OnSuccessListener<Item>() {
                    @Override
                    public void onSuccess(Item item) {
                        Log.d(TAG, "Fetching item names");
                        itemNames.add(item.getItemName());
                        itemImages.add(item.getItemPicture());
                    }
                });
            }
        }
        if(personalLoans.size()!=0){
            if (!personalLoans.get(i).getReturned()) {

                stockTakeFirebaseItem.query(personalLoans.get(i).getItemID()).addOnSuccessListener(new OnSuccessListener<Item>() {
                    @Override
                    public void onSuccess(Item item) {
                        Log.d(TAG, "Fetching the last item name");
                        itemNames.add(item.getItemName());
                        itemImages.add(item.getItemPicture());
                        getLoanQuantities(view);
                    }
                });
            }
        }
        else{
            getLoanQuantities(view);
        }
    }

    private void getLoanQuantities(View view){
        loanQuantities = new ArrayList<>();
        Log.d(TAG,"Adding loan quantities");
        for(int i=0;i< personalLoans.size();i++){
            loanQuantities.add(personalLoans.get(i).getQuantity());
        }
        getLoanDates(view);
    }

    private void getLoanDates(View view){
        loanDates = new ArrayList<>();
        Log.d(TAG,"Getting loan due dates");
        for(int i=0;i< personalLoans.size();i++){
            loanDates.add(personalLoans.get(i).getLoanDate());
        }
        initRecyclerView(view);
    }

    private void initRecyclerView(View view) {
        Log.d(TAG,"Initializing Loan Recycler View");
        RecyclerView loanRecyclerView = view.findViewById(R.id.loanRecyclerView);
        Log.d(TAG,"Recycler View: "+R.id.loanRecyclerView);
        loanAdapter = new LoanAdapter(this.getActivity(), personalLoans,itemNames,itemImages,loanQuantities,loanDates);
        loanRecyclerView.setAdapter(loanAdapter);
        Log.d(TAG,"Loan Adapter SET");
        loanRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
    }
}
