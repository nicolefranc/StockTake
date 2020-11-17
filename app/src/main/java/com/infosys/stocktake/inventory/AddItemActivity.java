package com.infosys.stocktake.inventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.infosys.stocktake.R;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.QrCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddItemActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private final int QR_WIDTH = 200;
    private final int QR_HEIGHT = 200;

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

        String documentId = item.getItemID();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").document(documentId)
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

//    public void generateQrCode(String data) {
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//
//        try {
//            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
//            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.RGB_565);
//
//            for (int x = 0; x < QR_WIDTH; x++) {
//                for (int y = 0; y < QR_HEIGHT; y++) {
//                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
//                }
//            }
//
//            // Convert to string to store in firebase
//            // Test the methods
//            String qrString = bitmapToString(bitmap);
//            Bitmap decodedBitmap = stringToBitmap(qrString);
//            imagePreview.setImageBitmap(decodedBitmap);
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
//    }

//    public String bitmapToString(Bitmap bitmap) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] b = baos.toByteArray();
//        String qrString = Base64.encodeToString(b, Base64.DEFAULT);
//
//        return qrString;
//    }

    /*
    * @param qrString (encoded string from bitmap
    * @return bitmap (decoded from the encoded string
    */
//    public Bitmap stringToBitmap(String qrString) {
//        try {
//            byte[] encodedByte = Base64.decode(qrString, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(encodedByte, 0, encodedByte.length);
//            return bitmap;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}