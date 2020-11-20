package com.infosys.stocktake.loans;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infosys.stocktake.R;
import com.infosys.stocktake.models.Loan;


public class AddLoanActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_add);

    }
}
