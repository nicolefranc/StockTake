package com.infosys.stocktake.models;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class User implements Serializable {
    private String TAG="USER DB";
    private String uuid;
    private String nfcTag;
    private String fullName;
    private int studentID;
    private String telegramHandle;
    private HashMap<String,Membership> clubMembership;
    private String[] itemsBorrowed;
    User queryUser;
    FirebaseFirestore db;
    CollectionReference users;

    public User(){}
    public User(int studentID,
                String telegramHandle,
                GoogleSignInAccount signInAccount){
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fbAuth.getCurrentUser();

        this.studentID = studentID;
        this.fullName = signInAccount.getDisplayName();
        this.telegramHandle = telegramHandle;
        this.clubMembership = new HashMap<>();
        if(currentUser != null){
            this.uuid = currentUser.getUid();
        }
        else{
            Log.e(TAG,"USER GOOGLE ACCOUNT NOT AUTHENTICATED");
        }
    }



    /***********************************************************************************************
     ACCESSORs
     ***********************************************************************************************/

    public void setClubMembership(String clubID, Membership membership){
        this.clubMembership.put(clubID, membership);
    }

    public void deleteClubMembership(String clubID){
        this.clubMembership.remove(clubID);
    }

    //call this when user is prompted to tap the card
    public void setNfcTag(String nfcTag){
        this.nfcTag= nfcTag;
    }
    public String getUuid() {
        return uuid;
    }

    public String getNfcTag() {
        return nfcTag;
    }

    public String getFullName() {
        return fullName;
    }

    public int getStudentID() {
        return studentID;
    }

    public String getTelegramHandle() {
        return telegramHandle;
    }

    public HashMap<String, Membership> getClubMembership() {
        return clubMembership;
    }

    public String[] getItemsBorrowed() {
        return itemsBorrowed;
    }



}
