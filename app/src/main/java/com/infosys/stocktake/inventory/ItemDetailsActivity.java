package com.infosys.stocktake.inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.infosys.stocktake.R;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.squareup.picasso.Picasso;

public class ItemDetailsActivity extends AppCompatActivity {
    Item item;
    TextView tvItemName, tvQtyAvailable, tvQtyBroken, tvQtyOnLoan, tvQtyOnRepair, tvItemDesc;
    ImageView ivItemPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Initialize UI Components
        tvItemName = findViewById(R.id.tvItemName);
        tvQtyAvailable = findViewById(R.id.tvQtyAvailable);
        tvQtyBroken = findViewById(R.id.tvQtyBroken);
        tvQtyOnLoan = findViewById(R.id.tvQtyOnLoan);
        tvQtyOnRepair = findViewById(R.id.tvQtyOnRepair);
        tvItemDesc = findViewById(R.id.tvItemDesc);
        ivItemPicture = findViewById(R.id.ivItemPicture);

        // Populate components with Item data from passed Intent
        item = (Item) getIntent().getSerializableExtra("ItemIntent");
        tvItemName.setText(item.getItemName());
        tvQtyAvailable.setText(item.getQtyStatus().get(ItemStatus.AVAILABLE.toString()).toString());
        tvQtyBroken.setText(item.getQtyStatus().get(ItemStatus.BROKEN.toString()).toString());
        tvQtyOnLoan.setText(item.getQtyStatus().get(ItemStatus.ON_LOAN.toString()).toString());
        tvQtyOnRepair.setText(item.getQtyStatus().get(ItemStatus.ON_REPAIR.toString()).toString());
        tvItemDesc.setText(item.getItemDescription());

        Uri imageUri = Uri.parse(item.getItemPicture());
        Picasso.get().load(imageUri)
                .fit().centerCrop().into(ivItemPicture);
    }
}