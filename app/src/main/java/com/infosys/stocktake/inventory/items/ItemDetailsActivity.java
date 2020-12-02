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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.itemloanhistory.ItemLoanHistoryActivity;
import com.infosys.stocktake.inventory.InventoryActivity;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.Loan;
import com.infosys.stocktake.models.QrCode;
import com.infosys.stocktake.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class ItemDetailsActivity extends AppCompatActivity {
    private static final String TAG = "inventory";
    Item item;
    private static final int QR_HEIGHT = 200;
    private static final int QR_WIDTH = 200;
    TextView tvItemName, tvQtyAvailable, tvQtyBroken, tvQtyOnLoan, tvQtyOnRepair, tvItemDesc, tvLastLoan, teleHandle;
    ImageView ivItemPicture, ivQrCode;
    Button tvLoanHistory;
    private boolean isAdmin;
    private StockTakeFirebase<User> stockTakeFirebase = new StockTakeFirebase<User>(User.class, "users");
    private StockTakeFirebase<Loan> loanFirebase = new StockTakeFirebase<Loan>(Loan.class, "loans");

    private ArrayList<String> admins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        item = (Item) getIntent().getSerializableExtra("ItemIntent");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        super.onCreate(savedInstanceState);

        if(isAdmin){
            //Initialize firebase
            setContentView(R.layout.activity_item_details_admin);
            setLoanWidgets(item.getLoaneeID());

        }
        else{
            setContentView(R.layout.activity_item_details);
            teleHandle = findViewById(R.id.telegramhandle);
            getAdmins();
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
            Task<ArrayList<Loan>> getLoanTask = loanFirebase.compoundQuery("item_ID", item.getItemID());
            getLoanTask.addOnSuccessListener(new OnSuccessListener<ArrayList<Loan>>() {
                @Override
                public void onSuccess(ArrayList<Loan> loans) {
                    try {
                        tvLoanHistory = findViewById(R.id.loan_history);
                        tvLoanHistory.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent loanHistoryIntent = new Intent(view.getContext(), ItemLoanHistoryActivity.class);
                                loanHistoryIntent.putExtra("itemIntent", item);
                                startActivity(loanHistoryIntent);
                            }
                        });
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        no_loaneee();
                    }
                }
            });
            getLoanTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    no_loaneee();
                }
            });
        }
        else{
            no_loaneee();
        }
    }

    private void no_loaneee(){
        Log.d(TAG, "onFailure: failed to get user");
        tvLoanHistory = findViewById(R.id.loan_history);
        tvLoanHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "No loan history for this item!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: TOAST SHOULD BE HERE");
            }
        });
    }

    private void getAdmins(){
        String club = item.getClubID();
        HashMap <String,String> adminHash = new HashMap<String,String>();
        adminHash.put(club,"ADMIN");
                Task<ArrayList<User>> getAdminTask = stockTakeFirebase.compoundQuery("clubMembership", adminHash);
        getAdminTask.addOnSuccessListener(new OnSuccessListener<ArrayList<User>>() {
            @Override
            public void onSuccess(ArrayList<User> users) {
                if(users != null) {
                    Log.d(TAG, "onSuccess: Admins are " + users.toString());
                    for (User user : users) {
                        if (user.getClubMembership().containsKey(club)) {
                            admins.add(user.getTelegramHandle());

                        }
                    }
                    String adminList = "";
                    for(String admin: admins){
                        if(admin.charAt(0) != '@'){
                            admin = "@" + admin;
                        }
                        adminList = adminList + admin + "\n";
                    }

                    teleHandle.setText(adminList);
                }
                else{
                    teleHandle.setText("this club has no admins!");
                }
            }
        });
        getAdminTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                teleHandle.setText("this club has no admins!");
            }
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent itemIntent = new Intent(ItemDetailsActivity.this, InventoryActivity.class);
        startActivity(itemIntent);
    }
}
