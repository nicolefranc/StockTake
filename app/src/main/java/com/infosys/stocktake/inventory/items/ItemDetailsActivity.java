package com.infosys.stocktake.inventory.items;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.infosys.stocktake.R;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.QrCode;
import com.squareup.picasso.Picasso;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class ItemDetailsActivity extends AppCompatActivity {
    private static final String TAG = "inventory";
    Item item;
    private static final int QR_HEIGHT = 200;
    private static final int QR_WIDTH = 200;
    TextView tvItemName, tvQtyAvailable, tvQtyBroken, tvQtyOnLoan, tvQtyOnRepair;
    ScrollView tvItemDesc; //change from textview to scrollview -felia
    ImageView ivItemPicture, ivQrCode;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_pichart);

        // Initialize UI Components
        tvItemName = findViewById(R.id.tvItemName);
        tvQtyAvailable = findViewById(R.id.tvQtyAvailable);
        tvQtyBroken = findViewById(R.id.tvQtyBroken);
        tvQtyOnLoan = findViewById(R.id.tvQtyOnLoan);
        tvQtyOnRepair = findViewById(R.id.tvQtyOnRepair);
        tvItemDesc = findViewById(R.id.tvItemDesc);
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

        tvItemDesc.addView(tv); //and here -felia


        // Display item image from the download url
        Uri imageUri = Uri.parse(item.getItemPicture());
        Picasso.get().load(imageUri)
                .fit().centerCrop().into(ivItemPicture);

        // Display QR code based on encoded QR string
        QrCode qr = new QrCode(QR_HEIGHT, QR_WIDTH);
        Bitmap bitmap = qr.stringToBitmap(item.getEncodedQr());
        ivQrCode.setImageBitmap(bitmap);

        setData();
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
}