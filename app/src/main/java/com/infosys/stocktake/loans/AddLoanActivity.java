package com.infosys.stocktake.loans;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.Loan;

import java.util.Date;


public class AddLoanActivity extends AppCompatActivity {
    private final String TAG = "ADD LOAN";
    public static final String LOAN_INTENT_KEY = "LOAN_ID_INTENT";

    Button loanButton;
    TextView itemNameText,itemIDText,clubIDText;
    EditText quantityEdit;
    String itemID;
    Loan currentLoan;
    Item currentItem;
    FirebaseUser currentUser;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        //Linking components to the XML
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_add);
        loanButton = findViewById(R.id.loanButton);
        itemNameText = findViewById(R.id.itemNameText);
        itemIDText = findViewById(R.id.itemIDText);
        clubIDText = findViewById(R.id.clubIDText);
        quantityEdit = findViewById(R.id.quantityEdit);

        //Firebase Operations
        final StockTakeFirebase<Item> stockTakeFirebaseItem = new StockTakeFirebase<>(Item.class,"items");
        final StockTakeFirebase<Loan> stockTakeFirebaseLoan = new StockTakeFirebase<>(Loan.class, "loans");
        final StockTakeFirebase<Club> stockTakeFirebaseClub = new StockTakeFirebase<>(Club.class, "clubs");

        //Getting User and Item Details
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        /* TODO
        Fetch the item details given the item id (explicit intent from the previous one must give item id)
        */
        // itemID = itemIDIntent.getIntExtra(PreviousActivity.ITEM_ID, 00000);
        //Intent itemIntent = getIntent();

        //Hardcoding item details
        itemID = "748379437-6282858f";


        //Set the item details to the respective fields
        stockTakeFirebaseItem.query(itemID).addOnSuccessListener(item -> {
            //TODO set the item image
            currentItem = item;
            itemNameText.setText(currentItem.getItemName());
            itemIDText.setText(currentItem.getItemID());
            clubIDText.setText(currentItem.getClubID());
        });
        //Add OnClickListener to the loan button
        loanButton.setOnClickListener(v -> {
            if(quantityEdit.getText().toString().equals("")){
                Toast.makeText(AddLoanActivity.this, R.string.invalid_quantity, Toast.LENGTH_SHORT).show();
            }
            else{
                stockTakeFirebaseClub.query(currentItem.getClubID()).addOnSuccessListener(new OnSuccessListener<Club>() {
                    @Override
                    public void onSuccess(Club club) {
                        String loanID = club.getClubID()+"-"+club.getLoanCounter();
                        int loanQuantity = Integer.parseInt(quantityEdit.getText().toString());
                        currentLoan = new Loan(loanID,currentItem.getItemID(),loanQuantity,club.getClubID(), currentUser.getUid(),new Date());
                        club.increaseLoanCounter();
                        updateLoanCounter(club);
                        //Firebase create a new Loan object
                        stockTakeFirebaseLoan.create(currentLoan,loanID).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddLoanActivity.this, R.string.create_loan_success, Toast.LENGTH_SHORT).show();
                                Log.d(TAG,AddLoanActivity.this.getResources().getString(R.string.create_loan_success));
                                Intent detailsIntent = new Intent(AddLoanActivity.this, LoanDetailsActivity.class);
                                detailsIntent.putExtra(LOAN_INTENT_KEY,currentLoan);
                                startActivity(detailsIntent);
                            }
                        });
                    }
                }).addOnFailureListener(e -> Log.e(TAG,"FAIL TO QUERY CLUB IN ADD LOAN"));
            }
        });


    }
    private void updateLoanCounter(Club club){
        StockTakeFirebase<Club> stockTakeFirebaseClub = new StockTakeFirebase<>(Club.class,"clubs");
        stockTakeFirebaseClub.update(club,club.getClubID()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Successfully added loan counter of club: "+club.getClubID() + "Current counter: "+club.getLoanCounter());
            }
        });
    }
}
