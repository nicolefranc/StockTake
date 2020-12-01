package com.infosys.stocktake.models;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Club {
    private final String TAG = "CLUB Operations";
    private String clubID;
    private String clubName;
    private int loanCounter;
    //list of club admins' student IDs who have approval
    ArrayList<String> adminList;

    public Club(){}
    public Club(String clubID, String clubName){
        this.clubID = clubID;
        this.clubName = clubName;
    }

    public int getLoanCounter(){ return loanCounter;}
    public void increaseLoanCounter(){
        this.loanCounter+=1;
    }
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

    public ArrayList<String> getAdminList() {
        return adminList;
    }

    public void addAdmin (String uuid){adminList.add(uuid);}
    public void removeAdmin (String uuid) {adminList.remove(uuid);}
}
