package com.infosys.stocktake.loans;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Item;
import com.infosys.stocktake.models.Loan;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {
    StockTakeFirebase<Item> stockTakeFirebaseItem = new StockTakeFirebase<>(Item.class,"items");
    LayoutInflater loanInflater;
    private ArrayList<Loan> loanArrayList;
    private Context context;

    public LoanAdapter(Context context, ArrayList<Loan> loanArrayList){
        this.context = context;
        this.loanArrayList = loanArrayList;
        loanInflater = LayoutInflater.from(context);
    }

    static class LoanViewHolder extends RecyclerView.ViewHolder{
        ImageView loanImage;
        TextView loanItemNameText;

        public LoanViewHolder(@NonNull View itemView) {
            super(itemView);
            loanImage = itemView.findViewById(R.id.loanImage);
            loanItemNameText = itemView.findViewById(R.id.loanItemNameText);
        }
    }

    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = loanInflater.inflate(R.layout.card_loan, viewGroup, false);
        return new LoanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanViewHolder loanViewHolder, int position) {
        String itemID = (loanArrayList.get(position).getItemID());
        stockTakeFirebaseItem.query(itemID).addOnSuccessListener(new OnSuccessListener<Item>() {
            @Override
            public void onSuccess(Item item) {
                loanViewHolder.loanItemNameText.setText(item.getItemName());
                Uri imageUri = Uri.parse(item.getItemPicture());
                Picasso.get().load(imageUri)
                .fit().centerCrop().into(loanViewHolder.loanImage);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
