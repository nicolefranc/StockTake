package com.infosys.stocktake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.infosys.stocktake.MainActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.auth.GoogleLoginActivity;

import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.net.URL;
import java.util.HashMap;

public class Profile extends AppCompatActivity {
    private static final String TAG = Profile.class.getSimpleName();
    TextView nameView, telegramView, studentIDView, profileClubView, profileMembershipView;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutButton = findViewById(R.id.logoutButton);
        nameView = findViewById(R.id.name);
        telegramView = findViewById(R.id.telegramhandle);
        studentIDView = findViewById(R.id.studentID);
        profileClubView = findViewById(R.id.profileClubName);
        profileMembershipView = findViewById(R.id.profileMembershipInfo);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        if (signInAccount  != null) {
            nameView.setText(signInAccount.getDisplayName());
        }
        loadUserInfo2();
//


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                // Define which page to go to after logging out
                Intent intent = new Intent(getApplicationContext(), GoogleLoginActivity.class);
                startActivity(intent);

            }
        });
    }


    private void loadUserInfo2() {
        final String documentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StockTakeFirebase<User> stockTakeFirebase = new StockTakeFirebase<>(User.class, "users");
        stockTakeFirebase.query(documentId).addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                String telegramHandle = user.getTelegramHandle();
                String studentId = Integer.toString(user.getStudentID());


                HashMap<String, Membership> userClubInfo = user.getClubMembership();
                for (String clubId: userClubInfo.keySet()) {
                    Membership membershipInfo = userClubInfo.get(clubId);
                    profileMembershipView.setText(membershipInfo.toString());
                    // QUERY THE CLUBS COLLECTION AND SET THE CLUB NAME, PASSING IN THE CLUB ID
                    updateClubNameField(clubId);

                }

                telegramView.setText(telegramHandle);
                studentIDView.setText(studentId);
            }
        });
    }
    private void loadUserInfo() {
//        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String documentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(documentId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String telegramHandle = documentSnapshot.get("telegramHandle").toString();
                        String studentId = documentSnapshot.get("studentID").toString();
                        User user = documentSnapshot.toObject(User.class);
                        HashMap<String, Membership> userClubInfo = user.getClubMembership();
                        for (String clubId: userClubInfo.keySet()) {
                            Membership membershipInfo = userClubInfo.get(clubId);
                            profileMembershipView.setText(membershipInfo.toString());
                            // QUERY THE CLUBS COLLECTION AND SET THE CLUB NAME, PASSING IN THE CLUB ID
                            updateClubNameField(clubId);

                        }
//                        profileClubView.setText();
//                        Log.d(TAG, documentSnapshot.get("clubMembership").toString());
//                        HashMap<String, String> clubMembership= documentSnapshot.get("clubMembership").to;
                        telegramView.setText(telegramHandle);
                        studentIDView.setText(studentId);
//                        Log.d(TAG, telegramHandle);
//
//                        Log.d(TAG, studentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, "Error retrieving credentials", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateClubNameField(String clubId) {
//        StockTakeFirebase<Club> stockTakeFirebase = new StockTakeFirebase<>(Club.class, "clubs");
//        stockTakeFirebase.query(clubId).addOnSuccessListener(new OnSuccessListener<Club>() {
//            @Override
//            public void onSuccess(Club club) {
//                String clubName = club.getClubName();
//                profileClubView.setText(clubName);
//                Log.d(TAG, "Set Club Info Successfully");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w(TAG, "Failed to set ClubInfo" + e.toString());
//            }
//        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clubs")
                .whereEqualTo("clubID", clubId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document: task.getResult()) {
                                String clubName = document.get("clubName").toString();
                                profileClubView.setText(clubName);
                                Log.d(TAG, "Set Club Info Successfully");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());

                        }
                    }
                });
    }


}