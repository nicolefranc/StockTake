package com.infosys.stocktake.inventory.items;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.infosys.stocktake.R;

public class ViewQrActivity extends AppCompatActivity {
    ImageView ivQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qr);

        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("QRBitMap");

        ivQrCode = findViewById(R.id.ivQrCode);
        ivQrCode.setImageBitmap(bitmap);
    }
}