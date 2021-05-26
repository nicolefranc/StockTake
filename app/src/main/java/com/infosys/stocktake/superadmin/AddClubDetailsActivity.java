package com.infosys.stocktake.superadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.User;

import java.io.IOException;
import java.util.ArrayList;

public class AddClubDetailsActivity extends AppCompatActivity {
    private final String TAG = "Add Club Details";
    private boolean IMAGE_FLAG = false;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;


    private ImageButton backBtn;
    private Button selectImageBtn;
    private Button addClubBtn;
    private TextView clubNameTV;
    private TextView adminEmailTV;

    FirebaseStorage storage;
    StorageReference storageReference;
    StockTakeFirebase<Club> clubStockTakeFirebase;
    StockTakeFirebase<User> userStockTakeFirebase;
    FirebaseAuth fbAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fetch all XML elements
        setContentView(R.layout.activity_create_club);
        backBtn = findViewById(R.id.backBtn);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        addClubBtn = findViewById(R.id.addClubBtn);
        clubNameTV = findViewById(R.id.clubNameTV);
        adminEmailTV = findViewById(R.id.adminEmailTV);

        // Firebase setup
        clubStockTakeFirebase = new StockTakeFirebase<>(Club.class,"clubs");
        userStockTakeFirebase = new StockTakeFirebase<>(User.class,"users");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fbAuth = FirebaseAuth.getInstance();

        //Image select intent
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        //Add club button listener
        addClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adminEmail = adminEmailTV.getText().toString();
                fbAuth.fetchSignInMethodsForEmail(adminEmail).addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                        boolean userExists = !signInMethodQueryResult.getSignInMethods().isEmpty();
                        Log.d(TAG, "onSuccess: Sign in methods: "+signInMethodQueryResult.getSignInMethods().toString());

                        //If email exists, add club to the DB
                        if(userExists){
                            //TODO: Send an email to the club admin, if admin uses the link, they will be added to the club admin list
                            uploadFile();
                        }
                        else{
                            Toast.makeText(AddClubDetailsActivity.this, "User with email "+adminEmail+" is not registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(AddClubDetailsActivity.this,SuperadminClubViewActivity.class);
                startActivity(backIntent);
            }
        });
    }
    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select image here..."),
                PICK_IMAGE_REQUEST
        );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                // Convert the image to bitmap to show in ImageView component
                Bitmap bitmap = MediaStore
                                    .Images
                                    .Media
                                    .getBitmap(getContentResolver(), filePath);

                Drawable d = new BitmapDrawable(getResources(), bitmap);
                selectImageBtn.setBackground(d);
                selectImageBtn.setText("");
                IMAGE_FLAG = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile() {
        if (filePath != null) {
            // Upload to Firebase Storage
            final StorageReference ref = storageReference
                    .child("clubImages/" + System.currentTimeMillis());
            UploadTask uploadTask = ref.putFile(filePath);
            // Retrieve the download url for the image uploaded to Firebase Storage
            // Download url is to be used to store in Firestore and to display later using Picasso
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Toast.makeText(AddClubDetailsActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                addToFirestore(downloadUri.toString());
                            } else {
                                Toast.makeText(AddClubDetailsActivity.this, "Upload FAILED", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFirestore(String storageLocation){
        // Retrieve Item Details
        String clubName = clubNameTV.getText().toString();
        clubStockTakeFirebase.getCollection().addOnSuccessListener(new OnSuccessListener<ArrayList<Club>>() {
            @Override
            public void onSuccess(ArrayList<Club> clubs) {
                int count = clubs.size();
                String clubID = "CLB-"+ count;
                Club club = new Club(clubID,clubName);
                if(IMAGE_FLAG){
                    club.setClubImage(storageLocation);
                }
                clubStockTakeFirebase.create(club,clubID).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(AddClubDetailsActivity.this, SuperadminClubViewActivity.class);
                        Toast.makeText(AddClubDetailsActivity.this, clubName+" has been successfully created", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
