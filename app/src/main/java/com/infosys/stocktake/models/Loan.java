package com.infosys.stocktake.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Loan {

    private static final String TAG="Loan Operations";

    private String itemID;
    private int quantity;

    private String loaneeID;
    private Date loanDate;
    private Date dueDate;
    private Boolean returned;


    private Date returnDate;
    private String loanID;


    private final String clubID;

    Loan queryLoan;
    Club loanerClub;
    FirebaseFirestore db;
    CollectionReference loans;
    CollectionReference clubs;




    public Loan(String itemID, int quantity, String clubID, String loaneeID, Date dueDate){
        this.itemID = itemID;
        this.quantity = quantity;
        this.clubID = clubID;
        this.loaneeID = loaneeID;
        this.loanDate = new Date();
        this.dueDate = dueDate;
        this.returned = false;
    }
    /*
    createLoan and getLoan to be done in the activities
     */
    public void createLoan(){
        db = FirebaseFirestore.getInstance();
        loans = db.collection("loans");
        loanerClub = new Club().getClub(this.clubID);
        loanID = clubID+"-"+loanerClub.getLoanCounter();
        loans.document(loanID)
                .set(this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "LOAN  "+ loanID + "has been successfully added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating LOAN "+loanID, e);
                    }
                });
    }
    public Loan getLoan(final String loanID) {
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        loans = db.collection("loans");

        DocumentReference loanRef = loans.document(loanID);
        loanRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                queryLoan = documentSnapshot.toObject(Loan.class);
                Log.d(TAG, "Loan "+ loanID + "has been successfully fetched!");
            }
        });
        return queryLoan;
    }

    public String getItemID() {
        return itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getClubID() {
        return clubID;
    }

    public String getLoaneeID() {
        return loaneeID;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Boolean getReturned() {
        return returned;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public String getLoanID() {
        return loanID;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }


}
