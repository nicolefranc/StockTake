package com.infosys.stocktake.inventory.items;

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

import com.infosys.stocktake.R;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.ItemStatus;
import com.infosys.stocktake.models.QrCode;
import com.squareup.picasso.Picasso;

public class ItemDetailsActivity extends AppCompatActivity {
    Item item;
    private static final int QR_HEIGHT = 200;
    private static final int QR_WIDTH = 200;
    TextView tvItemName, tvQtyAvailable, tvQtyBroken, tvQtyOnLoan, tvQtyOnRepair, tvItemDesc;
    ImageView ivItemPicture, ivQrCode;
    Button btnViewQrImage;
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
        ivQrCode = findViewById(R.id.ivQrCode);
        btnViewQrImage = findViewById(R.id.btnViewQrImage);

        // Populate components with Item data from passed Intent
        item = (Item) getIntent().getSerializableExtra("ItemIntent");
        tvItemName.setText(item.getItemName());
        tvQtyAvailable.setText(item.getQtyStatus().get(ItemStatus.AVAILABLE.toString()).toString());
        tvQtyBroken.setText(item.getQtyStatus().get(ItemStatus.BROKEN.toString()).toString());
        tvQtyOnLoan.setText(item.getQtyStatus().get(ItemStatus.ON_LOAN.toString()).toString());
        tvQtyOnRepair.setText(item.getQtyStatus().get(ItemStatus.ON_REPAIR.toString()).toString());
        tvItemDesc.setText(item.getItemDescription());

        // Display item image from the download url
        Uri imageUri = Uri.parse(item.getItemPicture());
        Picasso.get().load(imageUri)
                .fit().centerCrop().into(ivItemPicture);

        // Display QR code based on encoded QR string
        QrCode qr = new QrCode(QR_HEIGHT, QR_WIDTH);
        Bitmap bitmap = qr.stringToBitmap(item.getEncodedQr());
//        ivQrCode.setImageBitmap(bitmap);

        btnViewQrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("btnViewQrImage", "clicked");
                Intent intent = new Intent(getApplicationContext(), ViewQrActivity.class);
                intent.putExtra("QRBitMap", bitmap);
                startActivity(intent);
            }
        });
    }


}