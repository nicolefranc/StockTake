package com.infosys.stocktake.inventory.items;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;

    private String currentClub;
    private User currentUser;
    private Boolean isEdit;
    private Item editItem;
    private Boolean imageChanged = false;

    // UI Components
    private Button imagePreview;
    private Button uploadBtn;
    private EditText etItemName;
    private EditText etItemDesc;
    private ElegantNumberButton etQty;
    private Switch swShare;
    Drawable savedImage;


    // Instance for Firebase Storage, Storage Reference
    FirebaseStorage storage;
    StorageReference storageReference;
    StockTakeFirebase<Item> itemFirebase = new StockTakeFirebase<>(Item.class, "items");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //Get intent and check whether is edit or create
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("edit", false);

        //Get club name
        currentClub = getIntent().getStringExtra("club");
        currentUser = (User) getIntent().getSerializableExtra("User");

        // Initialise Firebase Storage, Storage Reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialise UI Components
        imagePreview = findViewById(R.id.imgPreview);
        uploadBtn = findViewById(R.id.uploadBtn);
        etItemName = findViewById(R.id.editTextItemName);
        etItemDesc = findViewById(R.id.editTextItemDescription);
        etQty = findViewById(R.id.editTextQty);
        swShare = findViewById(R.id.sharingToggleSwitch);

        // Event Listeners
        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if edit mode is on, record image change
                if(isEdit){
                    imageChanged = true;
                }
                AddItemActivity.this.selectImage();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemActivity.this.uploadFile();
            }
        });

        etQty.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = etQty.getNumber();
            }
        });
        swShare.setChecked(false);
        swShare.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getBaseContext(),"sharing is "+ (swShare.isChecked() ? "on":"off"), Toast.LENGTH_SHORT).show();
            }
        });

        //if is in edit mode, restore details for easy editing
        if(isEdit){
            editItem = (Item) intent.getSerializableExtra("item");
            etItemDesc.setText(editItem.getItemDescription(), TextView.BufferType.EDITABLE);
            etItemName.setText(editItem.getItemName(), TextView.BufferType.EDITABLE);
            swShare.setChecked(editItem.getIsPublic());
            TextView title = findViewById(R.id.titleText);
            title.setText("Edit Item");
            uploadBtn.setText("Update Item");

            Integer quantity = 0;
            for(Map.Entry<String, Integer> entry : editItem.getQtyStatus().entrySet()){
                quantity += entry.getValue();
            }
            etQty.setNumber(quantity.toString());

            Uri imageUri = Uri.parse(editItem.getItemPicture());
            Thread setImage = new Thread(){
                @Override
                public void run(){
                    Bitmap image = null;
                    try {
                        image = Picasso.get().load(imageUri).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    savedImage= new BitmapDrawable(getResources(), image);
                }
            };
            setImage.start();
            try {
                setImage.join();
                imagePreview.setBackground(savedImage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
                imagePreview.setBackground(d);
                imagePreview.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: Add some validation
    private void uploadFile() {
        if (filePath != null || isEdit) {
            // Upload to Firebase Storage
            if(!isEdit || imageChanged) {
                final StorageReference ref = storageReference
                        .child("testingImages/" + System.currentTimeMillis());
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
                            Toast.makeText(AddItemActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                            // if mode is create or image was changed, need to update image url
                            if(!isEdit) {
                                AddItemActivity.this.addToFirestore(downloadUri.toString());
                            } else {
                                updateFireStore(downloadUri.toString());
                            }
                        } else {
                            Toast.makeText(AddItemActivity.this, "Upload FAILED", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                updateFireStore(editItem.getItemPicture());
            }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFirestore(String storageLocation){
        // Retrieve Item Details
        String itemName = etItemName.getText().toString();
        String itemDesc = etItemDesc.getText().toString();
        int qty = Integer.parseInt(etQty.getNumber());
        String clubID = currentClub;
        String loaneeID = null;
        boolean isPublic = swShare.isChecked();

        final Item item = new Item(itemName, itemDesc, storageLocation, qty, loaneeID, clubID, isPublic);
        Toast.makeText(AddItemActivity.this, "is public is: " + item.getIsPublic().toString(),Toast.LENGTH_SHORT);
        itemFirebase.create(item, item.getItemID());
        Intent intent = new Intent(AddItemActivity.this, ItemDetailsActivity.class);
        intent.putExtra("ItemIntent", item);
        intent.putExtra("isAdmin",true);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void updateFireStore(String storageLocation){
        editItem.setItemName(etItemName.getText().toString());
        editItem.setItemDesc(etItemDesc.getText().toString());
        editItem.setIsPublic(swShare.isChecked());
        editItem.setItemPicture(storageLocation);
        Integer quantity = 0;
        for(Map.Entry<String, Integer> entry : editItem.getQtyStatus().entrySet()){
            quantity += entry.getValue();
        }
        int newAvail = editItem.getQtyStatus().get("AVAILABLE") + (Integer.parseInt(etQty.getNumber()) - quantity);
        newAvail = newAvail<0 ? 0: newAvail;
        editItem.setQtyStatus("AVAILABLE", newAvail);
        itemFirebase.update(editItem, editItem.getItemID());
        Intent intent = new Intent(AddItemActivity.this, ItemDetailsActivity.class);
        intent.putExtra("ItemIntent", editItem);
        intent.putExtra("isAdmin",true);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}