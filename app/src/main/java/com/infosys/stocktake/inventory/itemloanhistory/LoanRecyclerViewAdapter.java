package com.infosys.stocktake.inventory.itemloanhistory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.items.ItemDetailsActivity;
import com.infosys.stocktake.loans.LoanDetailsActivity;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.Loan;
import com.infosys.stocktake.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoanRecyclerViewAdapter extends RecyclerView.Adapter<LoanRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;
    private ArrayList<Loan> mLoans;
    private ArrayList<String> clubNames = new ArrayList<String>();
    private ArrayList<String> userNames = new ArrayList<String>();
    private ArrayList<Date> dueDates = new ArrayList<Date>();
    private ArrayList<Date> returnDates = new ArrayList<Date>();
    private Boolean isEmpty;
    private boolean isAdmin;


    public LoanRecyclerViewAdapter(ArrayList<Loan> loans, ArrayList<String> clubs, ArrayList<String> users, ArrayList<Date> duedates, ArrayList<Date> returndates, Context context){
        mContext = context;
        mLoans = loans;
        clubNames = clubs;
        userNames = users;
        dueDates = duedates;
        returnDates = returndates;

        Log.d(TAG, "recycler adapter initiated");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (!isEmpty) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item,parent,false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        if (!isEmpty) {
            Log.d(TAG, "onBindViewHolder: non-empty - " + mLoans.size());
            // setting text
            holder.clubName.setText(clubNames.get(position));
            holder.loaneeName.setText(userNames.get(position));
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
            holder.returnDate.setText(dateFormat.format(dueDates.get(position)));
            //removing checkmark if item is still due
            Date now = new Date();
            if(returnDates.get(position) == null){
                holder.checkmark.setVisibility(View.GONE);
            }
            else{
                holder.clubName.setTextColor(Color.GRAY);
                holder.loaneeName.setTextColor(Color.GRAY);
            }

            if(returnDates.get(position) == null && dueDates.get(position).before(now)){
                holder.clubName.setTextColor(Color.RED);
                holder.loaneeName.setTextColor(Color.RED);
            }

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LoanDetailsActivity.class);
                    intent.putExtra("LOAN_ID_INTENT", mLoans.get(position).getLoanID());
                    mContext.startActivity(intent);
                    Log.d(TAG, "tapped");
                }
            });
        }
        else{
            Log.d(TAG, "onBindViewHolder: setting empty message");
            holder.itemDescription.setText("Looks like there isn't anything here...");
            holder.itemName.setText("oops.");
            holder.itemImage.setImageResource(R.drawable.ic_outline_help_outline_24);
        }
    }

    @Override
    public int getItemCount() {
        if (mLoans.size() ==0){
            isEmpty = true;
            Log.d(TAG, "isEmpty = TRUE");
            return 1;
        }
        else{
            isEmpty = false;
            return mLoans.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImage;
        TextView itemName;
        TextView itemDescription;
        TextView clubName;
        TextView loaneeName;
        TextView returnDate;
        ImageView checkmark;

        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemDescription = itemView.findViewById(R.id.item_description);
            parentLayout = itemView.findViewById(R.id.item_parent_layout);
            clubName = itemView.findViewById(R.id.club_name);
            loaneeName = itemView.findViewById(R.id.loanee_name);
            returnDate = itemView.findViewById(R.id.return_date);
            checkmark = itemView.findViewById(R.id.checkmark);
        }
    }


}
