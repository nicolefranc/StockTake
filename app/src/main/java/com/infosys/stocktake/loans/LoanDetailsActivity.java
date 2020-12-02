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
import com.infosys.stocktake.inventory.itemloanhistory.LoanRecyclerViewAdapter;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.Loan;
import com.squareup.picasso.Picasso;

public class LoanDetailsActivity extends AppCompatActivity {
    public static final String TAG = "Loan Details Activity";
    public static final String PREVIOUS_ACTIVITY_KEY = "prev_act_key";
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

        final StockTakeFirebase<Loan> stockTakeFirebaseLoan = new StockTakeFirebase<>(Loan.class,"loans");
        final StockTakeFirebase<Item> stockTakeFirebaseItem = new StockTakeFirebase<>(Item.class, "items");

        //Get the intent from the previous state
        Intent loanDetailsIntent = getIntent();
        String previousActivity = loanDetailsIntent.getStringExtra(PREVIOUS_ACTIVITY_KEY);
        Loan currentLoan = (Loan) loanDetailsIntent.getSerializableExtra(AddLoanActivity.LOAN_INTENT_KEY);
        String loanID = currentLoan.getLoanID();

        if(!previousActivity.equals(LoanRecyclerViewAdapter.ACTIVITY_NAME)){
            returnButton.setVisibility(View.INVISIBLE);
        }
        loanIDText.setText(loanID);

        quantityText.setText(String.valueOf(currentLoan.getQuantity()));

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
                Intent homeIntent = new Intent(LoanDetailsActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });
    }
}
