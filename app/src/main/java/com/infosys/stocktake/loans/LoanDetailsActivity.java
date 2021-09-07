package com.infosys.stocktake.loans;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.infosys.stocktake.HomeActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
//import com.infosys.stocktake.inventory.InventoryActivity;
import com.infosys.stocktake.inventory.itemloanhistory.LoanRecyclerViewAdapter;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.Loan;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class LoanDetailsActivity extends AppCompatActivity {
    public static final String TAG = "LoanDetailsActivity";
    public static final String PREVIOUS_ACTIVITY_KEY = "prev_act_key";
    final StockTakeFirebase<Loan> stockTakeFirebaseLoan = new StockTakeFirebase<>(Loan.class,"loans");
    final StockTakeFirebase<Item> stockTakeFirebaseItem = new StockTakeFirebase<>(Item.class, "items");
    TextView loanIDText,loanItemNameText,quantityText;
    ImageView loanDetailsImage;
    Button returnButton, homeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);
        //Link the components
        loanIDText = findViewById(R.id.loanIDText);
        loanItemNameText = findViewById(R.id.loanItemNameText);
        quantityText = findViewById(R.id.loanDetailsQuantityText);
        homeButton = findViewById(R.id.homeButton);
        returnButton = findViewById(R.id.returnButton);
        loanDetailsImage = findViewById(R.id.loanDetailsImage);


        //Get the intent from the previous state
        Intent loanDetailsIntent = getIntent();
        String previousActivity = loanDetailsIntent.getStringExtra(PREVIOUS_ACTIVITY_KEY);
        Loan currentLoan = (Loan) loanDetailsIntent.getSerializableExtra(AddLoanActivity.LOAN_INTENT_KEY);
        String loanID = currentLoan.getLoanID();


        Log.d(TAG, "Is it returned? " + currentLoan.getReturned());
        if((!previousActivity.equals(LoanRecyclerViewAdapter.ACTIVITY_NAME) || (currentLoan.getReturned()))){


            returnButton.setVisibility(View.INVISIBLE);
        }
        loanIDText.setText(loanID);

        quantityText.setText(String.valueOf(currentLoan.getQuantity()));
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    beginHealthCheck(loanID);
//                returnLoan(loanID);
            }
        });
        String itemID = currentLoan.getItemID();
        stockTakeFirebaseItem.query(itemID).addOnSuccessListener(new OnSuccessListener<Item>() {
            @Override
            public void onSuccess(Item item) {
                loanItemNameText.setText(item.getItemName());
                Log.d(TAG, "Item " + item.getItemID() + " has been successfully fetched");
                Uri imageUri = Uri.parse(item.getItemPicture());
                Picasso.get().load(imageUri)
                        .fit().centerCrop().into(loanDetailsImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Item fails to be fetched");
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(LoanDetailsActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });
    }

    private void beginHealthCheck(String loanID) {
        Intent healthCheckIntent = new Intent(LoanDetailsActivity.this , ReturnHealthCheck.class);
        healthCheckIntent.putExtra("loan-id" , loanID);
        startActivity(healthCheckIntent);
    }

    private void returnLoan(String loanID){
        stockTakeFirebaseLoan.query(loanID).addOnSuccessListener(new OnSuccessListener<Loan>() {
            @Override
            public void onSuccess(Loan loan) {
                loan.setReturnDate(new Date());
                stockTakeFirebaseLoan.update(loan,loan.getLoanID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Successfully updated the loan");
                        stockTakeFirebaseItem.query(loan.getItemID()).addOnSuccessListener(new OnSuccessListener<Item>() {
                            @Override
                            public void onSuccess(Item item) {
                                Log.d(TAG,"Successfully fetched the item details");
                                int qtyAvailable = item.getQtyStatus().get(ItemStatus.AVAILABLE.toString());
                                qtyAvailable+=loan.getQuantity();
                                Log.d(TAG,"Changing qty to "+qtyAvailable);
                                item.setQtyStatus(ItemStatus.AVAILABLE.toString(),qtyAvailable);
                                stockTakeFirebaseItem.update(item,item.getItemID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"Successfully added the quantity back to the club");
                                        Toast.makeText(LoanDetailsActivity.this, "Item successfully returned", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoanDetailsActivity.this,HomeActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}
