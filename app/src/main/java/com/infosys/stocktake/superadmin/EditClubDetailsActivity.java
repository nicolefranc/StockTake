package com.infosys.stocktake.superadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;

import java.io.IOException;
import java.util.ArrayList;

import io.grpc.Context;

public class EditClubDetailsActivity extends AppCompatActivity {
    //Image stuff
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;

    //Static Elements
    public static String TAG = "Edit Club Details";

    //Elements
    private ImageButton backBtn;
    private ImageButton selectImageBtn;
    private Button saveChangesBtn;
    private Button deleteClubBtn;
    private TextView clubNameTV;
    private TextView adminEmailTV;
    private Club mClub;
    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StockTakeFirebase<Club> clubStockTakeFirebase;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fetch all the XML
        setContentView(R.layout.activity_edit_club);
        backBtn = findViewById(R.id.backBtn);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        deleteClubBtn = findViewById(R.id.deleteClubBtn);
        clubNameTV = findViewById(R.id.clubNameTV);
        adminEmailTV = findViewById(R.id.adminEmailTV);

        //Firebase setup
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        clubStockTakeFirebase = new StockTakeFirebase<>(Club.class, "clubs");

        //Prepopulate details
        Intent clubIntent = getIntent();
        mClub = (Club) clubIntent.getSerializableExtra("ClubIntent");
        clubNameTV.setText(mClub.getClubName());
        if (mClub.getAdminList() != null) {
            if (mClub.getAdminList().get(0) != null) {
                adminEmailTV.setText(mClub.getAdminList().get(0));
            }
        }
        Glide.with(this)
                .load(mClub.getClubImage())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(selectImageBtn);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(EditClubDetailsActivity.this, SuperadminClubViewActivity.class);
                startActivity(backIntent);
            }
        });

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 4a: update the entry (but only for club name, cos changing club admin has huge implications:
                //                                                  1. strip the current admin's privilege, 2. send email to the new admin)
                updateDetails();
            }
        });
        //TODO 5: Implement Delete Club button
        deleteClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 5a: Pop up a modal to confirm that user wants to delete the club
                String imageURI = mClub.getClubImage();
                clubStockTakeFirebase.delete(mClub.getClubID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        storageReference = storage.getReferenceFromUrl(imageURI);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditClubDetailsActivity.this, mClub.getClubName() + " has been successfully deleted", Toast.LENGTH_SHORT).show();
                                Intent backIntent = new Intent(EditClubDetailsActivity.this, SuperadminClubViewActivity.class);
                                startActivity(backIntent);
                            }
                        });
                    }
                });
            }
        });

    }

    private void selectImage() {
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
                Glide.with(this)
                        .load(d)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_background)
                        .into(selectImageBtn);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDetails() {
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
                        Toast.makeText(EditClubDetailsActivity.this, "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                        updateFirebase(downloadUri.toString());
                    } else {
                        Toast.makeText(EditClubDetailsActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            updateFirebase(null);
        }
    }

    private void updateFirebase(String storageLocation) {
        if (storageLocation != null) {
            storageReference = storage.getReferenceFromUrl(mClub.getClubImage());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    mClub.setClubImage(storageLocation);
                    mClub.setClubName(clubNameTV.getText().toString());
                    clubStockTakeFirebase.update(mClub, mClub.getClubID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditClubDetailsActivity.this, mClub.getClubName() + " has been successfully updated", Toast.LENGTH_SHORT).show();
                            Intent backIntent = new Intent(EditClubDetailsActivity.this, SuperadminClubViewActivity.class);
                            startActivity(backIntent);
                        }
                    });
                }
            });
        } else {
            mClub.setClubName(clubNameTV.getText().toString());
            clubStockTakeFirebase.update(mClub, mClub.getClubID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(EditClubDetailsActivity.this, mClub.getClubName() + " has been successfully updated", Toast.LENGTH_SHORT).show();
                    Intent backIntent = new Intent(EditClubDetailsActivity.this, SuperadminClubViewActivity.class);
                    startActivity(backIntent);
                }
            });
        }
    }
}
