package com.infosys.stocktake.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.infosys.stocktake.Profile;
import com.infosys.stocktake.R;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.infosys.stocktake.models.User;

public class ProfileSetupActivity extends AppCompatActivity {
    Button profileSetupButton;
    TextInputEditText studentIdField, teleHandleField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        studentIdField = findViewById(R.id.studentIdField);
        teleHandleField = findViewById(R.id.teleHandleField);

        profileSetupButton = findViewById(R.id.finProfileSetupBtn);
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        profileSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentId = studentIdField.getText().toString().trim();
                String telegramHandle = teleHandleField.getText().toString().trim();

                if (!TextUtils.isEmpty(studentId) && !TextUtils.isEmpty(telegramHandle) && signInAccount != null) {
                    User newUser = new User( Integer.parseInt(studentId) , telegramHandle , signInAccount);
                    newUser.createUser();
                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(intent);
                }
            }
        });


//
        }











    };