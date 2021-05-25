package com.infosys.stocktake.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.infosys.stocktake.Profile;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.User;
import com.infosys.stocktake.nfc.NfcReaderActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileSetupActivity extends AppCompatActivity {
    //    final String TAG = ProfileSetupActivity.class.getSimpleName();
    final String TAG = "User";
    Button nextButton;
    EditText studentIdField, teleHandleField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        studentIdField = findViewById(R.id.studentIdField);
        teleHandleField = findViewById(R.id.teleHandleField);


        nextButton = findViewById(R.id.next);

        studentIdField.setElevation(8 / this.getResources().getDisplayMetrics().density);
        teleHandleField.setElevation(8 / this.getResources().getDisplayMetrics().density);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileSetupAddClubActivity.studentId = studentIdField.getText().toString();
                ProfileSetupAddClubActivity.telegramHandle = teleHandleField.getText().toString();

            }
        });

    }


}










