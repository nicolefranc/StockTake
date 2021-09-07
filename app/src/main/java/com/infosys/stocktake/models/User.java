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

    // CONSTANTS
    public static final String USER_COLLECTION = "users";
    public static final String UUID = "uuid";
    public static final String USER = "user";
    public static final String CLUB_MEMBERSHIP = "clubMembership";
    public static final String NAME = "fullName";
    public static final String ITEMS_BORROWED = "itemsBorrowed";
    public static final String NFC_TAG = "nfcTag";
    public static final String STUDENT_ID = "studentID";
    public static final String TELE_HANDLE = "telegramHandle";

    private String uuid;
    private String nfcTag;
    private String fullName;
    private int studentID;
    private String telegramHandle;
    private HashMap<String,Membership> clubMembership;
    private boolean superAdmin;

//    private String[] itemsBorrowed;
//    User queryUser;
//    FirebaseFirestore db;
//    CollectionReference users;

    public User(){}
    public User(int studentID,
                String telegramHandle,
                GoogleSignInAccount signInAccount){
        Log.d("User", "User being instantiated");
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fbAuth.getCurrentUser();
        this.nfcTag = null;
        this.studentID = studentID;
        this.fullName = signInAccount.getDisplayName();
        this.telegramHandle = telegramHandle;
        this.clubMembership = new HashMap<>();
//        clubMembership.put("0q38hnjpiou4", Membership.ADMIN);
        if(currentUser != null){
            this.uuid = currentUser.getUid();
        }
        else{
            Log.e("USER DB","USER GOOGLE ACCOUNT NOT AUTHENTICATED");
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

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        superAdmin = superAdmin;
    }
}
