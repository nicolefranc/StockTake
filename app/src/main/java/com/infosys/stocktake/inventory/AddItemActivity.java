package com.infosys.stocktake.inventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infosys.stocktake.R;
import com.infosys.stocktake.models.Item;

import java.io.IOException;

public class AddItemActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;

    // UI Components
    private Button selectImgBtn;
    private ImageView imagePreview;
    private Button uploadBtn;
    private EditText etItemName;
    private EditText etItemDesc;
    private EditText etQty;

    // Instance for Firebase Storage, Storage Reference
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialise Firebase Storage, Storage Reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialise UI Components
        selectImgBtn = findViewById(R.id.selectImgBtn);
        imagePreview = findViewById(R.id.imgPreview);
        uploadBtn = findViewById(R.id.uploadBtn);
        etItemName = findViewById(R.id.editTextItemName);
        etItemDesc = findViewById(R.id.editTextItemDescription);
        etQty = findViewById(R.id.editTextQty);

        // Event Listeners
        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemActivity.this.selectImage();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemActivity.this.uploadFile();
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
                imagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile() {
        if (filePath != null) {
            // Upload to Firebase Storage
            final StorageReference ref = storageReference
                    .child("testingImages/" + System.currentTimeMillis());
            UploadTask uploadTask = ref.putFile(filePath);
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
                                Toast.makeText(AddItemActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                AddItemActivity.this.addToFirestore(downloadUri.toString());
                            } else {
                                Toast.makeText(AddItemActivity.this, "Upload FAILED", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFirestore(String storageLocation) {
        // Retrieve Item Details
        String itemName = etItemName.getText().toString();
        String itemDesc = etItemDesc.getText().toString();
        int qty = Integer.parseInt(etQty.getText().toString());
        String loaneeID = "1233278";
        String clubID = "748379437";

        final Item item = new Item(itemName, itemDesc, storageLocation, qty, loaneeID, clubID);
//        item.insertItem();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").document(item.getClubID().concat("-" + item.getItemID().substring(0, 8)))
                .set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DEBUG", "Successfully added to Items");
                        System.out.println("YAAAAAYYYYYYYYYYYYYYY");

                        // Pass item to display details in ItemDetailsActivity
                        Intent intent = new Intent(AddItemActivity.this, ItemDetailsActivity.class);
                        intent.putExtra("ItemIntent", item);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DEBUG", "Failed to add to items");
                        System.out.println("NOOOOOOOOOOOO");
                    }
                });;

    }
}