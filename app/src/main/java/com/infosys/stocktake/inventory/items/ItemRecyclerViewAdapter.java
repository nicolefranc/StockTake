package com.infosys.stocktake.inventory.items;

import android.content.Context;
import android.content.Intent;
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
import com.infosys.stocktake.R;
import com.infosys.stocktake.models.Item;

import java.util.ArrayList;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;
    private ArrayList<Item> mItems;
    private Boolean isEmpty;

    public ItemRecyclerViewAdapter(ArrayList<Item> items, Context context){
        mContext = context;
        mItems = items;
        Log.d(TAG, "recycler adapter initiated");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        if (!isEmpty) {
            Log.d(TAG, "onBindViewHolder: non-empty - " + mItems.size());
            String imageURL = mItems.get(position).getItemPicture();
            Glide.with(mContext)
                    .load(imageURL)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.itemImage);
//        holder.itemImage.setImageResource(R.drawable.ic_launcher_foreground);
            holder.itemDescription.setText(mItems.get(position).getItemDescription());
            holder.itemName.setText((mItems.get(position).getItemName()));

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ItemDetailsActivity.class);
                    intent.putExtra("ItemIntent", mItems.get(position));
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
        if (mItems.size() ==0){
            isEmpty = true;
            Log.d(TAG, "isEmpty = TRUE");
            return 1;
        }
        else{
            isEmpty = false;
            return mItems.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImage;
        TextView itemName;
        TextView itemDescription;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemDescription = itemView.findViewById(R.id.item_description);
            parentLayout = itemView.findViewById(R.id.item_parent_layout);
        }
    }
}
