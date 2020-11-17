package com.infosys.stocktake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infosys.stocktake.MainActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.auth.GoogleLoginActivity;

import com.infosys.stocktake.models.User;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Profile extends AppCompatActivity {
    TextView nameView, telegramView;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutButton = findViewById(R.id.logoutButton);
        nameView = findViewById(R.id.name);
        telegramView = findViewById(R.id.telegramhandle);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        if (signInAccount  != null) {
            nameView.setText(signInAccount.getDisplayName());
            telegramView.setText(signInAccount.getEmail());
        }

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


//        class getUser extends AsyncTask<> {
//
//            @Override
//            protected User doInBackground() {
//                User customUser = new User().getUser();
//
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                if (customUser != null) {
//                    name.setText(customUser.getStudentID());
//                    email.setText(customUser.getTelegramHandle());
//                }
//            }
//        }

    }



}