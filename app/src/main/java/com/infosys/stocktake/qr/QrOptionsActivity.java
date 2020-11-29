package com.infosys.stocktake.qr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.infosys.stocktake.MainActivity;
import com.infosys.stocktake.R;
import com.infosys.stocktake.inventory.items.ItemDetailsActivity;
import com.infosys.stocktake.models.Item;

public class QrOptionsActivity extends AppCompatActivity {
    private Item item;
    private Button loanBtn, viewDetailsBtn;
    private TextView tvUnableToLoan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_options);

        // Instantiate UI Components
        loanBtn = findViewById(R.id.loanBtn);
        viewDetailsBtn = findViewById(R.id.viewDetailsBtn);
        tvUnableToLoan = findViewById(R.id.tvUnableToLoan);

        // Check if able to loan
        boolean canLoan = (boolean) getIntent().getSerializableExtra("canLoan");
        item = (Item) getIntent().getSerializableExtra("ItemIntent");

        // if unable to loan, disable button and display text
        if (!canLoan) {
            loanBtn.setVisibility(View.INVISIBLE);
            tvUnableToLoan.setVisibility(View.VISIBLE);
        }

        viewDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QrOptionsActivity.this, ItemDetailsActivity.class);
                intent.putExtra("ItemIntent", item);
                startActivity(intent);
            }
        });

        // TODO: Change to loans activity
        loanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QrOptionsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}