package com.infosys.stocktake.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.infosys.stocktake.Profile;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ProfileSetupActivity extends AppCompatActivity {
    final String TAG = ProfileSetupActivity.class.getSimpleName();
    Button profileSetupButton;
    TextInputEditText studentIdField, teleHandleField;
    Spinner clubSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        studentIdField = findViewById(R.id.studentIdField);
        teleHandleField = findViewById(R.id.teleHandleField);
        clubSpinner = findViewById(R.id.clubSpinner);

        profileSetupButton = findViewById(R.id.finProfileSetupBtn);

        // TODO: Load Spinner Data
        loadClubData();

        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        profileSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentId = studentIdField.getText().toString().trim();
                String telegramHandle = teleHandleField.getText().toString().trim();
                String clubChoice =  clubSpinner.getSelectedItem().toString();

                if (!TextUtils.isEmpty(studentId) && !TextUtils.isEmpty(telegramHandle) && signInAccount != null) {
                    User newUser = new User( Integer.parseInt(studentId) , telegramHandle , signInAccount);
                    String NOT_EXCO_ID = "q93pgnhj3q5g";
                    if (clubChoice.equals("Not a Club Exco")) {
                        newUser.setClubMembership( NOT_EXCO_ID, Membership.MEMBER );
                        newUser.createUser();
                        Intent intent = new Intent(getApplicationContext(), Profile.class);
                        startActivity(intent);

                    } else {
                        // TODO: STANDARDISE CLUB IDS based on ClubChoice
                        // We have the club name
                        // We need to query the club collection for the CLub ID associated with that Club Name
                        setClubIdFromName(clubChoice, newUser);

                    }

                }
            }
        });



        }

        private void setClubIdFromName (final String clubName, final User user) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("clubs")
                    .whereEqualTo("clubName", clubName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String clubId = document.get("clubID").toString();
                                    Log.d(TAG, clubId);
                                    user.setClubMembership(clubId, Membership.ADMIN);
                                    user.createUser();
                                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                                    startActivity(intent);

                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }

//        private void setMembershipFromId(String clubID, User user) {
//
//        }


    private void loadClubData() {
        clubSpinner = findViewById(R.id.clubSpinner);
//        StockTakeFirebase<Club> stockTakeFirebase = new StockTakeFirebase<>(Club.class, "clubs");

        final List<String> mArrayList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clubs")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Club> clubs = queryDocumentSnapshots.toObjects(Club.class);
                        for (Club c: clubs) {
                            mArrayList.add(c.getClubName());
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ProfileSetupActivity.this, android.R.layout.simple_spinner_item, mArrayList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        clubSpinner.setAdapter(dataAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error getting data!", Toast.LENGTH_SHORT).show();
                    }
                });

        }
    }










