package com.infosys.stocktake.loans;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.Loan;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nonnull;


public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {
    private static final String TAG = "LOAN ADAPTER";

    private Context loanContext;
    private ArrayList<Loan> loanArrayList;
    private ArrayList<String> itemNames;
    private ArrayList<String> itemImages;
    private ArrayList<Integer> loanQuantities;
    private ArrayList<Date> loanDates;
    private boolean isEmpty;


    public LoanAdapter(Context loanContext, ArrayList<Loan> loanArrayList, ArrayList<String> itemNames, ArrayList<String> itemImages,
                       ArrayList<Integer> loanQuantities, ArrayList<Date> loanDates){
        this.loanContext = loanContext;
        this.loanArrayList = loanArrayList;
        this.itemNames = itemNames;
        this.itemImages = itemImages;
        this.loanQuantities = loanQuantities;
        this.loanDates = loanDates;
        Log.d(TAG,"Loan Adapter instantiated");
    }


    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (!isEmpty) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_loan, parent, false);
            LoanViewHolder viewHolder = new LoanViewHolder(view);
            Log.d(TAG,"View holder created");
            return viewHolder;
        } else {
            Log.d(TAG,"View holder created but empty");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item, parent, false);
            LoanViewHolder viewHolder = new LoanViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull LoanViewHolder holder, int position) {
        if(!isEmpty){
            Log.d(TAG,"Updating loans");
            holder.loanItemNameText.setText(itemNames.get(position));
            holder.loanQuantityText.setText(loanQuantities.get(position).toString());
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
            holder.loanDateText.setText(dateFormat.format(loanDates.get(position)));
            holder.loanIDText.setText(loanArrayList.get(position).getLoanID());

            String imageURL = itemImages.get(position);
            Glide.with(loanContext)
                    .load(imageURL)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.itemImage);

        }
        else{
            Log.d(TAG,"No loans found");
            holder.itemNameText.setText("Looks like there isn't anything here...");
            holder.itemNameText.setText("oops.");
            //can't find the ic_outline_help_outline_24
            holder.loanImage.setImageResource(R.drawable.ic_launcher_foreground);

        }
    }

    @Override
    public int getItemCount() {
        return loanArrayList.size();
    }
    public class LoanViewHolder extends RecyclerView.ViewHolder{
    //to populate actual loan history
    TextView loanItemNameText,loanQuantityText,loanDateText,loanIDText;
    ImageView itemImage,loanImage;

    //to populate empty loan history
    TextView itemNameText,itemDescriptionText;


    public LoanViewHolder(@NonNull View itemView) {
        super(itemView);
        loanImage = itemView.findViewById(R.id.loanImage);
        itemImage = itemView.findViewById(R.id.item_image);
        loanItemNameText = itemView.findViewById(R.id.loanItemName);
        loanQuantityText = itemView.findViewById(R.id.loanQuantity);
        loanDateText = itemView.findViewById(R.id.loanDate);
        loanIDText = itemView.findViewById(R.id.loanIDText);

        itemNameText = itemView.findViewById(R.id.item_name);
        itemDescriptionText = itemView.findViewById(R.id.item_description);
    }
}
}

