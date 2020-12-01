package com.infosys.stocktake.loans;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.infosys.stocktake.MainActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.Loan;
import com.squareup.picasso.Picasso;

public class LoanDetailsActivity extends AppCompatActivity {
    public static final String TAG = "Loan Details Activity";
    TextView loanIDText,loanItemNameText,quantityText;
    ImageView loanDetailsImage;
    Button homeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);
        //Link the components
        loanIDText = findViewById(R.id.loanIDText);
        loanItemNameText = findViewById(R.id.loanItemNameText);
        quantityText = findViewById(R.id.loanDetailsQuantityText);
        homeButton = findViewById(R.id.homeButton);
        loanDetailsImage = findViewById(R.id.loanDetailsImage);

        final StockTakeFirebase<Loan> stockTakeFirebaseLoan = new StockTakeFirebase<>(Loan.class,"loans");
        final StockTakeFirebase<Item> stockTakeFirebaseItem = new StockTakeFirebase<>(Item.class, "items");

        //Get the intent from the previous state
        Intent loanDetailsIntent = getIntent();
        String loanID = loanDetailsIntent.getStringExtra(AddLoanActivity.LOAN_INTENT_KEY);
        loanIDText.setText(loanID);

        //Set the loan data, including the image
        stockTakeFirebaseLoan.query(loanID).addOnSuccessListener(new OnSuccessListener<Loan>() {
            @Override
            public void onSuccess(Loan loan) {
                quantityText.setText(String.valueOf(loan.getQuantity()));

                String itemID = loan.getItemID();
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
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG,"ERROR IN FETCHING ITEM");
                    e.printStackTrace();
                }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(LoanDetailsActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });
    }
}
