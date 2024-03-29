package com.infosys.stocktake.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.Request;
import com.infosys.stocktake.models.RequestStatus;
import com.infosys.stocktake.models.User;
import com.infosys.stocktake.nfc.NfcReaderActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileSetupAddClubActivity extends AppCompatActivity {
    //    final String TAG = ProfileSetupActivity.class.getSimpleName();
    final String TAG = "User";
    Button profileSetupButton;
    Button addclub;
    Spinner clubSpinner;
    Spinner userSpinner;
    ListView clublist;
    static List<String> clubArrayList;
    static Map<String, String> clubIDArrayList;
    List<String> userTypeArrayList;
    static String studentId, telegramHandle;
    ArrayList<ClubDataModel> dataModels;
    static ArrayAdapter<String> clubdataAdapter;
    private static ClubDataAdapter adapter;
    String userId;
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    StockTakeFirebase<Request> requestStockTakeFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_add_club);

        clubSpinner = findViewById(R.id.userclub);
        userSpinner = findViewById(R.id.userpriviledge);
        addclub = findViewById(R.id.addClub);
        clublist = findViewById(R.id.list);
        profileSetupButton = findViewById(R.id.finProfileSetupBtn);

        userId = fbAuth.getCurrentUser().getUid();
        requestStockTakeFirebase = new StockTakeFirebase<Request>(Request.class, "requests");

        // TODO: Load Spinner Data
        loadClubData();
        loadUserTypeData();

        dataModels = new ArrayList<>();
        adapter = new ClubDataAdapter(dataModels,getApplicationContext());
        clublist.setAdapter(adapter);

        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        addclub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clubChoice =  clubSpinner.getSelectedItem().toString();
                String userType = userSpinner.getSelectedItem().toString();
                dataModels.add(new ClubDataModel(clubChoice, userType));
                clublist.setAdapter(adapter);
                clubArrayList.remove(clubChoice);
                clubdataAdapter.notifyDataSetChanged();
            }
        });


        profileSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User newUser = new User(Integer.parseInt(studentId) , telegramHandle , signInAccount);

                for (int i = 0; i< dataModels.size(); i++){
                    String clubChoice = dataModels.get(i).getClubName();
                    String userPriviledges = dataModels.get(i).getUserType();
                    String clubId = clubIDArrayList.get(clubChoice);
                    if(userPriviledges.equals("Member")){
                        newUser.setClubMembership(clubId, Membership.MEMBER );
                    }
                    else{
                        // User must send request to become an admin
//                        newUser.setClubMembership(clubId, Membership.ADMIN);
                        sendAdminRequest(clubId);
                    }
                }

                Intent intent = new Intent(getApplicationContext(), NfcReaderActivity.class);
                intent.putExtra("UserIntent", newUser);
                intent.putExtra("source", "profile");
                startActivity(intent);


            }});





    }

    private void sendAdminRequest(String clubId) {
        requestStockTakeFirebase.getCollection().addOnSuccessListener(new OnSuccessListener<ArrayList<Request>>() {
            @Override
            public void onSuccess(ArrayList<Request> requests) {
                int count;
                if (requests != null) {
                    count = requests.size();
                } else {
                    count = 0;
                }
                String reqId = "REQ-" + count;

                Request newRequest = new Request(reqId, clubId, userId, RequestStatus.PENDING);

                requestStockTakeFirebase.create(newRequest, reqId).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Admin Request Sent Successfully");
                    }
                });


            }
        });
    }


    private void loadClubData() {
//        StockTakeFirebase<Club> stockTakeFirebase = new StockTakeFirebase<>(Club.class, "clubs");

        clubArrayList = new ArrayList<>();
        clubIDArrayList = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clubs")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Club> clubs = queryDocumentSnapshots.toObjects(Club.class);
                        for (Club c: clubs) {
                            clubArrayList.add(c.getClubName());
                            clubIDArrayList.put(c.getClubName(), c.getClubID());
                        }
                        Collections.sort(clubArrayList);
                        clubdataAdapter = new ArrayAdapter<String>(ProfileSetupAddClubActivity.this, android.R.layout.simple_spinner_item, clubArrayList);
                        clubdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        clubSpinner.setAdapter(clubdataAdapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error getting data!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadUserTypeData() {
//        StockTakeFirebase<Club> stockTakeFirebase = new StockTakeFirebase<>(Club.class, "clubs");

        userTypeArrayList = new ArrayList<>();
        userTypeArrayList.add("Member");
        userTypeArrayList.add("Admin");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ProfileSetupAddClubActivity.this, android.R.layout.simple_spinner_item, userTypeArrayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        userSpinner.setAdapter(dataAdapter);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Toast.makeText(this,"Please complete the registration process",Toast.LENGTH_SHORT).show();
    }
}










