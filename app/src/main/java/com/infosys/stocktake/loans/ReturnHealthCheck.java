package com.infosys.stocktake.loans;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.infosys.stocktake.HomeActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
//import com.infosys.stocktake.inventory.InventoryActivity;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.Loan;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.w3c.dom.Text;

import java.util.Date;

public class ReturnHealthCheck extends AppCompatActivity {
    final String TAG = "ReturnHealthCheck";
    final StockTakeFirebase<Loan> stockTakeFirebaseLoan = new StockTakeFirebase<>(Loan.class,"loans");
    final StockTakeFirebase<Item> stockTakeFirebaseItem = new StockTakeFirebase<>(Item.class, "items");
    int qtyAvail, qtyBroken,qtyMissing, qtyLoaned;
    TextView healthyQtySet, brokenQtySet, missingQtySet;
    Button returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_health_check);
        Intent loanIdIntent = getIntent();
        String loanId = loanIdIntent.getStringExtra("loan-id");
        Log.d(TAG, "Received loan-id from intent: " + loanId);
        healthyQtySet = findViewById(R.id.healthyQtySet);
        brokenQtySet = findViewById(R.id.brokenQtySet);
        missingQtySet = findViewById(R.id.missingQtySet);
        returnBtn = findViewById(R.id.healthCheckReturnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Setting on click listener");

                returnItems(loanId);
            }
        });
        loadSeekBars(loanId);


    }

    private void loadSeekBars(String loanId) {
        Log.d(TAG, "Loan Id: " + loanId);
        stockTakeFirebaseLoan.query(loanId).addOnSuccessListener(new OnSuccessListener<Loan>() {
            @Override
            public void onSuccess(Loan loan) {
                stockTakeFirebaseItem.query(loan.getItemID()).addOnSuccessListener(new OnSuccessListener<Item>() {
                    @Override
                    public void onSuccess(Item item) {
//                        qtyAvailable = item.getQtyStatus().get(ItemStatus.AVAILABLE.toString());
//                        qtyBroken = item.getQtyStatus().get(ItemStatus.BROKEN.toString());
                        qtyLoaned = loan.getQuantity();
                        Log.d(TAG, "Got quantity loaned: " + qtyLoaned);
                        DiscreteSeekBar healthSeekBar = (DiscreteSeekBar) findViewById(R.id.healthySeekBar);
//        healthSeekBar.setMax(qtyAvailable);
                        healthSeekBar.setMax(qtyLoaned);
                        healthSeekBar.setMin(0);

                        DiscreteSeekBar brokenSeekBar = (DiscreteSeekBar) findViewById(R.id.brokenSeekBar);
                        brokenSeekBar.setMax(qtyLoaned);
                        brokenSeekBar.setMin(0);

                        DiscreteSeekBar missingSeekBar = (DiscreteSeekBar) findViewById(R.id.missingSeekBar);
                        missingSeekBar.setMax(qtyLoaned);
                        missingSeekBar.setMin(0);

                        healthSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                            @Override
                            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                                Log.d(TAG , "healthy value changed to " + value);
                                healthyQtySet.setText(String.valueOf(value));
                                qtyAvail = value;
                            }

                            @Override
                            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                            }
                        });

                        brokenSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                            @Override
                            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                                Log.d(TAG, "broken value changed to " + value);
                                brokenQtySet.setText(String.valueOf(value));
                                qtyBroken = value;
                            }

                            @Override
                            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                            }
                        });


                        missingSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                            @Override
                            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                                Log.d(TAG, "broken value changed to " + value);
                                missingQtySet.setText(String.valueOf(value));
                                qtyMissing = value;
                            }

                            @Override
                            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                            }
                        });
                    }
                });

            }
        });
    }

    private void returnItems(String loanId) {
        // If the quantities set do not match do not match the qty loaned out
        if (qtyAvail + qtyBroken + qtyMissing != qtyLoaned) {
            Log.d(TAG, "Why no work");
            Toast.makeText(this, "Quantities do not match quantity loaned of " + qtyLoaned, Toast.LENGTH_SHORT).show();
        } else {
            // Carry out successful loan return procedure
            stockTakeFirebaseLoan.query(loanId).addOnSuccessListener(new OnSuccessListener<Loan>() {
                @Override
                public void onSuccess(Loan loan) {
                    loan.setReturnDate(new Date());
                    stockTakeFirebaseLoan.update(loan, loan.getLoanID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            stockTakeFirebaseItem.query(loan.getItemID()).addOnSuccessListener(new OnSuccessListener<Item>() {
                                @Override
                                public void onSuccess(Item item) {
                                    int dbQtyAvailable = item.getQtyStatus().get(ItemStatus.AVAILABLE.toString());
                                    int dbQtyBroken = item.getQtyStatus().get(ItemStatus.BROKEN.toString());
                                    // TODO: ADD IN MISSING FIELD IN DB SCHEMA

                                    dbQtyAvailable+= qtyAvail;
                                    dbQtyBroken += qtyBroken;
                                    item.setQtyStatus(ItemStatus.AVAILABLE.toString(), dbQtyAvailable);
                                    item.setQtyStatus(ItemStatus.BROKEN.toString(), dbQtyBroken);
                                    stockTakeFirebaseItem.update(item, item.getItemID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ReturnHealthCheck.this, "Item Successfully Returned", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ReturnHealthCheck.this, HomeActivity.class);
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
};