package com.infosys.stocktake.models;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Club {
    private final String TAG = "CLUB Operations";
    private String clubID;
    private String clubName;
    private int loanCounter;
    //list of club admins' student IDs who have approval
    private String[] adminList;

    FirebaseFirestore db;
    CollectionReference clubs;
    Club queryClub;

    public Club(){
        loanCounter = getLoanCounter();
    }
    public Club(String clubID, String clubName){
        this.clubID = clubID;
        this.clubName = clubName;
        loanCounter = 0;
    }


    /*getClub to be migrated to activity file
     */
    public Club getClub(final String clubID){
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        clubs = db.collection("clubs");
        DocumentReference clubRef = db.collection("clubs").document(clubID);
        clubRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                queryClub = documentSnapshot.toObject(Club.class);
                Log.d(TAG, "CLUB "+ clubID +"has been successfully fetched!");
            }
        });
        return queryClub;
    }
    public int getLoanCounter(){ return loanCounter;}
    public void increaseLoanCounter(){loanCounter+=1;}
    public String getClubID() {
        return clubID;
    }

    public void setClubID(String clubID) {
        this.clubID = clubID;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String[] getAdminList() {
        return adminList;
    }

    public void setAdminList(String[] adminList) {
        this.adminList = adminList;
    }
}
