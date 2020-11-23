package com.infosys.stocktake.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.infosys.stocktake.R;
import com.infosys.stocktake.inventory.InventoryActivity;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TabLayout tabLayout;
    private TextInputEditText emailField;
    private TextInputEditText pwField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.emailField);
        pwField = findViewById(R.id.pwField);
//        registerButton = findViewById(R.id.registerBtn);
//
//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                register();
//            }
//        });

//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                login();
//            }
//        });
    }

        public void register () {
            String email = emailField.getText().toString().trim();
            String pw = pwField.getText().toString().trim();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pw)) {
                mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // TODO: Add info to db
                            Toast.makeText(LoginActivity.this, "User created.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Aw man. Some error occurred.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        public void login () {
            Intent intent = new Intent(this, GoogleLoginActivity.class);
            startActivity(intent);
        }

//    // TODO: add in validation of input
//    public int validate(String email) {
//        return 0;
//    };
    }
