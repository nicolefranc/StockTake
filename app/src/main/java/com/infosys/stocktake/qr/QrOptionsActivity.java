package com.infosys.stocktake.qr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.infosys.stocktake.R;
import com.infosys.stocktake.models.Item;

public class QrOptionsActivity extends AppCompatActivity {
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

        // if unable to loan, disable button and display text
        if (!canLoan) {
            loanBtn.setVisibility(View.INVISIBLE);
            tvUnableToLoan.setVisibility(View.VISIBLE);
        }
    }
}