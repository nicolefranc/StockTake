package com.infosys.stocktake.qr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.infosys.stocktake.MainActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.ItemDetailsActivity;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.Loan;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class QrScannerActivity extends AppCompatActivity {
    private static final String TAG = "QR";
    private static final int RC_PERMISSION = 10;
    private CodeScanner mCodeScanner;
    private boolean mPermissionGranted;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);


        CodeScannerView scannerView = findViewById(R.id.scannerView);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(QrScannerActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(QrScannerActivity.this, MainActivity.class);
//                        startActivity(intent);
                        identifyActivityToStart(result.getText());
                    }
                });
            }
        });
        mCodeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull final Exception error) {
                QrScannerActivity.this.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QrScannerActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false;
                requestPermissions(new String[] {Manifest.permission.CAMERA}, RC_PERMISSION);
            } else {
                mPermissionGranted = true;
            }
        } else {
            mPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                mCodeScanner.startPreview();
            } else {
                mPermissionGranted = false;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private boolean isAvailableForLoan(Item item) {
        Number qtyAvail = item.getQtyStatus().get(ItemStatus.AVAILABLE.toString());
        Log.d(TAG, String.valueOf(qtyAvail.intValue() > 0));
        return qtyAvail.intValue() > 0;
    }

    private void displayActivityByMembership(Item item, boolean isAvailable) {
        // 1. Extract club details based on the owner of the item
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUid = mAuth.getCurrentUser().getUid();
        String clubID = item.getClubID();

        Log.d(TAG, currentUid);
        StockTakeFirebase userRepo = new StockTakeFirebase(User.class, User.USER_COLLECTION);
        Task<ArrayList<User>> usersTask = userRepo.compoundQuery(User.UUID, currentUid);

        usersTask.addOnSuccessListener(new OnSuccessListener<ArrayList<User>>() {
            @Override
            public void onSuccess(ArrayList<User> users) {
                for (User user : users) {
                    if (user.getClubMembership().get(clubID) == Membership.ADMIN) {
                        Log.d(TAG, "Club owner.");
                        Log.d(TAG, isAvailable ? "Allow Loan." : "Cannot Loan");
                        // Club owner show inventory detail and loan out option
                        // Club owner and item still available, proceed to loan screen
                        // if not available, don't show the loan out button
                        Intent intent = new Intent(QrScannerActivity.this, QrOptionsActivity.class);
                        intent.putExtra("canLoan", isAvailable);
                        startActivity(intent);
                    } else {
                        // Not a club owner, check if user is the one borrowing
                        // items.loaneeID == current uuid

                        // Retrieve the item in Loans collection
                        // TODO: Uncomment when Loans collection has data to retrieve
//                        StockTakeFirebase loansRepo = new StockTakeFirebase(Loan.class, Loan.LOAN_COLLECTION);
//                        Task<ArrayList<Loan>> loansTask = loansRepo.compoundQuery(Loan.ITEM_ID, item.getItemID());
//                        loansTask.addOnSuccessListener(new OnSuccessListener<ArrayList<Loan>>() {
//                            @Override
//                            public void onSuccess(ArrayList<Loan> loans) {
//                                for (Loan loan : loans) {
//                                    if (loan.getLoaneeID() == currentUid) {
//                                        Log.d(TAG, "The loanee/borrower.");
//                                        // TODO: Change to go to Personal Loan History for this Item
//                                        Intent intent = new Intent(QrScannerActivity.this, MainActivity.class);
//                                        startActivity(intent);
//                                    } else {
////                                         TESTED!
//                                        Log.d(TAG, "Some rando.");
//                                        // else, show inventory detail
//                                        Intent intent = new Intent(QrScannerActivity.this, ItemDetailsActivity.class);
//                                        intent.putExtra("ItemIntent", item);
//                                        startActivity(intent);
//                                    }
//                                }
//                            }
//                        });

                        Log.d(TAG, "Not a club owner.");
                        if(item.getLoaneeID() == currentUid) {
                            Log.d(TAG, "The loanee/borrower.");
                            // TODO: Change to go to Personal Loan History for this Item
                            Intent intent = new Intent(QrScannerActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // TESTED!
                            Log.d(TAG, "Some rando.");
                            // else, show inventory detail
                            Intent intent = new Intent(QrScannerActivity.this, ItemDetailsActivity.class);
                            intent.putExtra("ItemIntent", item);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    } // end isClubOwner

//    private void identifyActivityToStart(String itemID) {
//
//        // 1. Get the itemID from QR code and extract item document
//        db.collection(Item.ITEM_COLLECTION)
//                .whereEqualTo(Item.ITEM_ID, itemID)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                // 1.1 Extract item details and create Item instance
//                                Item item = new Item(document.getData());
//
//                                // 1.2 Check if item is loaned out and redirect to appropriate activity
//                                if (isAvailableForLoan(item)) {
////                                if (isClubOwner(item.getClubID())) {
//                                    // AVAILABLE FOR LOAN
//                                    Log.d(TAG, "YES");
//                                    isClubOwner(item, true);
//                                } else {
//                                    // NOT AVAILABLE FOR LOAN
//                                    // TODO: Redirect to 'Unavailable for Loan' screen
//                                    Toast.makeText(QrScannerActivity.this, "Unavailable for loan", Toast.LENGTH_SHORT).show();
//                                    Log.d(TAG, "NO");
//                                    isClubOwner(item, false);
//                                }
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//    }


    private void identifyActivityToStart(String itemID) {
        // 1. Extract club details based on the owner of the item
        StockTakeFirebase itemsRepo = new StockTakeFirebase(Item.class, Item.ITEM_COLLECTION);

        Task<ArrayList<Item>> itemsTask = itemsRepo.compoundQuery(Item.ITEM_ID, itemID);
        itemsTask.addOnSuccessListener(new OnSuccessListener<ArrayList<Item>>() {
            @Override
            public void onSuccess(ArrayList<Item> items) {
                for(Item item : items) {
                    boolean isAvailable = isAvailableForLoan(item);
                    Log.d(TAG, String.valueOf(isAvailable));
                    displayActivityByMembership(item, isAvailable);
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
                // TODO: Redirect to invalid screen
                Toast.makeText(QrScannerActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(QrScannerActivity.this, "Testing", Toast.LENGTH_SHORT).show();
    }
}