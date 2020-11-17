package com.infosys.stocktake.models;

import android.util.Log;

import androidx.annotation.NonNull;

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

public class User {
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
    //Account Creation during first sign up
    public void createUser(){
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(this.uuid)
                .set(this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "USER "+ studentID + "has been successfully added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding USER", e);
                    }
                });

    }
    public User getUser() {
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        String queryUUID = fbAuth.getCurrentUser().getUid();
        db= FirebaseFirestore.getInstance();
        users = db.collection("users");
        DocumentReference docRef = db.collection("users").document(queryUUID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                queryUser = documentSnapshot.toObject(User.class);
                Log.d(TAG, "USER "+ queryUser.studentID + "has been successfully fetched!");
            }
        });
        return queryUser;
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
