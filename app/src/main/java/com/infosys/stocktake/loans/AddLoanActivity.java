package com.infosys.stocktake.loans;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.infosys.stocktake.nfc.NfcReaderActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddLoanActivity extends AppCompatActivity {
    private final String TAG = "ADD LOAN";
    public static final String LOAN_INTENT_KEY = "LOAN_ID_INTENT";

    Button loanButton;
    TextView itemNameText;
    EditText date_of_return;
    ImageView ivItemPicture;
    TextView loanCounter;

    Loan currentLoan;
    Item currentItem;
    FirebaseUser currentUser;
    Calendar myCalendar;
    int minteger = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        //Linking components to the XML
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_add);
        loanButton = findViewById(R.id.loanButton);
        itemNameText = findViewById(R.id.itemNameText);
        date_of_return = findViewById(R.id.date_of_return);
        ivItemPicture = findViewById(R.id.ivItemPicture);
        loanCounter = findViewById(R.id.loanCounterText);

        myCalendar  = Calendar.getInstance();

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
//        itemID = "748379437-6282858f";
        // Get Item Intent from scanning qr (QrOptionsActivity)
        currentItem = (Item) getIntent().getSerializableExtra("ItemIntent");


        //Set the item details to the respective fields
//        stockTakeFirebaseItem.query(itemID).addOnSuccessListener(item -> {
//            TODO set the item image
        Picasso.get().load(currentItem.getItemPicture())
                .fit().centerCrop().into(ivItemPicture);
//            currentItem = item;
            itemNameText.setText(currentItem.getItemName());

//        });
        //Add OnClickListener to the loan button
        loanButton.setOnClickListener(v -> {
            if(loanCounter.getText().toString().equals("")){
                Toast.makeText(AddLoanActivity.this, R.string.invalid_quantity, Toast.LENGTH_SHORT).show();
            }
            else{
                stockTakeFirebaseClub.query(currentItem.getClubID()).addOnSuccessListener(new OnSuccessListener<Club>() {
                    @Override
                    public void onSuccess(Club club) {
                        String loanID = club.getClubID()+"-"+club.getLoanCounter();
                        int loanQuantity = Integer.parseInt(loanCounter.getText().toString());
                        currentLoan = new Loan(loanID,currentItem.getItemID(),loanQuantity,club.getClubID(), currentUser.getUid(),new Date());
                        club.increaseLoanCounter();
                        updateLoanCounter(club);

                        // Proceed to read NFC
                        Intent detailsIntent = new Intent(AddLoanActivity.this, NfcReaderActivity.class);
                        detailsIntent.putExtra("LoanIntent", currentLoan);
                        detailsIntent.putExtra("source", "loan");
                        startActivity(detailsIntent);

                        //Firebase create a new Loan object
//                        stockTakeFirebaseLoan.create(currentLoan,loanID).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(AddLoanActivity.this, R.string.create_loan_success, Toast.LENGTH_SHORT).show();
//                                Log.d(TAG,AddLoanActivity.this.getResources().getString(R.string.create_loan_success));
////                                Intent detailsIntent = new Intent(AddLoanActivity.this, LoanDetailsActivity.class);
//                                Intent detailsIntent = new Intent(AddLoanActivity.this, NfcReaderActivity.class);
////                                detailsIntent.putExtra(LOAN_INTENT_KEY,loanID);
//                                detailsIntent.putExtra("LoanIntent", currentLoan);
//                                detailsIntent.putExtra("source", "loan");
//                                startActivity(detailsIntent);
//                            }
//                        });
                    }
                }).addOnFailureListener(e -> Log.e(TAG,"FAIL TO QUERY CLUB IN ADD LOAN"));
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        date_of_return.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddLoanActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    public void increaseInteger(View view) {
        minteger = minteger + 1;
        display(minteger);

    }public void decreaseInteger(View view) {
        minteger = minteger - 1;
        display(minteger);
    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.loanCounterText);
        displayInteger.setText("" + number);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date_of_return.setText(sdf.format(myCalendar.getTime()));
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
