package com.infosys.stocktake.inventory.items;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.itemloanhistory.ItemLoanHistoryActivity;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.QrCode;
import com.infosys.stocktake.models.User;
import com.squareup.picasso.Picasso;

public class ItemDetailsActivity extends AppCompatActivity {
    private static final String TAG = "inventory";
    Item item;
    private static final int QR_HEIGHT = 200;
    private static final int QR_WIDTH = 200;
    TextView tvItemName, tvQtyAvailable, tvQtyBroken, tvQtyOnLoan, tvQtyOnRepair, tvItemDesc, tvLastLoan;
    ImageView ivItemPicture, ivQrCode;
    Button tvLoanHistory;
    private boolean isAdmin;
    private StockTakeFirebase<User> stockTakeFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        item = (Item) getIntent().getSerializableExtra("ItemIntent");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        super.onCreate(savedInstanceState);

        if(isAdmin){
            //Initialize firebase
            stockTakeFirebase = new StockTakeFirebase<User>(User.class, "users");
            setContentView(R.layout.activity_item_details_admin);
            setLoanWidgets(item.getLoaneeID());

        }
        else{
            setContentView(R.layout.activity_item_details);
        }

        // Initialize UI Components
        tvItemName = findViewById(R.id.tvItemName);
        tvQtyAvailable = findViewById(R.id.tvQtyAvailable);
        tvQtyBroken = findViewById(R.id.tvQtyBroken);
        tvQtyOnLoan = findViewById(R.id.tvQtyOnLoan);
        tvQtyOnRepair = findViewById(R.id.tvQtyOnRepair);
        tvItemDesc = findViewById(R.id.tvItemDesc);
        ivItemPicture = findViewById(R.id.ivItemPicture);
        ivQrCode = findViewById(R.id.ivQrCode);

        Log.d(TAG, "Retrieving items...");
        // Populate components with Item data from passed Intent
        tvItemName.setText(item.getItemName());
        Log.d(TAG, item.getItemName());
        tvQtyAvailable.setText(item.getQtyStatus().get(ItemStatus.AVAILABLE.toString()).toString());
        tvQtyBroken.setText(item.getQtyStatus().get(ItemStatus.BROKEN.toString()).toString());
        tvQtyOnLoan.setText(item.getQtyStatus().get(ItemStatus.ON_LOAN.toString()).toString());
        tvQtyOnRepair.setText(item.getQtyStatus().get(ItemStatus.ON_REPAIR.toString()).toString());
        tvItemDesc.setText(item.getItemDescription());
        Log.d(TAG, "onCreate: isAdmin is " + isAdmin);

        // Display item image from the download url
        Uri imageUri = Uri.parse(item.getItemPicture());
        Picasso.get().load(imageUri)
                .fit().centerCrop().into(ivItemPicture);

        // Display QR code based on encoded QR string
        QrCode qr = new QrCode(QR_HEIGHT, QR_WIDTH);
        Bitmap bitmap = qr.stringToBitmap(item.getEncodedQr());
        ivQrCode.setImageBitmap(bitmap);
    }

    private void setLoanWidgets(String loaneeID) {
        if (loaneeID != null) {
            Task<User> getUserTask = stockTakeFirebase.query(loaneeID);
            getUserTask.addOnSuccessListener(new OnSuccessListener<User>() {
                @Override
                public void onSuccess(User user) {
                    String userName = user.getFullName();
                    tvLastLoan = findViewById(R.id.last_loan);
                    tvLoanHistory = findViewById(R.id.loan_history);
                    tvLastLoan.setText(userName);
                    tvLoanHistory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent loanHistoryIntent = new Intent(view.getContext(), ItemLoanHistoryActivity.class);
                            loanHistoryIntent.putExtra("itemIntent", item);
                            startActivity(loanHistoryIntent);
                        }
                    });
                }
            });
            getUserTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: failed to get user");
                    tvLastLoan = findViewById(R.id.loan_history);
                    tvLastLoan.setText("failed :(");
                }
            });
        }
    }
}