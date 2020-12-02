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

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Loan implements Serializable{

    private static final String TAG="Loan Operations";
    // CONSTANTS
    public static final String LOAN_COLLECTION = "loans";
    public static final String ITEM_ID = "itemID";

    private String itemID;
    private int quantity;

    private String loaneeID;
    private Date loanDate;
    private Date dueDate;
    private Boolean returned;


    private Date returnDate;
    private String loanID;


    private String clubID;

    Loan queryLoan;
    Club loanerClub;
    FirebaseFirestore db;
    CollectionReference loans;
    CollectionReference clubs;



    public Loan(){}

    public Loan(String loanID, String itemID, int quantity , String clubID, String loaneeID, Date dueDate){
        this.loanID = loanID;
        this.quantity = quantity;
        this.itemID = itemID;
        this.clubID = clubID;
        this.loaneeID = loaneeID;
        this.loanDate = new Date();
        this.dueDate = dueDate;
        this.returned = false;
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

    public void setLoaneeID(String loaneeID) {
        this.loaneeID = loaneeID;
    }
}
