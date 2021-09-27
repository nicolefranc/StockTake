package com.infosys.stocktake.inventory.items;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infosys.stocktake.HomeActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.clubs.ViewClubActivity;
import com.infosys.stocktake.inventory.itemloanhistory.ItemLoanHistoryActivity;
//import com.infosys.stocktake.inventory.InventoryActivity;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.Loan;
import com.infosys.stocktake.models.QrCode;
import com.infosys.stocktake.models.User;
import com.squareup.picasso.Picasso;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemDetailsActivity extends AppCompatActivity {
    private static final String TAG = "inventory";
    Item item;
    Club club;
    private static final int QR_HEIGHT = 200;
    private static final int QR_WIDTH = 200;
    TextView tvItemName, tvQtyAvailable, tvQtyBroken, tvQtyOnLoan, tvQtyOnRepair, teleHandle;
    ScrollView tvItemDesc; //change from textview to scrollview -felia
    ImageView ivItemPicture, ivQrCode;
    PieChart pieChart;
    Button tvLoanHistory;
    Switch sharingSwitch;
    ImageButton editButton, deleteButton;
    private boolean isAdmin;
    private StockTakeFirebase<User> stockTakeFirebase = new StockTakeFirebase<User>(User.class, "users");
    private StockTakeFirebase<Loan> loanFirebase = new StockTakeFirebase<Loan>(Loan.class, "loans");
    private StockTakeFirebase<Item> itemFirebase = new StockTakeFirebase<>(Item.class, "items");

    private ArrayList<String> admins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        item = (Item) getIntent().getSerializableExtra("ItemIntent");
        club = (Club) getIntent().getSerializableExtra("club");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        Log.d(TAG, "ItemsDetsAct: " + isAdmin);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_pichart);

        if(isAdmin){
            //Initialize firebase
            setContentView(R.layout.activity_item_detail_pichart_admin);
            setLoanWidgets();
        }
        else{
            setContentView(R.layout.activity_item_detail_pichart);
            teleHandle = findViewById(R.id.telegramhandle);
            getAdmins();
        }

        // Initialize UI Components
        tvItemName = findViewById(R.id.tvItemName);
        tvQtyAvailable = findViewById(R.id.tvQtyAvailable);
        tvQtyBroken = findViewById(R.id.tvQtyBroken);
        tvQtyOnLoan = findViewById(R.id.tvQtyOnLoan);
        tvQtyOnRepair = findViewById(R.id.tvQtyOnRepair);
        tvItemDesc = findViewById(R.id.tvItemDescp);
        ivItemPicture = findViewById(R.id.ivItemPicture);
        ivQrCode = findViewById(R.id.ivQrCode);
        pieChart = findViewById(R.id.piechart);



        Log.d(TAG, "Retrieving items...");
        // Populate components with Item data from passed Intent

        item = (Item) getIntent().getSerializableExtra("ItemIntent");
        TextView tv = new TextView(this); //changes are here -felia
        tv.setText(item.getItemDescription());

        tvItemName.setText(item.getItemName());
        Log.d(TAG, item.getItemName());
        tvQtyAvailable.setText(item.getQtyStatus().get(ItemStatus.AVAILABLE.toString()).toString());
        tvQtyBroken.setText(item.getQtyStatus().get(ItemStatus.BROKEN.toString()).toString());
        tvQtyOnLoan.setText(item.getQtyStatus().get(ItemStatus.ON_LOAN.toString()).toString());
        tvQtyOnRepair.setText(item.getQtyStatus().get(ItemStatus.ON_REPAIR.toString()).toString());
        Log.d(TAG, "onCreate: isAdmin is " + isAdmin);

        tvItemDesc.addView(tv); //and here -felia

        if(isAdmin) {
            sharingSwitch = findViewById(R.id.itemShareSw);
            editButton = findViewById(R.id.editButton);
            deleteButton = findViewById(R.id.deleteButton);

            // set sharing switch depending on sharing status
            sharingSwitch.setChecked(item.getIsPublic());
            sharingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setIsPublic(isChecked);
                    itemFirebase.update(item, item.getItemID());
                    Toast.makeText(ItemDetailsActivity.this, "Sharing " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT);
                }
            });

            //set listener for edit button
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIntent = new Intent(ItemDetailsActivity.this, AddItemActivity.class);
                    editIntent.putExtra("edit", true);
                    editIntent.putExtra("item", item);
                    startActivity(editIntent);
                }
            });


            //set listener for delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ItemDetailsActivity.this)
                            .setTitle("Delete Item")
                            .setMessage("Are you sure you want to delete this item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    itemFirebase.delete(item.getItemID());
                                    Toast.makeText(ItemDetailsActivity.this, item.getItemName() + " has been deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ItemDetailsActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        }

        // Display item image from the download url
        Uri imageUri = Uri.parse(item.getItemPicture());
        Picasso.get().load(imageUri)
                .fit().centerInside().into(ivItemPicture);

        // Display QR code based on encoded QR string
        QrCode qr = new QrCode(QR_HEIGHT, QR_WIDTH);
        Bitmap bitmap = qr.stringToBitmap(item.getEncodedQr());
        ivQrCode.setImageBitmap(bitmap);
        ivQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btnViewQrImage", "clicked");
//                Intent intent = new Intent(getApplicationContext(), ViewQrActivity.class);
//                intent.putExtra("QRBitMap", bitmap);
//                intent.putExtra("ItemName", item.getItemName());
//                startActivity(intent);


                try {
                    Toast.makeText(ItemDetailsActivity.this , "Downloading QR code... ", Toast.LENGTH_SHORT).show();
                    Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas c = new Canvas(bmp);
                    String text = item.getItemName();
                    Paint p = new Paint();
                    p.setTypeface(Typeface.DEFAULT);
                    if (item.getItemName().length() > 20) {
                        p.setTextSize(12);
                    } else {
                        p.setTextSize(18);
                    }
                    p.setColor(getResources().getColor(R.color.black));
                    int width = (int) p.measureText(text);
//                    int yPos = (int) ((c.getHeight() / 2)
//                            - ((p.descent() + p.ascent()) / 2) - 10);
                    int yPos = (int) ((c.getHeight()) - ((p.descent() + p.ascent()) / 2) - 10);
                    c.drawText(text, (bmp.getWidth() - width) / 2, yPos, p);
                    saveBitmap(getApplicationContext(), bmp, Bitmap.CompressFormat.PNG, "image/jpeg", item.getItemName());

                    Toast.makeText(ItemDetailsActivity.this, "QR Downloaded", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setData();
    }

    private void saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
                            @NonNull final Bitmap.CompressFormat format, @NonNull final String mimeType,
                            @NonNull final String displayName) throws IOException
    {
        final String relativeLocation = Environment.DIRECTORY_PICTURES;

        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);

        final ContentResolver resolver = context.getContentResolver();

        OutputStream stream = null;
        Uri uri = null;

        try
        {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, contentValues);

            if (uri == null)
            {
                throw new IOException("Failed to create new MediaStore record.");
            }

            stream = resolver.openOutputStream(uri);

            if (stream == null)
            {
                throw new IOException("Failed to get output stream.");
            }

            if (bitmap.compress(format, 95, stream) == false)
            {
                throw new IOException("Failed to save bitmap.");
            }
        }
        catch (IOException e)
        {
            if (uri != null)
            {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }
    private void setData()
    {


        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "Available",
                        Integer.parseInt(item.getQtyStatus().get(ItemStatus.AVAILABLE.toString()).toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Broken",
                        Integer.parseInt(item.getQtyStatus().get(ItemStatus.BROKEN.toString()).toString()),
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        "On Repair",
                        Integer.parseInt(item.getQtyStatus().get(ItemStatus.ON_LOAN.toString()).toString()),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Loan",
                        Integer.parseInt(item.getQtyStatus().get(ItemStatus.ON_REPAIR.toString()).toString()),
                        Color.parseColor("#707070")));

        // To animate the pie chart
        pieChart.startAnimation();
    }

    private void setLoanWidgets() {
            Task<ArrayList<Loan>> getLoanTask = loanFirebase.compoundQuery("itemID", item.getItemID());
            getLoanTask.addOnSuccessListener(new OnSuccessListener<ArrayList<Loan>>() {
                @Override
                public void onSuccess(ArrayList<Loan> loans) {
                        if(loans != null) {
                            Log.d(TAG, "onSuccess: Found user! Setting loan widgets...");
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
                        else{
                            Log.d(TAG, "onSuccess: did not find loanee");
                            no_loaneee();
                        }
                }
            });
            getLoanTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Did not find loanee");
                    no_loaneee();
                }
            });
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
        if(club!=null) {
            Intent clubIntent = new Intent(ItemDetailsActivity.this, ViewClubActivity.class);
            clubIntent.putExtra("ClubIntent", club);
            startActivity(clubIntent);
        } else {
            Intent invIntent = new Intent(ItemDetailsActivity.this, HomeActivity.class);
            startActivity(invIntent);
        }
    }
}
